package com.morewires.block.util;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class InfiniwireChainParent {
    final Map<BlockPos, InfiniwireChain> chainMap = new HashMap<>();

    public void update(BlockPos updateOrigin, World world)
    {
        chainMap.get(updateOrigin).update(world);
    }

    public boolean containsPos(BlockPos pos)
    {
        return chainMap.containsKey(pos);
    }
}
