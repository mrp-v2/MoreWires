package mrp_v2.morewires.block;

import mrp_v2.morewires.item.AdjustedRedstoneItem;
import mrp_v2.morewires.item.InfiniwireItem;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashSet;

public class InfiniwireBlock extends AdjustedRedstoneWireBlock
{
    private static boolean doingUpdate = false;

    public InfiniwireBlock(float hueChange, String id)
    {
        super(Properties.from(Blocks.REDSTONE_WIRE), hueChange, id + "_infiniwire");
    }

    @Override public AdjustedRedstoneItem createBlockItem(ITag<Item> dyeTag)
    {
        InfiniwireItem item = new InfiniwireItem(this, new Item.Properties().group(ItemGroup.REDSTONE), dyeTag);
        item.setRegistryName(this.getRegistryName());
        return item;
    }

    @Override protected void func_235547_a_(World world, BlockPos pos, BlockState state)
    {
        if (!doingUpdate)
        {
            doingUpdate = true;
            updateChain(world, pos);
            doingUpdate = false;
        }
    }

    private void updateNeighbors(World world, HashSet<BlockPos> updatedBlocks)
    {
        for (BlockPos pos : updatedBlocks)
        {
            for (BlockPos updatePos : getRelevantUpdateNeighbors(pos, true))
            {
                world.notifyNeighborsOfStateChange(updatePos, this);
            }
        }
    }

    private HashSet<BlockPos> getRelevantWireNeighbors(BlockPos pos)
    {
        HashSet<BlockPos> relevantWireNeighbors = getRelevantUpdateNeighbors(pos, false);
        for (Direction horizontalDirection : Direction.Plane.HORIZONTAL)
        {
            for (Direction verticalDirection : Direction.Plane.VERTICAL)
            {
                relevantWireNeighbors.add(pos.offset(horizontalDirection).offset(verticalDirection));
            }
        }
        return relevantWireNeighbors;
    }

    private HashSet<BlockPos> getRelevantUpdateNeighbors(BlockPos pos, boolean includeSelf)
    {
        HashSet<BlockPos> relevantNeighbors = new HashSet<>();
        if (includeSelf)
        {
            relevantNeighbors.add(pos);
        }
        for (Direction direction : Direction.values())
        {
            relevantNeighbors.add(pos.offset(direction));
        }
        return relevantNeighbors;
    }

    private HashSet<BlockPos> updateInfiniwireChain(World world, HashSet<BlockPos> chain, int strength)
    {
        HashSet<BlockPos> updatedBlocks = new HashSet<>();
        for (BlockPos pos : chain)
        {
            BlockState state = world.getBlockState(pos);
            if (strength != state.get(POWER))
            {
                world.setBlockState(pos, state.with(POWER, strength), 2);
                updatedBlocks.add(pos);
            }
        }
        return updatedBlocks;
    }

    private void updateChain(World world, BlockPos pos)
    {
        HashSet<BlockPos> chain = getBlocksInChain(world, pos);
        int newStrength = getStrongestSignalChain(world, chain);
        updateNeighbors(world, updateInfiniwireChain(world, chain, newStrength));
    }

    private int getStrongestSignalChain(World world, HashSet<BlockPos> chain)
    {
        int strongest = 0;
        this.canProvidePower = false;
        for (BlockPos pos : chain)
        {
            int test = world.getRedstonePowerFromNeighbors(pos);
            if (test > strongest)
            {
                strongest = test;
            }
        }
        this.canProvidePower = true;
        return strongest;
    }

    private HashSet<BlockPos> getBlocksInChain(World world, BlockPos pos)
    {
        HashSet<BlockPos> blocks = new HashSet<>();
        if (world.getBlockState(pos).isIn(this))
        {
            blocks.add(pos);
        }
        getBlocksInChain(world, pos, blocks);
        return blocks;
    }

    private void getBlocksInChain(World world, BlockPos pos, HashSet<BlockPos> foundBlocks)
    {
        for (BlockPos neighborPos : getRelevantWireNeighbors(pos))
        {
            BlockState state = world.getBlockState(neighborPos);
            if (state.isIn(this))
            {
                if (foundBlocks.add(neighborPos))
                {
                    getBlocksInChain(world, neighborPos, foundBlocks);
                }
            }
        }
    }
}