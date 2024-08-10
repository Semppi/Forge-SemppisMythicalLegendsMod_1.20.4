package net.semppi.semppis_mythical_legends_mod.datagen;

import net.minecraftforge.client.model.generators.ModelFile;
import net.semppi.semppis_mythical_legends_mod.SemppisMythicalLegendsMod;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import net.semppi.semppis_mythical_legends_mod.block.ModBlocks;

//public class ModBlockStateProvider extends BlockStateProvider {
//
//    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
//        super(output, SemppisMythicalLegendsMod.MOD_ID, exFileHelper);
//    }
//
//    @Override
//    protected void registerStatesAndModels() {
//        simpleBlockWithItem(ModBlocks.WENDIGO_SKULL.get(),
//                new ModelFile.UncheckedModelFile(modLoc("block/wendigo_skull")));
//    }
//}