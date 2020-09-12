package mrp_v2.morewires.datagen;

import mrp_v2.morewires.MoreWires;
import mrp_v2.morewires.util.ObjectHolder;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class ItemTagGenerator extends ItemTagsProvider
{
    public ItemTagGenerator(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider,
            @Nullable ExistingFileHelper existingFileHelper)
    {
        super(dataGenerator, blockTagProvider, MoreWires.ID, existingFileHelper);
    }

    @Override protected void registerTags()
    {
        this.getOrCreateBuilder(ObjectHolder.WIRES_TAG)
                .add(ObjectHolder.WIRE_BLOCK_ITEMS)
                .addOptionalTag(new ResourceLocation(Tags.Items.DUSTS_REDSTONE.getName().toString()));
        this.getOrCreateBuilder(ObjectHolder.INFINIWIRES_TAG).add(ObjectHolder.INFINIWIRE_BLOCK_ITEMS);
    }

    @Override public String getName()
    {
        return super.getName() + ": " + MoreWires.ID;
    }
}
