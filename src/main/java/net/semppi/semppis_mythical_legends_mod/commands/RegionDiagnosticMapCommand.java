package net.semppi.semppis_mythical_legends_mod.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.semppi.semppis_mythical_legends_mod.spawn.RegionGate;
import net.semppi.semppis_mythical_legends_mod.world.Continent;
import net.semppi.semppis_mythical_legends_mod.world.Ocean;
import net.semppi.semppis_mythical_legends_mod.world.Region;
import net.semppi.semppis_mythical_legends_mod.world.RegionSurfaceClassifier;
import net.semppi.semppis_mythical_legends_mod.world.SubDir;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

/** Exports a large no-chunk-load overview of the resolved region overlay. */
public final class RegionDiagnosticMapCommand {
    private static final int DEFAULT_RADIUS = 4_096;
    private static final int DEFAULT_STEP = 64;
    private static final int MAX_PIXELS_PER_SIDE = 513;
    private static final AtomicBoolean RUNNING = new AtomicBoolean(false);
    private static final ExecutorService WORKER = Executors.newSingleThreadExecutor(task -> {
        Thread thread = new Thread(task, "sml-region-map");
        thread.setDaemon(true);
        return thread;
    });

    private static final int[] LAND_BASE = {
            0xD29A43, // Africa
            0xE7EEF4, // Antarctica
            0x9A63C7, // Asia
            0x4C9B63, // Europe
            0xD75A4A, // North America
            0x43A89B, // South America
            0xD8B643  // Australia
    };

    private static final int[] OCEAN_COLORS = {
            0xA8DDF0, // Arctic
            0x438FD0, // North Atlantic
            0x2C809C, // South Atlantic
            0x596FB6, // Indian
            0x5EA8E5, // North Pacific
            0x397F7C, // South Pacific
            0xC08AA4  // Southern
    };

    private RegionDiagnosticMapCommand() {}

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("smlregionmap")
                        .requires(source -> source.hasPermission(2))
                        .executes(context -> start(
                                context.getSource(), DEFAULT_RADIUS, DEFAULT_STEP
                        ))
                        .then(Commands.argument(
                                        "radius", IntegerArgumentType.integer(512, 16_384)
                                )
                                .executes(context -> start(
                                        context.getSource(),
                                        IntegerArgumentType.getInteger(context, "radius"),
                                        DEFAULT_STEP
                                ))
                                .then(Commands.argument(
                                                "step", IntegerArgumentType.integer(32, 512)
                                        )
                                        .executes(context -> start(
                                                context.getSource(),
                                                IntegerArgumentType.getInteger(context, "radius"),
                                                IntegerArgumentType.getInteger(context, "step")
                                        ))
                                )
                        )
        );
    }

    private static int start(CommandSourceStack source, int radius, int step) {
        if (source.getLevel().dimension() != Level.OVERWORLD) {
            source.sendFailure(Component.literal(
                    "Region maps are available only in the Overworld."
            ));
            return 0;
        }

        if (radius % step != 0) {
            source.sendFailure(Component.literal(
                    "The radius must be evenly divisible by the sample step "
                            + "so the command position remains the exact center pixel."
            ));
            return 0;
        }

        int size = radius / step * 2 + 1;
        if (size > MAX_PIXELS_PER_SIDE) {
            source.sendFailure(Component.literal(
                    "That map would be " + size + " pixels wide. Increase the step "
                            + "or reduce the radius (maximum 513 pixels per side)."
            ));
            return 0;
        }
        if (!RUNNING.compareAndSet(false, true)) {
            source.sendFailure(Component.literal(
                    "A continental diagnostic map is already being generated."
            ));
            return 0;
        }

        ServerLevel level = source.getLevel();
        MinecraftServer server = source.getServer();
        BlockPos center = BlockPos.containing(source.getPosition());
        source.sendSuccess(
                () -> Component.literal(
                        "Generating a " + size + "x" + size
                                + " continental map covering "
                                + (radius * 2) + "x" + (radius * 2)
                                + " blocks. No distant chunks will be loaded."
                ),
                false
        );

        WORKER.execute(() -> {
            try {
                Path image = generate(level, server, center, radius, step, size);
                server.execute(() -> source.sendSuccess(
                        () -> Component.literal(
                                "Continental map complete: "
                                        + server.getWorldPath(LevelResource.ROOT)
                                        .relativize(image)
                        ),
                        false
                ));
            } catch (Exception exception) {
                server.execute(() -> source.sendFailure(Component.literal(
                        "Continental map failed: " + exception.getMessage()
                )));
            } finally {
                RUNNING.set(false);
            }
        });
        return 1;
    }

    private static Path generate(ServerLevel level, MinecraftServer server,
                                 BlockPos center, int radius, int step, int size)
            throws IOException {
        BufferedImage image = new BufferedImage(
                size, size, BufferedImage.TYPE_INT_RGB
        );
        int startX = center.getX() - radius;
        int startZ = center.getZ() - radius;

        for (int pixelZ = 0; pixelZ < size; pixelZ++) {
            int worldZ = startZ + pixelZ * step;
            for (int pixelX = 0; pixelX < size; pixelX++) {
                int worldX = startX + pixelX * step;
                RegionSurfaceClassifier.Sample sample =
                        RegionGate.resolveGenerated(level, worldX, worldZ);
                image.setRGB(pixelX, pixelZ, color(sample));
            }
        }

        // Mark the command position without hiding its surrounding region.
        int middle = size / 2;
        image.setRGB(middle, middle, 0xFF0000);
        if (middle > 0) {
            image.setRGB(middle - 1, middle, 0xFF0000);
            image.setRGB(middle, middle - 1, 0xFF0000);
        }
        if (middle + 1 < size) {
            image.setRGB(middle + 1, middle, 0xFF0000);
            image.setRGB(middle, middle + 1, 0xFF0000);
        }

        Path directory = server.getWorldPath(LevelResource.ROOT)
                .resolve("debug")
                .resolve("sml_region_maps");
        Files.createDirectories(directory);
        String baseName = "region-map-x" + center.getX()
                + "-z" + center.getZ()
                + "-r" + radius
                + "-s" + step;
        Path imagePath = directory.resolve(baseName + ".png");
        Path legendPath = directory.resolve(baseName + "-legend.txt");

        if (!ImageIO.write(image, "png", imagePath.toFile())) {
            throw new IOException("No PNG image writer is available.");
        }
        Files.writeString(
                legendPath,
                legend(center, radius, step, size),
                StandardCharsets.UTF_8
        );
        return imagePath;
    }

    private static int color(RegionSurfaceClassifier.Sample sample) {
        Region region = sample.region();
        int color = region.ocean()
                ? OCEAN_COLORS[region.sea().ordinal()]
                : landColor(region.continent(), region.dir());

        return switch (sample.kind()) {
            case RIVER -> mix(color, 0x246FD1, 0.42);
            case COAST -> mix(color, 0x55D4D0, 0.32);
            default -> color;
        };
    }

    private static int landColor(Continent continent, SubDir direction) {
        int base = LAND_BASE[continent.ordinal()];
        if (continent == Continent.ANTARCTICA) return base;

        return switch (direction) {
            case NORTH -> mix(base, 0xDDEEFF, 0.30);
            case EAST -> mix(base, 0x8FD36B, 0.22);
            case SOUTH -> mix(base, 0x7A1F22, 0.24);
            case WEST -> mix(base, 0xF4A340, 0.25);
            case CENTRAL -> mix(base, 0x8A56A8, 0.20);
        };
    }

    private static int mix(int first, int second, double amount) {
        Color a = new Color(first);
        Color b = new Color(second);
        double keep = 1.0 - amount;
        int red = (int) Math.round(a.getRed() * keep + b.getRed() * amount);
        int green = (int) Math.round(a.getGreen() * keep + b.getGreen() * amount);
        int blue = (int) Math.round(a.getBlue() * keep + b.getBlue() * amount);
        return (red << 16) | (green << 8) | blue;
    }

    private static String legend(BlockPos center, int radius, int step, int size) {
        StringBuilder text = new StringBuilder();
        text.append("Semppi's Mythical Legends continental diagnostic map\n")
                .append("Center X/Z: ").append(center.getX()).append("/")
                .append(center.getZ()).append('\n')
                .append("Radius: ").append(radius).append(" blocks\n")
                .append("Sample step: ").append(step).append(" blocks per pixel\n")
                .append("Image: ").append(size).append("x").append(size).append(" pixels\n")
                .append("North is up; the red cross is the command position.\n")
                .append("Rivers are tinted blue and coasts cyan.\n\n");

        for (Continent continent : Continent.values()) {
            if (continent == Continent.ANTARCTICA) {
                appendLegend(text, Region.land(continent, SubDir.CENTRAL));
            } else {
                for (SubDir direction : SubDir.values()) {
                    appendLegend(text, Region.land(continent, direction));
                }
            }
        }
        for (Ocean ocean : Ocean.values()) {
            appendLegend(text, Region.sea(ocean));
        }
        return text.toString();
    }

    private static void appendLegend(StringBuilder text, Region region) {
        int color = region.ocean()
                ? OCEAN_COLORS[region.sea().ordinal()]
                : landColor(region.continent(), region.dir());
        text.append(String.format(
                Locale.ROOT, "#%06X = %s%n", color, region.display()
        ));
    }
}
