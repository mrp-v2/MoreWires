package mrp_v2.morewires.datagen;

import mrp_v2.morewires.MoreWires;
import mrp_v2.morewires.util.Util;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockModelBuilder;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.tuple.Pair;

public class BlockStateGenerator extends BlockStateProvider
{
    public BlockStateGenerator(DataGenerator gen, ExistingFileHelper exFileHelper)
    {
        super(gen, MoreWires.ID, exFileHelper);
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
    }

    @Override public String getName()
    {
        return super.getName() + ": " + MoreWires.ID;
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
}
