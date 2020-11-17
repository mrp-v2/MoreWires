package mrp_v2.morewires.datagen;

import mrp_v2.morewires.MoreWires;
import mrp_v2.morewires.block.AdjustedRedstoneWireBlock;
import mrp_v2.morewires.block.InfiniwireBlock;
import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.morewires.util.Util;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

public class BlockStateGenerator extends BlockStateProvider
{
    private final ExistingFileHelper existingFileHelper;

    public BlockStateGenerator(DataGenerator gen, String modId, ExistingFileHelper exFileHelper)
    {
        super(gen, modId, exFileHelper);
        this.existingFileHelper = exFileHelper;
    }

    @Override protected void registerStatesAndModels()
    {
        Pair<String, String> particleDot = Pair.of("particle", "dot");
        Pair<String, String> line0 = Pair.of("line", "line0");
        Pair<String, String> line1 = Pair.of("line", "line1");
        registerModel("dot", particleDot, Pair.of("line", "dot"));
        registerModel("side", particleDot);
        registerModel("side0", line0);
        registerModel("side1", line1);
        registerModel("side_alt", particleDot);
        registerModel("side_alt0", line0);
        registerModel("side_alt1", line1);
        registerModel("up", particleDot, line0);
        registerWireStates();
        registerInfiniwireStates();
    }

    @SafeVarargs private final void registerModel(String suffix, Pair<String, String>... textures)
    {
        ModelBuilder<BlockModelBuilder> modelBuilder = this.models()
                .withExistingParent(MoreWires.ID + ":block/infiniwire_" + suffix,
                        "minecraft:block/redstone_dust_" + suffix);
        for (Pair<String, String> texture : textures)
        {
            modelBuilder = modelBuilder.texture(texture.getLeft(),
                    Util.makeResourceLocation("block", "infiniwire_" + texture.getRight()));
        }
    }

    private void registerWireStates()
    {
        for (RegistryObject<AdjustedRedstoneWireBlock> block : ObjectHolder.WIRE_BLOCKS_EXCLUDING_REDSTONE.values())
        {
            registerWireBasedStates(block.get(), "redstone_dust", true);
        }
    }

    private void registerInfiniwireStates()
    {
        for (RegistryObject<InfiniwireBlock> block : ObjectHolder.INFINIWIRE_BLOCKS.values())
        {
            registerWireBasedStates(block.get(), "infiniwire", false);
        }
    }

    private void registerWireBasedStates(AdjustedRedstoneWireBlock wireBasedBlock, String stem, boolean vanillaModel)
    {
        ModelFile.ExistingModelFile dotModel = getModel(vanillaModel, "block/" + stem + "_dot");
        ModelFile.ExistingModelFile upModel = getModel(vanillaModel, "block/" + stem + "_up");
        ModelFile.ExistingModelFile side0Model = getModel(vanillaModel, "block/" + stem + "_side0");
        ModelFile.ExistingModelFile side1Model = getModel(vanillaModel, "block/" + stem + "_side1");
        ModelFile.ExistingModelFile sideAlt0Model = getModel(vanillaModel, "block/" + stem + "_side_alt0");
        ModelFile.ExistingModelFile sideAlt1Model = getModel(vanillaModel, "block/" + stem + "_side_alt1");
        this.getMultipartBuilder(wireBasedBlock)
                .part()
                .modelFile(dotModel)
                .addModel()
                .condition(RedstoneWireBlock.NORTH, RedstoneSide.NONE)
                .condition(RedstoneWireBlock.SOUTH, RedstoneSide.NONE)
                .condition(RedstoneWireBlock.EAST, RedstoneSide.NONE)
                .condition(RedstoneWireBlock.WEST, RedstoneSide.NONE)
                .end()
                .part()
                .modelFile(dotModel)
                .addModel()
                .condition(RedstoneWireBlock.NORTH, RedstoneSide.SIDE, RedstoneSide.UP)
                .condition(RedstoneWireBlock.EAST, RedstoneSide.SIDE, RedstoneSide.UP)
                .end()
                .part()
                .modelFile(dotModel)
                .addModel()
                .condition(RedstoneWireBlock.SOUTH, RedstoneSide.SIDE, RedstoneSide.UP)
                .condition(RedstoneWireBlock.EAST, RedstoneSide.SIDE, RedstoneSide.UP)
                .end()
                .part()
                .modelFile(dotModel)
                .addModel()
                .condition(RedstoneWireBlock.SOUTH, RedstoneSide.SIDE, RedstoneSide.UP)
                .condition(RedstoneWireBlock.WEST, RedstoneSide.SIDE, RedstoneSide.UP)
                .end()
                .part()
                .modelFile(dotModel)
                .addModel()
                .condition(RedstoneWireBlock.NORTH, RedstoneSide.SIDE, RedstoneSide.UP)
                .condition(RedstoneWireBlock.WEST, RedstoneSide.SIDE, RedstoneSide.UP)
                .end()
                .part()
                .modelFile(side0Model)
                .addModel()
                .condition(RedstoneWireBlock.NORTH, RedstoneSide.SIDE, RedstoneSide.UP)
                .end()
                .part()
                .modelFile(sideAlt0Model)
                .addModel()
                .condition(RedstoneWireBlock.SOUTH, RedstoneSide.SIDE, RedstoneSide.UP)
                .end()
                .part()
                .modelFile(sideAlt1Model)
                .rotationY(270)
                .addModel()
                .condition(RedstoneWireBlock.EAST, RedstoneSide.SIDE, RedstoneSide.UP)
                .end()
                .part()
                .modelFile(side1Model)
                .rotationY(270)
                .addModel()
                .condition(RedstoneWireBlock.WEST, RedstoneSide.SIDE, RedstoneSide.UP)
                .end()
                .part()
                .modelFile(upModel)
                .addModel()
                .condition(RedstoneWireBlock.NORTH, RedstoneSide.UP)
                .end()
                .part()
                .modelFile(upModel)
                .rotationY(90)
                .addModel()
                .condition(RedstoneWireBlock.EAST, RedstoneSide.UP)
                .end()
                .part()
                .modelFile(upModel)
                .rotationY(180)
                .addModel()
                .condition(RedstoneWireBlock.SOUTH, RedstoneSide.UP)
                .end()
                .part()
                .modelFile(upModel)
                .rotationY(270)
                .addModel()
                .condition(RedstoneWireBlock.WEST, RedstoneSide.UP)
                .end();
    }

    private ModelFile.ExistingModelFile getModel(boolean vanilla, String path)
    {
        ResourceLocation location = vanilla ? mcLoc(path) : modLoc(path);
        return new ModelFile.ExistingModelFile(location, this.existingFileHelper);
    }
}
