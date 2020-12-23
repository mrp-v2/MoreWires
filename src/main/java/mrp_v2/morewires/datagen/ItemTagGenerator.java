package mrp_v2.morewires.datagen;

import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.mrplibrary.datagen.providers.ItemTagsProvider;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.TagsProvider;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class ItemTagGenerator extends ItemTagsProvider
{
    public ItemTagGenerator(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, String modId,
            @Nullable ExistingFileHelper existingFileHelper)
    {
        super(dataGenerator, blockTagProvider, modId, existingFileHelper);
    }

    @Override protected void registerTags()
    {
        TagsProvider.Builder<Item> wiresTagBuilder = this.getOrCreateBuilder(ObjectHolder.WIRES_TAG);
        TagsProvider.Builder<Item> infiniwiresTagBuilder = this.getOrCreateBuilder(ObjectHolder.INFINIWIRES_TAG);
        for (String color : ObjectHolder.COLORS.keySet())
        {
            wiresTagBuilder.add(ObjectHolder.WIRE_BLOCK_ITEMS.get(color).get());
            infiniwiresTagBuilder.add(ObjectHolder.INFINIWIRE_BLOCK_ITEMS.get(color).get());
        }
        wiresTagBuilder.addOptionalTag(new ResourceLocation(Tags.Items.DUSTS_REDSTONE.getName().toString()));
    }
}
