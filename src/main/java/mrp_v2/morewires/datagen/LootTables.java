package mrp_v2.morewires.datagen;

import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.morewires.util.Util;
import net.minecraft.block.Block;

public class LootTables extends mrp_v2.mrp_v2datagenlibrary.datagen.LootTables
{
    public LootTables()
    {
        Util.doOperationOn((blockObject) -> this.dropSelfLootTable(blockObject.get()),
                ObjectHolder.WIRE_BLOCKS_EXCLUDING_REDSTONE.values());
        Util.doOperationOn((blockObject) -> this.dropSelfLootTable(blockObject.get()),
                ObjectHolder.INFINIWIRE_BLOCKS.values());
    }

    private void dropSelfLootTable(Block block)
    {
        this.addLootTable(block, this::registerDropSelfLootTable);
    }
}
