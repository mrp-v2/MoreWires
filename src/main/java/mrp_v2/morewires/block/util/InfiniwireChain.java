package mrp_v2.morewires.block.util;

import mrp_v2.morewires.block.InfiniwireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InfiniwireChain
{
    final Map<BlockPos, BlockState> positions = new HashMap<>();
    final Set<InfiniwireChain> chainsPoweredBy = new HashSet<>();
    final Set<InfiniwireChain> chainsPowering = new HashSet<>();
    final InfiniwireBlock block;

    public InfiniwireChain(InfiniwireBlock block)
    {
        this.block = block;
    }

    void update(World world)
    {
        int powerFromChains = getPowerFromRelevantChains();
        int powerFromWorld = getUpdatedBlockPower(world);
        BlockState state = getBlockState();
        int oldPower = getPower(state);
        int newPower = Math.max(powerFromChains, powerFromWorld);
        update(oldPower, newPower, world);
    }

    void update(int oldPower, int newPower, World world)
    {
        if (newPower != oldPower)
        {
            block.updateNeighbors(world, InfiniwireBlock.updateInfiniwireChain(world, positions, newPower));
            positions.keySet()
                    .forEach((pos1) -> positions.computeIfPresent(pos1, (pos2, state) -> world.getBlockState(pos2)));
            for (InfiniwireChain chain : chainsPowering)
            {
                int chainPower = chain.getPower();
                if (chainPower < newPower)
                {
                    chain.update(chainPower, newPower, world);
                } else if (chainPower == oldPower)
                {
                    chain.update(world);
                }
            }
        }
    }

    int getPower()
    {
        return getPower(getBlockState());
    }

    BlockState getBlockState()
    {
        return positions.values().stream().findFirst().get();
    }

    int getPower(BlockState state)
    {
        return state.get(InfiniwireBlock.POWER);
    }

    int getPowerFromRelevantChains()
    {
        int power = 0;
        for (InfiniwireChain chain : chainsPoweredBy)
        {
            power = Math.max(power, chain.getPower());
        }
        return power;
    }

    int getUpdatedBlockPower(World world)
    {
        return InfiniwireBlock.getStrongestNonWireSignal(world, positions.keySet());
    }
}
