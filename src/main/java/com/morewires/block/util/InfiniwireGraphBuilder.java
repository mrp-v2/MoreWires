package com.morewires.block.util;

import com.morewires.block.InfiniwireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class InfiniwireGraphBuilder {
    protected final Map<BlockPos, BlockState> blockMap = new HashMap<>();
    protected final Map<BlockPos, List<Connection>> connectionMap = new HashMap<>();
    protected final Set<Connection> connectionSet = new HashSet<>();
    protected final BlockPos originPos;
    protected final BlockState originState;
    protected final InfiniwireBlock block;

    public InfiniwireGraphBuilder(BlockPos originPos, BlockState originState, InfiniwireBlock block)
    {
        this.originPos = originPos;
        this.originState = originState;
        this.block = block;
    }

    protected List<Connection> getOrCreateConnectionList(BlockPos pos)
    {
        if (connectionMap.containsKey(pos))
        {
            return connectionMap.get(pos);
        } else
        {
            ArrayList<Connection> list = new ArrayList<>();
            connectionMap.put(pos, list);
            return list;
        }
    }

    public InfiniwireChainParent build()
    {
        Set<InfiniwireChain> chains = new HashSet<>();
        for (Connection connection : connectionSet)
        {
            InfiniwireChain a = null, b = null;
            for (InfiniwireChain chain : chains)
            {
                if (chain.positions.containsKey(connection.a))
                {
                    a = chain;
                }
                if (chain.positions.containsKey(connection.b))
                {
                    b = chain;
                }
            }
            if (a != null && a == b)
            {
                continue;
            }
            if (a != null && b != null)
            {
                setupChainRelationship(a, b, connection.connectionType, chains);
            } else if (connection.connectionType == ConnectionType.BIDIRECTIONAL)
            {
                if (a == null && b != null)
                {
                    b.positions.put(connection.a, blockMap.get(connection.a));
                } else if (a != null)
                {
                    a.positions.put(connection.b, blockMap.get(connection.b));
                } else
                {
                    a = new InfiniwireChain(block);
                    a.positions.put(connection.a, blockMap.get(connection.a));
                    a.positions.put(connection.b, blockMap.get(connection.b));
                    chains.add(a);
                }
            } else
            {
                if (a == null && b != null)
                {
                    a = new InfiniwireChain(block);
                    a.positions.put(connection.a, blockMap.get(connection.a));
                    chains.add(a);
                    setupChainRelationship(a, b, connection.connectionType, chains);
                } else if (a != null)
                {
                    b = new InfiniwireChain(block);
                    b.positions.put(connection.b, blockMap.get(connection.b));
                    chains.add(b);
                    setupChainRelationship(a, b, connection.connectionType, chains);
                } else
                {
                    a = new InfiniwireChain(block);
                    a.positions.put(connection.a, blockMap.get(connection.a));
                    chains.add(a);
                    b = new InfiniwireChain(block);
                    b.positions.put(connection.b, blockMap.get(connection.b));
                    chains.add(b);
                    setupChainRelationship(a, b, connection.connectionType, chains);
                }
            }
        }
        InfiniwireChainParent parent = new InfiniwireChainParent();
        for (InfiniwireChain chain : chains)
        {
            for (BlockPos pos : chain.positions.keySet())
            {
                parent.chainMap.put(pos, chain);
            }
        }
        if (chains.size() == 0)
        {
            InfiniwireChain chain = new InfiniwireChain(block);
            chain.positions.put(originPos, originState);
            parent.chainMap.put(originPos, chain);
        }
        return parent;
    }

    protected void setupChainRelationship(InfiniwireChain a, InfiniwireChain b, ConnectionType connectionType,
                                          Set<InfiniwireChain> chains)
    {
        switch (connectionType)
        {
            case BIDIRECTIONAL:
                checkForRecursivePowering(mergeChains(a, b, chains), chains);
                break;
            case A_TO_B:
                a.chainsPowering.add(b);
                b.chainsPoweredBy.add(a);
                checkForRecursivePowering(a, chains);
                break;
            case B_TO_A:
                b.chainsPowering.add(a);
                a.chainsPoweredBy.add(b);
                checkForRecursivePowering(a, chains);
                break;
        }
    }

    protected InfiniwireChain mergeChains(InfiniwireChain a, InfiniwireChain b, Set<InfiniwireChain> chains)
    {
        InfiniwireChain combinedChain = new InfiniwireChain(block);
        combinedChain.positions.putAll(a.positions);
        combinedChain.positions.putAll(b.positions);
        a.chainsPoweredBy.remove(b);
        b.chainsPoweredBy.remove(a);
        a.chainsPowering.remove(b);
        b.chainsPowering.remove(a);
        combinedChain.chainsPoweredBy.addAll(a.chainsPoweredBy);
        for (InfiniwireChain chain : a.chainsPoweredBy)
        {
            chain.chainsPowering.remove(a);
            chain.chainsPowering.add(combinedChain);
        }
        combinedChain.chainsPoweredBy.addAll(b.chainsPoweredBy);
        for (InfiniwireChain chain : b.chainsPoweredBy)
        {
            chain.chainsPowering.remove(b);
            chain.chainsPowering.add(combinedChain);
        }
        combinedChain.chainsPowering.addAll(a.chainsPowering);
        for (InfiniwireChain chain : a.chainsPowering)
        {
            chain.chainsPoweredBy.remove(a);
            chain.chainsPoweredBy.add(combinedChain);
        }
        combinedChain.chainsPowering.addAll(b.chainsPowering);
        for (InfiniwireChain chain : b.chainsPowering)
        {
            chain.chainsPoweredBy.remove(b);
            chain.chainsPoweredBy.add(combinedChain);
        }
        chains.remove(a);
        chains.remove(b);
        chains.add(combinedChain);
        return combinedChain;
    }

    protected void checkForRecursivePowering(InfiniwireChain chain, Set<InfiniwireChain> chains)
    {
        checkForRecursivePowering(chain, chain, chains, new HashSet<>(), new Stack<>());
    }

    protected void mergeChains(Set<InfiniwireChain> chainsToMerge, Set<InfiniwireChain> chains)
    {
        if (chainsToMerge.size() < 2)
        {
            return;
        }
        InfiniwireChain[] chainArray = chainsToMerge.toArray(new InfiniwireChain[0]);
        if (chainsToMerge.size() == 2)
        {
            checkForRecursivePowering(mergeChains(chainArray[0], chainArray[1], chains), chains);
        }
        InfiniwireChain combined = chainArray[0];
        for (int i = 1; i < chainArray.length; i++)
        {
            combined = mergeChains(combined, chainArray[i], chains);
        }
        checkForRecursivePowering(combined, chains);
    }

    protected void checkForRecursivePowering(InfiniwireChain current, InfiniwireChain start,
                                             Set<InfiniwireChain> chains, Set<InfiniwireChain> checkedChains, Stack<InfiniwireChain> currentPath)
    {
        checkedChains.add(current);
        for (InfiniwireChain chain : current.chainsPowering)
        {
            currentPath.push(chain);
            if (chain == start)
            {
                mergeChains(new HashSet<>(currentPath), chains);
                return;
            }
            if (!checkedChains.contains(chain))
            {
                checkForRecursivePowering(chain, start, chains, checkedChains, currentPath);
            }
            currentPath.pop();
        }
    }

    public void addNewConnection(BlockPos aPos, BlockState aState, BlockPos bPos, BlockState bState,
                                 ConnectionType connectionType)
    {
        Connection connection = new Connection(aPos, bPos, connectionType);
        if (!connectionSet.contains(connection))
        {
            connection.install();
            if (!blockMap.containsKey(aPos))
            {
                blockMap.put(aPos, aState);
            }
            if (!blockMap.containsKey(bPos))
            {
                blockMap.put(bPos, bState);
            }
        }
    }

    public enum ConnectionType
    {
        BIDIRECTIONAL, A_TO_B, B_TO_A
    }

    protected class Connection
    {
        protected final BlockPos a, b;
        protected final ConnectionType connectionType;

        protected Connection(BlockPos a, BlockPos b, ConnectionType connectionType)
        {
            this.a = a;
            this.b = b;
            this.connectionType = connectionType;
        }

        protected void install()
        {
            getOrCreateConnectionList(a).add(this);
            getOrCreateConnectionList(b).add(this);
            connectionSet.add(this);
        }

        @Override public int hashCode()
        {
            return (a.hashCode() + b.hashCode());
        }

        @Override public boolean equals(Object o)
        {
            if (this == o)
            {
                return true;
            }
            if (!(o instanceof Connection other))
            {
                return false;
            }
            boolean equalsPositions = a.equals(other.a) && b.equals(other.b) || a.equals(other.b) && b.equals(other.a);
            return equalsPositions && connectionType == other.connectionType;
        }
    }
}
