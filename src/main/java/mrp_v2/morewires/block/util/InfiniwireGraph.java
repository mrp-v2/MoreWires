package mrp_v2.morewires.block.util;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

import java.util.*;

public class InfiniwireGraph
{
    protected final Map<BlockPos, BlockState> blockMap = new HashMap<>();
    protected final Map<BlockPos, List<Connection>> connectionMap = new HashMap<>();
    protected final Set<Connection> connectionSet = new HashSet<>();

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

    public boolean addNewConnection(BlockPos aPos, BlockState aState, BlockPos bPos, BlockState bState,
            ConnectionType connectionType)
    {
        if (!blockMap.containsKey(aPos))
        {
            blockMap.put(aPos, aState);
        }
        if (!blockMap.containsKey(bPos))
        {
            blockMap.put(bPos, bState);
        }
        Connection connection = new Connection(aPos, bPos, connectionType);
        if (connectionSet.contains(connection))
        {
            return false;
        } else
        {
            connection.install();
            return true;
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
            if (!(o instanceof Connection))
            {
                return false;
            }
            Connection other = (Connection) o;
            boolean equalsPositions = a.equals(other.a) && b.equals(other.b) || a.equals(other.b) && b.equals(other.a);
            return equalsPositions && connectionType == other.connectionType;
        }
    }
}
