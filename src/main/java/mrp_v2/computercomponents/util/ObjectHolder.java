package mrp_v2.computercomponents.util;

import mrp_v2.computercomponents.block.AltRedstoneWireBlock;
import mrp_v2.computercomponents.block.InfiniwireBlock;
import net.minecraft.item.BlockItem;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;

public class ObjectHolder
{
    public static final AltRedstoneWireBlock ALT_REDSTONE_WIRE_BLOCK;
    public static final BlockItem ALT_REDSTONE_BLOCK_ITEM;
    public static final InfiniwireBlock[] INFINIWIRE_BLOCKS;
    public static final BlockItem[] INFINIWIRE_BLOCK_ITEMS;
    public static final ArrayList<Pair<Float, String>> COLORS = new ArrayList<>();

    static
    {
        COLORS.add(Pair.of(2.0F / 3.0F, "blue"));
        COLORS.add(Pair.of(1.0F / 3.0F, "green"));
        COLORS.add(Pair.of(1.0F / 12.0F, "orange"));
        COLORS.add(Pair.of(1.0F / 6.0F, "yellow"));
        COLORS.add(Pair.of(5.0F / 6.0F, "pink"));
        ALT_REDSTONE_WIRE_BLOCK = new AltRedstoneWireBlock(0);
        ALT_REDSTONE_BLOCK_ITEM = ALT_REDSTONE_WIRE_BLOCK.createBlockItem();
        INFINIWIRE_BLOCKS = new InfiniwireBlock[COLORS.size()];
        INFINIWIRE_BLOCK_ITEMS = new BlockItem[COLORS.size()];
        int i = 0;
        for (Pair<Float, String> color : COLORS)
        {
            INFINIWIRE_BLOCKS[i] = new InfiniwireBlock(color.getLeft(), color.getRight());
            INFINIWIRE_BLOCK_ITEMS[i] = INFINIWIRE_BLOCKS[i].createBlockItem();
            i++;
        }
    }
}
