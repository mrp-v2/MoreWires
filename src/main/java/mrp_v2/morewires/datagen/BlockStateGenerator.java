package mrp_v2.morewires.datagen;

import mrp_v2.morewires.MoreWires;
import mrp_v2.morewires.block.AdjustedRedstoneWireBlock;
import mrp_v2.morewires.block.InfiniwireBlock;
import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.mrplibrary.datagen.providers.BlockStateProvider;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.data.DataGenerator;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

public class BlockStateGenerator extends BlockStateProvider
{
    protected ModelFile dotModel, upModel, side0Model, side1Model, sideAlt0Model, sideAlt1Model;

    public BlockStateGenerator(DataGenerator gen, String modId, ExistingFileHelper exFileHelper)
    {
        super(gen, modId, exFileHelper);
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

    @SafeVarargs protected final void registerModel(String suffix, Pair<String, String>... textures)
    {
        ModelBuilder<BlockModelBuilder> modelBuilder = this.models()
                .withExistingParent(MoreWires.ID + ":block/infiniwire_" + suffix,
                        "minecraft:block/redstone_dust_" + suffix);
        for (Pair<String, String> texture : textures)
        {
            modelBuilder.texture(texture.getLeft(), modLoc("block/infiniwire_" + texture.getRight()));
        }
    }

    protected void registerWireStates()
    {
        setupWireModels();
        for (RegistryObject<AdjustedRedstoneWireBlock> block : ObjectHolder.WIRE_BLOCKS_EXCLUDING_REDSTONE.values())
        {
            registerWireBasedStates(block.get());
        }
    }

    protected void setupWireModels()
    {
        dotModel = models().getExistingFile(new ResourceLocation("block/redstone_dust_dot"));
        upModel = models().getExistingFile(new ResourceLocation("block/redstone_dust_up"));
        side0Model = models().getExistingFile(new ResourceLocation("block/redstone_dust_side0"));
        side1Model = models().getExistingFile(new ResourceLocation("block/redstone_dust_side1"));
        sideAlt0Model = models().getExistingFile(new ResourceLocation("block/redstone_dust_side_alt0"));
        sideAlt1Model = models().getExistingFile(new ResourceLocation("block/redstone_dust_side_alt1"));
    }

    protected void registerWireBasedStates(AdjustedRedstoneWireBlock wireBasedBlock)
    {
        this.getMultipartBuilder(wireBasedBlock)
                // no connections or connections on different axes
                .part().modelFile(dotModel).addModel().useOr().nestedGroup()
                .condition(RedstoneWireBlock.NORTH, RedstoneSide.NONE)
                .condition(RedstoneWireBlock.SOUTH, RedstoneSide.NONE)
                .condition(RedstoneWireBlock.EAST, RedstoneSide.NONE)
                .condition(RedstoneWireBlock.WEST, RedstoneSide.NONE).end().nestedGroup().nestedGroup().useOr()
                .condition(RedstoneWireBlock.NORTH, RedstoneSide.SIDE, RedstoneSide.UP)
                .condition(RedstoneWireBlock.SOUTH, RedstoneSide.SIDE, RedstoneSide.UP).endNestedGroup().nestedGroup()
                .useOr().condition(RedstoneWireBlock.EAST, RedstoneSide.SIDE, RedstoneSide.UP)
                .condition(RedstoneWireBlock.WEST, RedstoneSide.SIDE, RedstoneSide.UP).endNestedGroup().end().end()
                //
                .part().modelFile(side0Model).addModel()
                .condition(RedstoneWireBlock.NORTH, RedstoneSide.SIDE, RedstoneSide.UP).end()
                //
                .part().modelFile(sideAlt0Model).addModel()
                .condition(RedstoneWireBlock.SOUTH, RedstoneSide.SIDE, RedstoneSide.UP).end()
                //
                .part().modelFile(sideAlt1Model).rotationY(270).addModel()
                .condition(RedstoneWireBlock.EAST, RedstoneSide.SIDE, RedstoneSide.UP).end()
                //
                .part().modelFile(side1Model).rotationY(270).addModel()
                .condition(RedstoneWireBlock.WEST, RedstoneSide.SIDE, RedstoneSide.UP).end()
                //
                .part().modelFile(upModel).addModel().condition(RedstoneWireBlock.NORTH, RedstoneSide.UP).end()
                //
                .part().modelFile(upModel).rotationY(90).addModel().condition(RedstoneWireBlock.EAST, RedstoneSide.UP)
                .end()
                //
                .part().modelFile(upModel).rotationY(180).addModel().condition(RedstoneWireBlock.SOUTH, RedstoneSide.UP)
                .end()
                //
                .part().modelFile(upModel).rotationY(270).addModel().condition(RedstoneWireBlock.WEST, RedstoneSide.UP)
                .end();
    }

    protected void registerInfiniwireStates()
    {
        setupInfiniwireModels();
        for (RegistryObject<InfiniwireBlock> block : ObjectHolder.INFINIWIRE_BLOCKS.values())
        {
            registerWireBasedStates(block.get());
        }
    }

    protected void setupInfiniwireModels()
    {
        dotModel = models().getExistingFile(new ResourceLocation(MoreWires.ID, "block/infiniwire_dot"));
        upModel = models().getExistingFile(new ResourceLocation(MoreWires.ID, "block/infiniwire_up"));
        side0Model = models().getExistingFile(new ResourceLocation(MoreWires.ID, "block/infiniwire_side0"));
        side1Model = models().getExistingFile(new ResourceLocation(MoreWires.ID, "block/infiniwire_side1"));
        sideAlt0Model = models().getExistingFile(new ResourceLocation(MoreWires.ID, "block/infiniwire_side_alt0"));
        sideAlt1Model = models().getExistingFile(new ResourceLocation(MoreWires.ID, "block/infiniwire_side_alt1"));
    }
}
