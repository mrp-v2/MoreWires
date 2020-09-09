package mrp_v2.computercomponents.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.function.BiFunction;

public class InfiniwireBlock extends AltRedstoneWireBlock
{
    private static final HashMap<Block, HashMap<Integer, Vector3f>> blockAndStrengthToColorMap = new HashMap<>();
    private final BiFunction<Float, Integer, Float> colorFunction = (colorPart, power) ->
    {
        if (power == 0)
        {
            return colorPart * 0.3F;
        }
        return MathHelper.clamp(colorPart * (1 - (0.04F) * (16 - power)), 0.0F, 1.0F);
    };
    private boolean doingUpdate = false;

    public InfiniwireBlock(Vector3f color, String id)
    {
        super(Properties.from(Blocks.REDSTONE_WIRE));
        this.setRegistryName(id + "_infiniwire");
        this.calculateColor(color);
    }

    private void calculateColor(Vector3f color)
    {
        HashMap<Integer, Vector3f> colors = new HashMap<>();
        blockAndStrengthToColorMap.put(this, colors);
        for (int i = 0; i <= 15; ++i)
        {
            float r = colorFunction.apply(color.getX(), i);
            float g = colorFunction.apply(color.getY(), i);
            float b = colorFunction.apply(color.getZ(), i);
            colors.put(i, new Vector3f(r, g, b));
        }
    }

    public static int getColor(BlockState state)
    {
        int power = state.get(POWER);
        HashMap<Integer, Vector3f> colors = blockAndStrengthToColorMap.get(state.getBlock());
        Vector3f vector3f = colors.get(power);
        return MathHelper.rgb(vector3f.getX(), vector3f.getY(), vector3f.getZ());
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
                        this.spawnPoweredParticle(worldIn, rand, pos, blockAndStrengthToColorMap.get(this).get(i),
                                direction, Direction.UP, -0.5F, 0.5F);
                    case SIDE:
                        this.spawnPoweredParticle(worldIn, rand, pos, blockAndStrengthToColorMap.get(this).get(i),
                                Direction.DOWN, direction, 0.0F, 0.5F);
                        break;
                    case NONE:
                    default:
                        this.spawnPoweredParticle(worldIn, rand, pos, blockAndStrengthToColorMap.get(this).get(i),
                                Direction.DOWN, direction, 0.0F, 0.3F);
                }
            }
        }
    }
}