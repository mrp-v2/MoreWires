package mrp_v2.morewires.datagen;

import mrp_v2.morewires.MoreWires;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockModelGenerator extends BlockModelProvider
{
    public BlockModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper)
    {
        super(generator, MoreWires.ID, existingFileHelper);
    }

    @Override protected void registerModels()
    {
        
    }

    @Override public String getName()
    {
        return super.getName() + ": " + MoreWires.ID;
    }
}
