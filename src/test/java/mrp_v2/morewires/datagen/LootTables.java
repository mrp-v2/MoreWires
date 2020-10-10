package mrp_v2.morewires.datagen;

import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.morewires.util.Util;
import net.minecraft.block.Block;

public class LootTables extends mrp_v2.mrp_v2datagenlibrary.datagen.LootTables
{
    public LootTables()
    {
        Util.doOperationOn(this::dropSelfLootTable, ObjectHolder.WIRE_BLOCKS_EXCLUDING_REDSTONE);
        Util.doOperationOn(this::dropSelfLootTable, ObjectHolder.INFINIWIRE_BLOCKS);
    }

    private void dropSelfLootTable(Block block)
    {
        this.addLootTable(block, this::registerDropSelfLootTable);
    }
}
