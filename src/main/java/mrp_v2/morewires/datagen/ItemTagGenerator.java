package mrp_v2.morewires.datagen;

import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.mrplibrary.datagen.providers.ItemTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;

public class ItemTagGenerator extends ItemTagsProvider {
    public ItemTagGenerator(DataGenerator dataGenerator, BlockTagsProvider blockTagProvider, String modId,
                            @Nullable ExistingFileHelper existingFileHelper) {
        super(dataGenerator, blockTagProvider, modId, existingFileHelper);
    }

    @Override
    protected void addTags() {
        TagsProvider.TagAppender<Item> wiresTagBuilder = this.tag(ObjectHolder.WIRES_TAG_KEY);
        TagsProvider.TagAppender<Item> infiniwiresTagBuilder = this.tag(ObjectHolder.INFINIWIRES_TAG_KEY);
        for (String color : ObjectHolder.COLORS.keySet()) {
            wiresTagBuilder.add(ObjectHolder.WIRE_BLOCK_ITEMS.get(color).get());
            infiniwiresTagBuilder.add(ObjectHolder.INFINIWIRE_BLOCK_ITEMS.get(color).get());
        }
        wiresTagBuilder.addOptionalTag(Tags.Items.DUSTS_REDSTONE.location());
    }
}
