package mrp_v2.morewires.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ITag;

public class InfiniwireItem extends AdjustedRedstoneItem
{
    public InfiniwireItem(Block blockIn, Properties builder, ITag<Item> dyeTag)
    {
        super(blockIn, builder, dyeTag);
    }

    @Override
    public boolean isFoil(ItemStack stack)
    {
        return true;
    }
}
