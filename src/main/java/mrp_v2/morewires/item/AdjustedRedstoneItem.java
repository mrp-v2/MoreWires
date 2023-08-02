package mrp_v2.morewires.item;

import net.minecraft.tags.Tag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

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
