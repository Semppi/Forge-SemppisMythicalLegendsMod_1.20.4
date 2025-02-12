package net.semppi.semppis_mythical_legends_mod;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.semppi.semppis_mythical_legends_mod.block.ModBlocks;
import net.semppi.semppis_mythical_legends_mod.block.client.PukisEggRenderer;
import net.semppi.semppis_mythical_legends_mod.block.client.WendigoSkullRenderer;
import net.semppi.semppis_mythical_legends_mod.block.entity.ModBlockEntities;
import net.semppi.semppis_mythical_legends_mod.commands.CancelTransformCommand;
import net.semppi.semppis_mythical_legends_mod.commands.TransformCommand;
import net.semppi.semppis_mythical_legends_mod.datagen.DataGenerators;
import net.semppi.semppis_mythical_legends_mod.entity.EntitySpawnHandler;
import net.semppi.semppis_mythical_legends_mod.entity.client.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.semppi.semppis_mythical_legends_mod.entity.ModEntities;
import net.semppi.semppis_mythical_legends_mod.entity.custom.AlicantoEntity;
import net.semppi.semppis_mythical_legends_mod.event.*;
import net.semppi.semppis_mythical_legends_mod.item.ModCreativeModeTabs;
import net.semppi.semppis_mythical_legends_mod.item.ModItems;
import net.semppi.semppis_mythical_legends_mod.loot.ModLootModifiers;
import net.semppi.semppis_mythical_legends_mod.sound.ModSounds;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;

@Mod(SemppisMythicalLegendsMod.MOD_ID)
public class SemppisMythicalLegendsMod {
    public static final String MOD_ID = "semppis_mythical_legends_mod";
    private static final Logger LOGGER = LogManager.getLogger();

    public SemppisMythicalLegendsMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MinecraftForge.EVENT_BUS.register(new DismountEventHandler());
        ModCreativeModeTabs.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModLootModifiers.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModEntities.register(modEventBus);
        ModSounds.register(modEventBus);
        modEventBus.addListener(DataGenerators::gatherData);

        MinecraftForge.EVENT_BUS.addListener(this::onRegisterCommands);
        MinecraftForge.EVENT_BUS.register(new TransformationEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerTeleportHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerRenderHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerInputEventHandler());

        modEventBus.addListener(this::commonSetup);
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(EntitySpawnHandler.class);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        GeckoLib.initialize();
        SpawnPlacements.register(ModEntities.SATYR.get(),
                SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.BEHEMOTH.get(),
                SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.PUKIS.get(),
                SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.WENDIGO.get(),
                SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.LOVELAND_FROGMAN.get(),
                SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.COLOSSAL_LOBSTER.get(),
                SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WaterAnimal::checkSurfaceWaterAnimalSpawnRules);
        SpawnPlacements.register(ModEntities.ALICANTO.get(),
                SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                AlicantoEntity::canAlicantoSpawn);
        SpawnPlacements.register(ModEntities.PROTO_WENDIGO.get(),
                SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                Animal::checkAnimalSpawnRules);
    }

    private void addCreative(final BuildCreativeModeTabContentsEvent event) {
        // Creative tab contents code here
    }

    // Use RegisterCommandsEvent instead of FMLServerStartingEvent
    private void onRegisterCommands(RegisterCommandsEvent event) {
        TransformCommand.register(event.getDispatcher());
        CancelTransformCommand.register(event.getDispatcher());
    }

    @Mod.EventBusSubscriber(modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            EntityRenderers.register(ModEntities.ALICANTO.get(), AlicantoRenderer::new);
            EntityRenderers.register(ModEntities.BEHEMOTH.get(), BehemothRenderer::new);
            EntityRenderers.register(ModEntities.COLOSSAL_LOBSTER.get(), ColossalLobsterRenderer::new);
            EntityRenderers.register(ModEntities.KRAKEN.get(), KrakenRenderer::new);
            EntityRenderers.register(ModEntities.LOVELAND_FROGMAN.get(), LovelandFrogmanRenderer::new);
            EntityRenderers.register(ModEntities.MALPHAS.get(), MalphasRenderer::new);
            EntityRenderers.register(ModEntities.MANDRAKE.get(), MandrakeRenderer::new);
            EntityRenderers.register(ModEntities.PROTO_WENDIGO.get(), ProtoWendigoRenderer::new);
            EntityRenderers.register(ModEntities.PUKIS.get(), PukisRenderer::new);
            EntityRenderers.register(ModEntities.SATYR.get(), SatyrRenderer::new);
            EntityRenderers.register(ModEntities.WENDIGO.get(), WendigoRenderer::new);

            event.enqueueWork(() -> {
                BlockEntityRenderers.register(ModBlockEntities.WENDIGO_SKULL_BLOCK_ENTITY.get(), WendigoSkullRenderer::new);
                BlockEntityRenderers.register(ModBlockEntities.PUKIS_EGG_BLOCK_ENTITY.get(), PukisEggRenderer::new);
            });
        }
    }
}