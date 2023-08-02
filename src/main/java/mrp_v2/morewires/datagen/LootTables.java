package mrp_v2.morewires.datagen;

import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.mrplibrary.datagen.BlockLootTables;
import net.minecraft.block.Block;
import net.minecraftforge.fml.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class LootTables extends BlockLootTables
{
    public LootTables()
    {
        List<RegistryObject<? extends Block>> blockObjects = new ArrayList<>();
        blockObjects.addAll(ObjectHolder.INFINIWIRE_BLOCKS.values());
        blockObjects.addAll(ObjectHolder.WIRE_BLOCKS_EXCLUDING_REDSTONE.values());
        for (RegistryObject<? extends Block> blockObject : blockObjects)
        {
            dropSelfLootTable(blockObject.get());
        }
    }

    protected void dropSelfLootTable(Block block)
    {
        this.addLootTable(block, this::dropSelf);
    }
}
