package mrp_v2.computercomponents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class InfiniwireBlock extends AltRedstoneWireBlock
{
    private static final HashMap<Block, HashMap<Integer, Pair<Integer, Vector3f>>> blockAndStrengthToColorMap =
            new HashMap<>();
    private static boolean doingUpdate = false;

    public InfiniwireBlock(float hueChange, String id)
    {
        super(Properties.from(Blocks.REDSTONE_WIRE));
        this.setRegistryName(id + "_infiniwire");
        blockAndStrengthToColorMap.put(this, calculateColors(hueChange));
    }

    private static HashMap<Integer, Pair<Integer, Vector3f>> calculateColors(float hueChange)
    {
        while (hueChange > 1)
        {
            hueChange--;
        }
        while (hueChange < 0)
        {
            hueChange++;
        }
        HashMap<Integer, Pair<Integer, Vector3f>> colors = new HashMap<>();
        for (int i = 0; i <= 15; i++)
        {
            Vector3f RGBColorVecF = RedstoneWireBlock.powerRGB[i];
            Vector3i RGBColorVecI =
                    new Vector3i(RGBColorVecF.getX() * 255, RGBColorVecF.getY() * 255, RGBColorVecF.getZ() * 255);
            float[] hsb = Color.RGBtoHSB(RGBColorVecI.getX(), RGBColorVecI.getY(), RGBColorVecI.getZ(), null);
            hsb[0] += hueChange;
            if (hsb[0] > 1)
            {
                hsb[0]--;
            }
            int color = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
            Vector3f colorVec = new Vector3f((color >> 16) & 0xFF, (color >> 8) & 0xFF, color & 0xFF);
            colors.put(i, Pair.of(color, colorVec));
        }
        return colors;
    }

    public static int getColor(BlockState state)
    {
        return blockAndStrengthToColorMap.get(state.getBlock()).get(state.get(POWER)).getLeft();
    }

    @Override public BlockItem createBlockItem()
    {
        BlockItem item = new BlockItem(this, new Item.Properties().group(ItemGroup.REDSTONE));
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
        canProvidePower = false;
        for (BlockPos pos : chain)
        {
            int test = world.getRedstonePowerFromNeighbors(pos);
            if (test > strongest)
            {
                strongest = test;
            }
        }
        canProvidePower = true;
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

    @Override public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        int i = stateIn.get(POWER);
        if (i != 0)
        {
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                RedstoneSide redstoneside = stateIn.get(FACING_PROPERTY_MAP.get(direction));
                switch (redstoneside)
                {
                    case UP:
                        this.spawnPoweredParticle(worldIn, rand, pos,
                                blockAndStrengthToColorMap.get(this).get(i).getRight(), direction, Direction.UP, -0.5F,
                                0.5F);
                    case SIDE:
                        this.spawnPoweredParticle(worldIn, rand, pos,
                                blockAndStrengthToColorMap.get(this).get(i).getRight(), Direction.DOWN, direction, 0.0F,
                                0.5F);
                        break;
                    case NONE:
                    default:
                        this.spawnPoweredParticle(worldIn, rand, pos,
                                blockAndStrengthToColorMap.get(this).get(i).getRight(), Direction.DOWN, direction, 0.0F,
                                0.3F);
                }
            }
        }
    }
}