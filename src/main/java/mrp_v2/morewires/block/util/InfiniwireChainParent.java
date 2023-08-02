package mrp_v2.morewires.block.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.HashMap;
import java.util.Map;

public class InfiniwireChainParent
{
    final Map<BlockPos, InfiniwireChain> chainMap = new HashMap<>();

    public void update(BlockPos updateOrigin, Level world)
    {
        chainMap.get(updateOrigin).update(world);
    }

    public boolean containsPos(BlockPos pos)
    {
        return chainMap.containsKey(pos);
    }
}
