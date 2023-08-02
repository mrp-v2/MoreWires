package mrp_v2.morewires.item;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class AdjustedRedstoneItem extends BlockItem
{
    private final TagKey<Item> dyeTag;

    public AdjustedRedstoneItem(Block blockIn, Properties builder, TagKey<Item> dyeTag)
    {
        super(blockIn, builder);
        this.dyeTag = dyeTag;
    }

    public TagKey<Item> getDyeTag()
    {
        return dyeTag;
    }
}
