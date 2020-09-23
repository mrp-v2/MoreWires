package mrp_v2.morewires.block;

import mrp_v2.morewires.item.AdjustedRedstoneItem;
import mrp_v2.morewires.item.InfiniwireItem;
import net.minecraft.block.Block;
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
    private boolean doingUpdate = false;

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

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (oldState.isIn(state.getBlock()) || worldIn.isRemote)
        {
            return;
        }
        super.onBlockAdded(state, worldIn, pos, oldState, isMoving);
        int test = this.getStrongestNonWireSignal(worldIn, pos);
        int neighborTest = this.getNeighborEquivalency(worldIn, pos);
        if (neighborTest == -2)
        {
            if (test != 0)
            {
                worldIn.setBlockState(pos, state.with(POWER, test));
            }
        } else if (neighborTest == -1)
        {
            this.updateChain(worldIn, pos);
        } else if (neighborTest >= test)
        {
            worldIn.setBlockState(pos, state.with(POWER, neighborTest));
        } else
        {
            this.updateChain(worldIn, pos);
        }
    }

    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.isIn(newState.getBlock()) || isMoving)
        {
            return;
        }
        if (state.hasTileEntity() && (!state.isIn(newState.getBlock()) || !newState.hasTileEntity()))
        {
            worldIn.removeTileEntity(pos);
        }
        if (worldIn.isRemote)
        {
            return;
        }
        if (newState.isIn(this) || !state.isIn(this))
        {
            return;
        }
        HashSet<HashSet<BlockPos>> neighborChains = new HashSet<>();
        for (BlockPos neighborPos : getConnectedWireNeighbors(worldIn, pos))
        {
            neighborChains.add(getBlocksInChain(worldIn, neighborPos));
        }
        neighborChains.remove(new HashSet<BlockPos>());
        if (neighborChains.size() > 1)
        {
            for (HashSet<BlockPos> chain : neighborChains)
            {
                this.updateChain(worldIn, chain);
            }
        } else if (this.getStrongestNonWireSignal(worldIn, pos) >= state.get(POWER))
        {
            for (HashSet<BlockPos> chain : neighborChains)
            {
                this.updateChain(worldIn, chain);
            }
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
            boolean isMoving)
    {
        if (worldIn.isRemote)
        {
            return;
        }
        if (state.isValidPosition(worldIn, pos))
        {
            BlockState neighborState = worldIn.getBlockState(fromPos);
            if (isWireBlock(neighborState) || isWireBlock(blockIn))
            {
                return;
            }
            this.updateChain(worldIn, pos);
        } else
        {
            spawnDrops(state, worldIn, pos);
            worldIn.removeBlock(pos, false);
        }
    }

    private int getStrongestNonWireSignal(World world, BlockPos pos)
    {
        this.canProvidePower = false;
        int strongest = world.getRedstonePowerFromNeighbors(pos);
        this.canProvidePower = true;
        return strongest;
    }

    /**
     * Tests the equivalency of neighboring wires.
     * 0-15 means all neighbor wires are that power level.
     * -1 means that the neighbor wires have different power levels.
     * -2 means that there are no neighboring wires.
     */
    private int getNeighborEquivalency(World world, BlockPos pos)
    {
        int foundPower = -2;
        for (BlockPos neighborPos : getConnectedWireNeighbors(world, pos))
        {
            BlockState neighborState = world.getBlockState(neighborPos);
            int neighborPower = neighborState.get(POWER);
            if (foundPower == -2)
            {
                foundPower = neighborPower;
            } else if (neighborPower != foundPower)
            {
                return -1;
            }
        }
        return foundPower;
    }

    private HashSet<BlockPos> getConnectedWireNeighbors(World world, BlockPos pos)
    {
        HashSet<BlockPos> relevantWireNeighbors = new HashSet<>();
        for (Direction horizontalDirection : Direction.Plane.HORIZONTAL)
        {
            BlockPos neighborPos = pos.offset(horizontalDirection);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (neighborState.isNormalCube(world, neighborPos))
            {
                BlockPos thisUp = pos.up();
                if (!world.getBlockState(thisUp).isNormalCube(world, thisUp))
                {
                    BlockPos neighborUp = neighborPos.up();
                    if (world.getBlockState(neighborUp).isIn(this))
                    {
                        relevantWireNeighbors.add(neighborUp);
                    }
                }
            } else
            {
                BlockPos neighborDown = neighborPos.down();
                if (neighborState.isIn(this))
                {
                    relevantWireNeighbors.add(neighborPos);
                } else if (world.getBlockState(neighborDown).isIn(this))
                {
                    relevantWireNeighbors.add(neighborDown);
                }
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

    private void updateChain(World world, HashSet<BlockPos> chain)
    {
        if (this.doingUpdate)
        {
            return;
        }
        int newStrength = getStrongestNonWireSignal(world, chain);
        this.doingUpdate = true;
        updateNeighbors(world, updateInfiniwireChain(world, chain, newStrength));
        this.doingUpdate = false;
    }

    private void updateNeighbors(World world, HashSet<BlockPos> updatedBlocks)
    {
        HashSet<BlockPos> toUpdate = new HashSet<>();
        for (BlockPos pos : updatedBlocks)
        {
            toUpdate.addAll(getRelevantUpdateNeighbors(pos, true));
        }
        for (BlockPos updatePos : toUpdate)
        {
            world.notifyNeighborsOfStateChange(updatePos, this);
        }
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

    private int getStrongestNonWireSignal(World world, HashSet<BlockPos> chain)
    {
        int strongest = 0;
        globalCanProvidePower = false;
        for (BlockPos pos : chain)
        {
            int test = world.getRedstonePowerFromNeighbors(pos);
            if (test > strongest)
            {
                strongest = test;
            }
        }
        globalCanProvidePower = true;
        return strongest;
    }

    private void updateChain(World world, BlockPos pos)
    {
        if (doingUpdate)
        {
            return;
        }
        HashSet<BlockPos> chain = getBlocksInChain(world, pos);
        this.updateChain(world, chain);
    }

    private HashSet<BlockPos> getBlocksInChain(World world, BlockPos pos)
    {
        HashSet<BlockPos> blocks = new HashSet<>();
        if (world.getBlockState(pos).isIn(this))
        {
            blocks.add(pos);
            getBlocksInChain(world, pos, blocks);
        }
        return blocks;
    }

    private void getBlocksInChain(World world, BlockPos pos, HashSet<BlockPos> foundBlocks)
    {
        for (BlockPos neighborPos : getConnectedWireNeighbors(world, pos))
        {
            if (foundBlocks.add(neighborPos))
            {
                getBlocksInChain(world, neighborPos, foundBlocks);
            }
        }
    }
}