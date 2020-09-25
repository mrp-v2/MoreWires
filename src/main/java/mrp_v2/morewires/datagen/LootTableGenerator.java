package mrp_v2.morewires.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import mrp_v2.morewires.MoreWires;
import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.morewires.util.Util;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class LootTableGenerator extends LootTableProvider
{
    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>>
            lootTables = ImmutableList.of(Pair.of(LootTables::new, LootParameterSets.BLOCK));

    public LootTableGenerator(DataGenerator dataGeneratorIn)
    {
        super(dataGeneratorIn);
    }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables()
    {
        return lootTables;
    }

    @Override protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker)
    {
        map.forEach(
                (resourceLocation, lootTable) -> LootTableManager.validateLootTable(validationtracker, resourceLocation,
                        lootTable));
    }

    @Override public String getName()
    {
        return super.getName() + ": " + MoreWires.ID;
    }

    private static class LootTables extends BlockLootTables
    {
        private final ArrayList<Block> knownBlocks;

        public LootTables()
        {
            this.knownBlocks = new ArrayList<>();
            Util.doOperationOn(this.knownBlocks::add, ObjectHolder.WIRE_BLOCKS_EXCLUDING_REDSTONE);
            Util.doOperationOn(this.knownBlocks::add, ObjectHolder.INFINIWIRE_BLOCKS);
        }

        @Override protected void addTables()
        {
            for (Block block : this.knownBlocks)
            {
                this.registerDropSelfLootTable(block);
            }
        }

        @Override protected Iterable<Block> getKnownBlocks()
        {
            return this.knownBlocks;
        }
    }
}
