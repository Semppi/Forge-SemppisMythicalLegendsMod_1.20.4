//package net.semppi.semppis_mythical_legends_mod.commands;
//
//import net.minecraft.commands.Commands;
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.chat.Component;
//import net.minecraftforge.event.RegisterCommandsEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
//import net.semppi.semppis_mythical_legends_mod.world.Region;
//import net.semppi.semppis_mythical_legends_mod.world.RegionSampler;
//
//@Mod.EventBusSubscriber(modid = SemppisMythicalLegendsMod.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
//public final class RegionDebugCommands {
//    private RegionDebugCommands() {}
//
//    @SubscribeEvent
//    public static void onRegisterCommands(RegisterCommandsEvent e) {
//        e.getDispatcher().register(
//                Commands.literal("smlwhere")
//                        .requires(src -> src.hasPermission(2)) // optional
//                        .executes(ctx -> {
//                            var src = ctx.getSource();
//                            var sl = src.getLevel();
//                            var pos = BlockPos.containing(src.getPosition());
//
//                            var sampler = new RegionSampler();
//                            Region r = sampler.landRegion(sl, pos.getX(), pos.getZ());
//
//                            String msg = r.ocean()
//                                    ? "Region: OCEAN/" + r.sea() + " at " + pos
//                                    : "Region: " + r.continent() + " " + r.dir() + " at " + pos;
//
//                            src.sendSuccess(() -> Component.literal(msg), false);
//                            return 1;
//                        })
//        );
//    }
//}