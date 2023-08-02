package mrp_v2.morewires.item;

import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

public class InfiniwireItem extends AdjustedRedstoneItem
{
    public InfiniwireItem(Block blockIn, Properties builder, Tag<Item> dyeTag)
    {
        super(blockIn, builder, dyeTag);
    }

    @Override
    public boolean isFoil(ItemStack stack)
    {
        return true;
    }
}
