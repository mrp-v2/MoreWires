package mrp_v2.morewires.block;

import mrp_v2.morewires.block.util.InfiniwireChainParent;
import mrp_v2.morewires.block.util.InfiniwireGraphBuilder;
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
        super(Properties.copy(Blocks.REDSTONE_WIRE), hueChange);
    }

    public static HashSet<BlockPos> updateInfiniwireChain(World world, Map<BlockPos, BlockState> chain, int strength)
    {
        HashSet<BlockPos> updatedBlocks = new HashSet<>();
        for (Map.Entry<BlockPos, BlockState> entry : chain.entrySet())
        {
            if (strength != entry.getValue().getValue(POWER))
            {
                world.setBlock(entry.getKey(), entry.getValue().setValue(POWER, strength), 2);
                updatedBlocks.add(entry.getKey());
            }
        }
        return updatedBlocks;
    }

    public static int getStrongestNonWireSignal(World world, Set<BlockPos> chain)
    {
        int strongest = 0, test;
        globalCanProvidePower = false;
        for (BlockPos pos : chain)
        {
            test = world.getBestNeighborSignal(pos);
            if (test > strongest)
            {
                strongest = test;
            }
        }
        globalCanProvidePower = true;
        return strongest;
    }

    @Override public InfiniwireItem createBlockItem(ITag<Item> dyeTag)
    {
        return new InfiniwireItem(this, new Item.Properties().tab(ItemGroup.TAB_REDSTONE), dyeTag);
    }

    @Override
    public void onPlace(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (oldState.is(state.getBlock()) || worldIn.isClientSide)
        {
            return;
        }
        int test = this.getStrongestNonWireSignal(worldIn, pos);
        int neighborTest = this.getNeighborEquivalency(worldIn, pos);
        if (neighborTest == -2)
        {
            if (test != 0)
            {
                worldIn.setBlockAndUpdate(pos, state.setValue(POWER, test));
                this.updateRelevantNeighbors(worldIn, pos);
            }
        } else if (neighborTest == -1)
        {
            this.updateChain(worldIn, pos);
        } else if (neighborTest >= test)
        {
            worldIn.setBlockAndUpdate(pos, state.setValue(POWER, neighborTest));
            this.updateRelevantNeighbors(worldIn, pos);
        } else
        {
            this.updateChain(worldIn, pos);
        }
    }

    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos,
            boolean isMoving)
    {
        if (worldIn.isClientSide)
        {
            return;
        }
        if (state.canSurvive(worldIn, pos))
        {
            BlockState neighborState = worldIn.getBlockState(fromPos);
            if (isWireBlock(neighborState) || isWireBlock(blockIn))
            {
                return;
            }
            this.updateChain(worldIn, pos);
        } else
        {
            dropResources(state, worldIn, pos);
            worldIn.removeBlock(pos, false);
        }
    }

    private int getStrongestNonWireSignal(World world, BlockPos pos)
    {
        globalCanProvidePower = false;
        int strongest = world.getBestNeighborSignal(pos);
        globalCanProvidePower = true;
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
            neighborPower = neighbor.getValue().getValue(POWER);
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

    private Map<BlockPos, BlockState> getConnectedWireNeighbors(World world, BlockPos pos)
    {
        Map<BlockPos, BlockState> relevantWireNeighbors = new HashMap<>();
        BlockPos thisUp = pos.above(), neighborPos, neighborUp, neighborDown;
        BlockState thisUpState = world.getBlockState(thisUp), neighborState, neighborUpState, neighborDownState;
        boolean thisUpIsNormalCube = thisUpState.isRedstoneConductor(world, thisUp), neighborIsNormalCube;
        for (Direction horizontalDirection : Direction.Plane.HORIZONTAL)
        {
            neighborPos = pos.relative(horizontalDirection);
            neighborState = world.getBlockState(neighborPos);
            if (neighborState.is(this))
            {
                relevantWireNeighbors.put(neighborPos, neighborState);
                continue;
            }
            neighborIsNormalCube = neighborState.isRedstoneConductor(world, neighborPos);
            if (!thisUpIsNormalCube)
            {
                neighborUp = neighborPos.above();
                neighborUpState = world.getBlockState(neighborUp);
                if (neighborUpState.is(this))
                {
                    relevantWireNeighbors.put(neighborUp, neighborUpState);
                    continue;
                }
            }
            if (!neighborIsNormalCube)
            {
                neighborDown = neighborPos.below();
                neighborDownState = world.getBlockState(neighborDown);
                if (neighborDownState.is(this))
                {
                    relevantWireNeighbors.put(neighborDown, neighborDownState);
                }
            }
        }
        return relevantWireNeighbors;
    }

    private void updateRelevantNeighbors(World world, BlockPos pos)
    {
        for (BlockPos updatePos : getRelevantUpdateNeighbors(pos))
        {
            world.updateNeighborsAt(updatePos, this);
        }
    }

    private HashSet<BlockPos> getRelevantUpdateNeighbors(BlockPos pos)
    {
        HashSet<BlockPos> relevantNeighbors = new HashSet<>();
        relevantNeighbors.add(pos);
        for (Direction direction : Direction.values())
        {
            relevantNeighbors.add(pos.relative(direction));
        }
        return relevantNeighbors;
    }

    @Override
    public void onRemove(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.is(newState.getBlock()) || isMoving)
        {
            return;
        }
        if (state.hasTileEntity() && (!state.is(newState.getBlock()) || !newState.hasTileEntity()))
        {
            worldIn.removeBlockEntity(pos);
        }
        if (worldIn.isClientSide)
        {
            return;
        }
        if (newState.is(this) || !state.is(this))
        {
            return;
        }
        if (state.getValue(POWER) == 0)
        {
            this.updateRelevantNeighbors(worldIn, pos);
            return;
        }
        HashMap<InfiniwireChainParent, BlockPos> neighborChains = new HashMap<>();
        OuterLoop:
        for (BlockPos neighborPos : getConnectedWireNeighbors(worldIn, pos).keySet())
        {
            for (InfiniwireChainParent chain : neighborChains.keySet())
            {
                if (chain.containsPos(neighborPos))
                {
                    continue OuterLoop;
                }
            }
            neighborChains.put(getBlocksInChain(worldIn, neighborPos).build(), neighborPos);
        }
        if (neighborChains.size() > 1)
        {
            for (InfiniwireChainParent wireGraph : neighborChains.keySet())
            {
                this.updateChain(neighborChains.get(wireGraph), worldIn, wireGraph);
            }
        } else if (this.getStrongestNonWireSignal(worldIn, pos) == state.getValue(POWER))
        {
            for (InfiniwireChainParent wireGraph : neighborChains.keySet())
            {
                this.updateChain(neighborChains.get(wireGraph), worldIn, wireGraph);
            }
        }
        this.updateRelevantNeighbors(worldIn, pos);
    }

    private InfiniwireGraphBuilder getBlocksInChain(World world, BlockPos pos)
    {
        BlockState state = world.getBlockState(pos);
        InfiniwireGraphBuilder wireGraph = new InfiniwireGraphBuilder(pos, state, this);
        if (state.is(this))
        {
            getBlocksInChain(world, pos, wireGraph, new HashSet<>());
        }
        return wireGraph;
    }

    private void updateChain(BlockPos updateOrigin, World world, InfiniwireChainParent chain)
    {
        if (this.doingUpdate)
        {
            return;
        }
        this.doingUpdate = true;
        chain.update(updateOrigin, world);
        this.doingUpdate = false;
    }

    public void updateNeighbors(World world, HashSet<BlockPos> updatedBlocks)
    {
        HashSet<BlockPos> toUpdate = new HashSet<>();
        for (BlockPos pos : updatedBlocks)
        {
            toUpdate.addAll(getRelevantUpdateNeighbors(pos));
        }
        for (BlockPos updatePos : toUpdate)
        {
            world.updateNeighborsAt(updatePos, this);
        }
    }

    private void updateChain(World world, BlockPos pos)
    {
        if (doingUpdate)
        {
            return;
        }
        this.updateChain(pos, world, getBlocksInChain(world, pos).build());
    }

    private void getBlocksInChain(World world, BlockPos pos, InfiniwireGraphBuilder wireGraph,
            Set<BlockPos> checkedPositions)
    {
        checkedPositions.add(pos);
        for (BlockPos relevantNeighbor : getConnectedWireNeighbors(world, pos, wireGraph))
        {
            if (!checkedPositions.contains(relevantNeighbor))
            {
                getBlocksInChain(world, relevantNeighbor, wireGraph, checkedPositions);
            }
        }
    }

    private Set<BlockPos> getConnectedWireNeighbors(World world, BlockPos pos, InfiniwireGraphBuilder wireGraph)
    {
        Set<BlockPos> relevantWireNeighbors = new HashSet<>();
        BlockPos thisUp = pos.above(), thisDown = pos.below(), neighborPos, neighborUp, neighborDown;
        BlockState thisState = world.getBlockState(pos), thisUpState = world.getBlockState(thisUp), thisDownState =
                world.getBlockState(thisDown), neighborState, neighborUpState, neighborDownState;
        InfiniwireGraphBuilder.ConnectionType connectionType;
        boolean thisUpIsNormalCube = thisUpState.isRedstoneConductor(world, thisUp), thisDownIsNormalCube =
                thisDownState.isRedstoneConductor(world, thisDown), neighborIsNormalCube;
        for (Direction horizontalDirection : Direction.Plane.HORIZONTAL)
        {
            neighborPos = pos.relative(horizontalDirection);
            neighborState = world.getBlockState(neighborPos);
            if (neighborState.is(this))
            {
                relevantWireNeighbors.add(neighborPos);
                wireGraph.addNewConnection(pos, thisState, neighborPos, neighborState,
                        InfiniwireGraphBuilder.ConnectionType.BIDIRECTIONAL);
                continue;
            }
            neighborIsNormalCube = neighborState.isRedstoneConductor(world, neighborPos);
            if (!thisUpIsNormalCube)
            {
                neighborUp = neighborPos.above();
                neighborUpState = world.getBlockState(neighborUp);
                if (neighborUpState.is(this))
                {
                    if (neighborIsNormalCube)
                    {
                        connectionType = InfiniwireGraphBuilder.ConnectionType.BIDIRECTIONAL;
                    } else
                    {
                        connectionType = InfiniwireGraphBuilder.ConnectionType.A_TO_B;
                    }
                    relevantWireNeighbors.add(neighborUp);
                    wireGraph.addNewConnection(pos, thisState, neighborUp, neighborUpState, connectionType);
                }
            }
            if (!neighborIsNormalCube)
            {
                neighborDown = neighborPos.below();
                neighborDownState = world.getBlockState(neighborDown);
                if (neighborDownState.is(this))
                {
                    if (thisDownIsNormalCube)
                    {
                        connectionType = InfiniwireGraphBuilder.ConnectionType.BIDIRECTIONAL;
                    } else
                    {
                        connectionType = InfiniwireGraphBuilder.ConnectionType.B_TO_A;
                    }
                    relevantWireNeighbors.add(neighborDown);
                    wireGraph.addNewConnection(pos, thisState, neighborDown, neighborDownState, connectionType);
                }
            }
        }
        return relevantWireNeighbors;
    }
}