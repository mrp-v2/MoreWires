package com.morewires.block;

import com.morewires.block.util.InfiniwireChainParent;
import com.morewires.block.util.InfiniwireGraphBuilder;
import com.morewires.item.InfiniwireItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class InfiniwireBlock extends AdjustedRedstoneWireBlock {
    private boolean doingUpdate = false;
    public InfiniwireBlock(int RGBInt) {
        super(Settings.copy(Blocks.REDSTONE_WIRE), RGBInt);
    }
    public static HashSet<BlockPos> updateInfiniwireChain(World world, Map<BlockPos, BlockState> chain, int strength)
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

    public static int getStrongestNonWireSignal(World world, Set<BlockPos> chain)
    {
        int strongest = 0, test;
        globalCanProvidePower = false;
        for (BlockPos pos : chain)
        {
            test = world.getReceivedRedstonePower(pos);
            if (test > strongest)
            {
                strongest = test;
            }
        }
        globalCanProvidePower = true;
        return strongest;
    }

    public InfiniwireItem createBlockItem(Item dye)
    {
        return new InfiniwireItem(this, new Item.Settings(), dye);
    }

    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving)
    {
        if (oldState.isOf(state.getBlock()) || worldIn.isClient())
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
    public void neighborUpdate(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving)
    {
        if (worldIn.isClient())
        {
            return;
        }
        if (state.canPlaceAt(worldIn, pos))
        {
            BlockState neighborState = worldIn.getBlockState(fromPos);
            if (isWireBlock(neighborState) || isWireBlock(blockIn))
            {
                return;
            }
            this.updateChain(worldIn, pos);
        } else
        {
            dropStacks(state, worldIn, pos);
            worldIn.removeBlock(pos, false);
        }
    }

    private int getStrongestNonWireSignal(World world, BlockPos pos)
    {
        globalCanProvidePower = false;
        int strongest = world.getReceivedRedstonePower(pos);
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

    private Map<BlockPos, BlockState> getConnectedWireNeighbors(World world, BlockPos pos)
    {
        Map<BlockPos, BlockState> relevantWireNeighbors = new HashMap<>();
        BlockPos thisUp = pos.up(), neighborPos, neighborUp, neighborDown;
        BlockState thisUpState = world.getBlockState(thisUp), neighborState, neighborUpState, neighborDownState;
        boolean thisUpIsNormalCube = thisUpState.isSolidBlock(world, thisUp), neighborIsNormalCube;
        for (Direction horizontalDirection : Direction.Type.HORIZONTAL)
        {
            neighborPos = pos.offset(horizontalDirection);
            neighborState = world.getBlockState(neighborPos);
            if (neighborState.isOf(this))
            {
                relevantWireNeighbors.put(neighborPos, neighborState);
                continue;
            }
            neighborIsNormalCube = neighborState.isSolidBlock(world, neighborPos);
            if (!thisUpIsNormalCube)
            {
                neighborUp = neighborPos.up();
                neighborUpState = world.getBlockState(neighborUp);
                if (neighborUpState.isOf(this))
                {
                    relevantWireNeighbors.put(neighborUp, neighborUpState);
                    continue;
                }
            }
            if (!neighborIsNormalCube)
            {
                neighborDown = neighborPos.down();
                neighborDownState = world.getBlockState(neighborDown);
                if (neighborDownState.isOf(this))
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
            world.updateNeighborsAlways(updatePos, this);
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

    @Override
    public void onStateReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.isOf(newState.getBlock()) || isMoving)
        {
            return;
        }
        if (state.hasBlockEntity() && (!state.isOf(newState.getBlock()) || !newState.hasBlockEntity()))
        {
            worldIn.removeBlockEntity(pos);
        }
        if (worldIn.isClient())
        {
            return;
        }
        if (newState.isOf(this) || !state.isOf(this))
        {
            return;
        }
        if (state.get(POWER) == 0)
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
        } else if (this.getStrongestNonWireSignal(worldIn, pos) == state.get(POWER))
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
        if (state.isOf(this))
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
            world.updateNeighborsAlways(updatePos, this);
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
        BlockPos thisUp = pos.up(), thisDown = pos.down(), neighborPos, neighborUp, neighborDown;
        BlockState thisState = world.getBlockState(pos), thisUpState = world.getBlockState(thisUp), thisDownState =
                world.getBlockState(thisDown), neighborState, neighborUpState, neighborDownState;
        InfiniwireGraphBuilder.ConnectionType connectionType;
        boolean thisUpIsNormalCube = thisUpState.isSolidBlock(world, thisUp), thisDownIsNormalCube =
                thisDownState.isSolidBlock(world, thisDown), neighborIsNormalCube;
        for (Direction horizontalDirection : Direction.Type.HORIZONTAL)
        {
            neighborPos = pos.offset(horizontalDirection);
            neighborState = world.getBlockState(neighborPos);
            if (neighborState.isOf(this))
            {
                relevantWireNeighbors.add(neighborPos);
                wireGraph.addNewConnection(pos, thisState, neighborPos, neighborState,
                        InfiniwireGraphBuilder.ConnectionType.BIDIRECTIONAL);
                continue;
            }
            neighborIsNormalCube = neighborState.isSolidBlock(world, neighborPos);
            if (!thisUpIsNormalCube)
            {
                neighborUp = neighborPos.up();
                neighborUpState = world.getBlockState(neighborUp);
                if (neighborUpState.isOf(this))
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
                neighborDown = neighborPos.down();
                neighborDownState = world.getBlockState(neighborDown);
                if (neighborDownState.isOf(this))
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

