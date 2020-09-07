package mrp_v2.computercomponents.util;

import mrp_v2.computercomponents.block.AltRedstoneWireBlock;
import mrp_v2.computercomponents.block.InfiniwireBlock;
import net.minecraft.item.BlockItem;

public class ObjectHolder
{
    public static final AltRedstoneWireBlock ALT_REDSTONE_WIRE_BLOCK;
    public static final BlockItem ALT_REDSTONE_BLOCK_ITEM;
    public static final InfiniwireBlock INFINIWIRE_BLOCK;
    public static final BlockItem INFINIWIRE_BLOCK_ITEM;

    static
    {
        ALT_REDSTONE_WIRE_BLOCK = new AltRedstoneWireBlock();
        ALT_REDSTONE_BLOCK_ITEM = ALT_REDSTONE_WIRE_BLOCK.createBlockItem();
        INFINIWIRE_BLOCK = new InfiniwireBlock();
        INFINIWIRE_BLOCK_ITEM = INFINIWIRE_BLOCK.createBlockItem();
    }
}
