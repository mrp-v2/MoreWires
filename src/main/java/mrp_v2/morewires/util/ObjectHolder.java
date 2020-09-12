package mrp_v2.morewires.util;

import mrp_v2.morewires.block.AdjustedRedstoneWireBlock;
import mrp_v2.morewires.block.InfiniwireBlock;
import mrp_v2.morewires.item.AdjustedRedstoneItem;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;

public class ObjectHolder
{
    public static final AdjustedRedstoneWireBlock[] WIRE_BLOCKS;
    public static final AdjustedRedstoneWireBlock[] WIRE_BLOCKS_EXCLUDING_REDSTONE;
    public static final AdjustedRedstoneItem[] WIRE_BLOCK_ITEMS;
    public static final AdjustedRedstoneItem[] WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE;
    public static final InfiniwireBlock[] INFINIWIRE_BLOCKS;
    public static final AdjustedRedstoneItem[] INFINIWIRE_BLOCK_ITEMS;
    public static final ArrayList<Triple<Float, String, ITag<Item>>> COLORS = new ArrayList<>();
    public static final Tags.IOptionalNamedTag<Item> WIRES_TAG =
            ItemTags.createOptional(Util.makeResourceLocation("wires"));
    public static final Tags.IOptionalNamedTag<Item> INFINIWIRES_TAG =
            ItemTags.createOptional(Util.makeResourceLocation("infiniwires"));

    static
    {
        COLORS.add(Triple.of(0.0F, "red", Tags.Items.DYES_RED));
        COLORS.add(Triple.of(2.0F / 3.0F, "blue", Tags.Items.DYES_BLUE));
        COLORS.add(Triple.of(1.0F / 3.0F, "green", Tags.Items.DYES_GREEN));
        COLORS.add(Triple.of(1.0F / 12.0F, "orange", Tags.Items.DYES_ORANGE));
        COLORS.add(Triple.of(5.0F / 6.0F, "pink", Tags.Items.DYES_PINK));
        COLORS.add(Triple.of(1.0F / 6.0F, "yellow", Tags.Items.DYES_YELLOW));
        WIRE_BLOCKS = new AdjustedRedstoneWireBlock[COLORS.size()];
        WIRE_BLOCKS_EXCLUDING_REDSTONE = new AdjustedRedstoneWireBlock[COLORS.size() - 1];
        WIRE_BLOCK_ITEMS = new AdjustedRedstoneItem[COLORS.size()];
        WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE = new AdjustedRedstoneItem[COLORS.size() - 1];
        int i = 0;
        WIRE_BLOCKS[i] = new AdjustedRedstoneWireBlock(COLORS.get(i).getLeft(), "minecraft:redstone");
        WIRE_BLOCK_ITEMS[i] = WIRE_BLOCKS[i].createBlockItem(COLORS.get(i).getRight());
        i++;
        for (Triple<Float, String, ITag<Item>> color : COLORS.subList(1, COLORS.size()))
        {
            WIRE_BLOCKS[i] = new AdjustedRedstoneWireBlock(color.getLeft(), color.getMiddle());
            WIRE_BLOCKS_EXCLUDING_REDSTONE[i - 1] = WIRE_BLOCKS[i];
            WIRE_BLOCK_ITEMS[i] = WIRE_BLOCKS[i].createBlockItem(color.getRight());
            WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE[i - 1] = WIRE_BLOCK_ITEMS[i];
            i++;
        }
        INFINIWIRE_BLOCKS = new InfiniwireBlock[COLORS.size()];
        INFINIWIRE_BLOCK_ITEMS = new AdjustedRedstoneItem[COLORS.size()];
        i = 0;
        for (Triple<Float, String, ITag<Item>> color : COLORS)
        {
            INFINIWIRE_BLOCKS[i] = new InfiniwireBlock(color.getLeft(), color.getMiddle());
            INFINIWIRE_BLOCK_ITEMS[i] = INFINIWIRE_BLOCKS[i].createBlockItem(color.getRight());
            i++;
        }
    }
}
