package mrp_v2.morewires.datagen;

import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.mrplibrary.datagen.BlockLootTables;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LootTables extends BlockLootTables
{
    private final List<RegistryObject<? extends Block>> blockObjects;
    public LootTables()
    {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
        blockObjects = new ArrayList<>();
        blockObjects.addAll(ObjectHolder.INFINIWIRE_BLOCKS.values());
        blockObjects.addAll(ObjectHolder.WIRE_BLOCKS_EXCLUDING_REDSTONE.values());
    }

    @Override
    protected void generate() {
        for (RegistryObject<? extends Block> blockObject : blockObjects)
        {
            this.dropSelf(blockObject.get());
        }
    }

    @Override
    protected Iterable<Block> getKnownBlocks()
    {
        return blockObjects.stream().map((blockObject) -> blockObject.get()).collect(Collectors.toList());
    }
}
