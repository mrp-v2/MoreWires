package mrp_v2.morewires.block;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InfiniwireBlock extends AdjustedRedstoneWireBlock
{
    private boolean doingUpdate = false;

    public InfiniwireBlock(float hueChange)
    {
        super(Properties.from(Blocks.REDSTONE_WIRE), hueChange);
    }

    @Override public InfiniwireItem createBlockItem(ITag<Item> dyeTag)
    {
        return new InfiniwireItem(this, new Item.Properties().group(ItemGroup.REDSTONE), dyeTag);
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (oldState.isIn(state.getBlock()) || worldIn.isRemote)
        {
            return;
        }
        int test = this.getStrongestNonWireSignal(worldIn, pos);
        int neighborTest = this.getNeighborEquivalency(worldIn, pos);
        if (neighborTest == -2)
        {
            if (test != 0)
            {
                worldIn.setBlockState(pos, state.with(POWER, test));
                this.updateRelevantNeighbors(worldIn, pos);
            }
        } else if (neighborTest == -1)
        {
            this.updateChain(worldIn, pos);
        } else if (neighborTest >= test)
        {
            worldIn.setBlockState(pos, state.with(POWER, neighborTest));
            this.updateRelevantNeighbors(worldIn, pos);
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
        if (state.get(POWER) == 0)
        {
            this.updateRelevantNeighbors(worldIn, pos);
            return;
        }
        HashSet<HashMap<BlockPos, BlockState>> neighborChains = new HashSet<>();
        for (BlockPos neighborPos : getConnectedWireNeighbors(worldIn, pos).keySet())
        {
            neighborChains.add(getBlocksInChain(worldIn, neighborPos));
        }
        if (neighborChains.size() > 1)
        {
            for (HashMap<BlockPos, BlockState> chain : neighborChains)
            {
                this.updateChain(worldIn, chain);
            }
        } else if (this.getStrongestNonWireSignal(worldIn, pos) == state.get(POWER))
        {
            for (HashMap<BlockPos, BlockState> chain : neighborChains)
            {
                this.updateChain(worldIn, chain);
            }
        }
        this.updateRelevantNeighbors(worldIn, pos);
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
        int foundPower = -2, neighborPower;
        for (Map.Entry<BlockPos, BlockState> neighbor : getConnectedWireNeighbors(world, pos).entrySet())
        {
            neighborPower = neighbor.getValue().get(POWER);
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

    private HashMap<BlockPos, BlockState> getConnectedWireNeighbors(World world, BlockPos pos)
    {
        HashMap<BlockPos, BlockState> relevantWireNeighbors = new HashMap<>();
        BlockPos neighborPos, thisUp, neighborUp, neighborDown;
        BlockState neighborState, neighborUpState, neighborDownState;
        for (Direction horizontalDirection : Direction.Plane.HORIZONTAL)
        {
            neighborPos = pos.offset(horizontalDirection);
            neighborState = world.getBlockState(neighborPos);
            if (neighborState.isNormalCube(world, neighborPos))
            {
                thisUp = pos.up();
                if (!world.getBlockState(thisUp).isNormalCube(world, thisUp))
                {
                    neighborUp = neighborPos.up();
                    neighborUpState = world.getBlockState(neighborUp);
                    if (neighborUpState.isIn(this))
                    {
                        relevantWireNeighbors.put(neighborUp, neighborUpState);
                    }
                }
            } else
            {
                neighborDown = neighborPos.down();
                if (neighborState.isIn(this))
                {
                    relevantWireNeighbors.put(neighborPos, neighborState);
                } else
                {
                    neighborDownState = world.getBlockState(neighborDown);
                    if (neighborDownState.isIn(this))
                    {
                        relevantWireNeighbors.put(neighborDown, neighborDownState);
                    }
                }
            }
        }
        return relevantWireNeighbors;
    }

    private void updateRelevantNeighbors(World world, BlockPos pos)
    {
        for (BlockPos updatePos : getRelevantUpdateNeighbors(pos))
        {
            world.notifyNeighborsOfStateChange(updatePos, this);
        }
    }

    private HashSet<BlockPos> getRelevantUpdateNeighbors(BlockPos pos)
    {
        HashSet<BlockPos> relevantNeighbors = new HashSet<>();
        relevantNeighbors.add(pos);
        for (Direction direction : Direction.values())
        {
            relevantNeighbors.add(pos.offset(direction));
        }
        return relevantNeighbors;
    }

    private void updateChain(World world, HashMap<BlockPos, BlockState> chain)
    {
        if (this.doingUpdate)
        {
            return;
        }
        int newStrength = getStrongestNonWireSignal(world, chain.keySet());
        this.doingUpdate = true;
        updateNeighbors(world, updateInfiniwireChain(world, chain, newStrength));
        this.doingUpdate = false;
    }

    private void updateNeighbors(World world, HashSet<BlockPos> updatedBlocks)
    {
        HashSet<BlockPos> toUpdate = new HashSet<>();
        for (BlockPos pos : updatedBlocks)
        {
            toUpdate.addAll(getRelevantUpdateNeighbors(pos));
        }
        for (BlockPos updatePos : toUpdate)
        {
            world.notifyNeighborsOfStateChange(updatePos, this);
        }
    }

    private HashSet<BlockPos> updateInfiniwireChain(World world, HashMap<BlockPos, BlockState> chain, int strength)
    {
        HashSet<BlockPos> updatedBlocks = new HashSet<>();
        for (Map.Entry<BlockPos, BlockState> entry : chain.entrySet())
        {
            if (strength != entry.getValue().get(POWER))
            {
                world.setBlockState(entry.getKey(), entry.getValue().with(POWER, strength), 2);
                updatedBlocks.add(entry.getKey());
            }
        }
        return updatedBlocks;
    }

    private int getStrongestNonWireSignal(World world, Set<BlockPos> chain)
    {
        int strongest = 0, test;
        globalCanProvidePower = false;
        for (BlockPos pos : chain)
        {
            test = world.getRedstonePowerFromNeighbors(pos);
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
        HashMap<BlockPos, BlockState> chain = getBlocksInChain(world, pos);
        this.updateChain(world, chain);
    }

    private HashMap<BlockPos, BlockState> getBlocksInChain(World world, BlockPos pos)
    {
        HashMap<BlockPos, BlockState> blocks = new HashMap<>();
        BlockState state = world.getBlockState(pos);
        if (state.isIn(this))
        {
            blocks.put(pos, state);
            getBlocksInChain(world, pos, blocks);
        }
        return blocks;
    }

    private void getBlocksInChain(World world, BlockPos pos, HashMap<BlockPos, BlockState> foundBlocks)
    {
        for (Map.Entry<BlockPos, BlockState> neighbor : getConnectedWireNeighbors(world, pos).entrySet())
        {
            if (foundBlocks.put(neighbor.getKey(), neighbor.getValue()) == null)
            {
                getBlocksInChain(world, neighbor.getKey(), foundBlocks);
            }
        }
    }
}