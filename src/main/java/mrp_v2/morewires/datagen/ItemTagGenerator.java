package mrp_v2.morewires.datagen;

import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.mrplibrary.datagen.providers.BlockTagsProvider;
import mrp_v2.mrplibrary.datagen.providers.ItemTagsProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class ItemTagGenerator extends ItemTagsProvider {
    public ItemTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, BlockTagsProvider blockTagProvider, String modId,
                            @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTagProvider.contentsGetter(), modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        TagsProvider.TagAppender<Item> wiresTagBuilder = this.tag(ObjectHolder.WIRES_TAG_KEY);
        TagsProvider.TagAppender<Item> infiniwiresTagBuilder = this.tag(ObjectHolder.INFINIWIRES_TAG_KEY);
        for (String color : ObjectHolder.COLORS.keySet()) {
            wiresTagBuilder.add(cast(ObjectHolder.WIRE_BLOCK_ITEMS.get(color).getKey()));
            infiniwiresTagBuilder.add(cast(ObjectHolder.INFINIWIRE_BLOCK_ITEMS.get(color).getKey()));
        }
        wiresTagBuilder.addOptionalTag(Tags.Items.DUSTS_REDSTONE.location());
    }

    private <T> ResourceKey<T> cast(ResourceKey<? extends T> key) {
        return (ResourceKey<T>) key;
    }
}
