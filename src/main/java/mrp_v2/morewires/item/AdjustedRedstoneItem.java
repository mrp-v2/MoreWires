package mrp_v2.morewires.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tags.Tag;

public class AdjustedRedstoneItem extends BlockItem
{
    private final Tag<Item> dyeTag;

    public AdjustedRedstoneItem(Block blockIn, Properties builder, Tag<Item> dyeTag)
    {
        super(blockIn, builder);
        this.dyeTag = dyeTag;
    }

    public Tag<Item> getDyeTag()
    {
        return dyeTag;
    }
}
