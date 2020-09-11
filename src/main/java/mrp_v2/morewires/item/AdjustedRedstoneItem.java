package mrp_v2.morewires.item;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;

public class AdjustedRedstoneItem extends BlockItem
{
    private final ITag<Item> dyeTag;

    public AdjustedRedstoneItem(Block blockIn, Properties builder, ITag<Item> dyeTag)
    {
        super(blockIn, builder);
        this.dyeTag = dyeTag;
    }

    public ITag<Item> getDyeTag()
    {
        return dyeTag;
    }
}
