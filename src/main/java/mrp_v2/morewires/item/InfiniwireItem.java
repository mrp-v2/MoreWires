package mrp_v2.morewires.item;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.Tag;

public class InfiniwireItem extends AdjustedRedstoneItem
{
    public InfiniwireItem(Block blockIn, Properties builder, Tag<Item> dyeTag)
    {
        super(blockIn, builder, dyeTag);
    }

    @Override public boolean hasEffect(ItemStack stack)
    {
        return true;
    }
}
