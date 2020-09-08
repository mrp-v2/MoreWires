package mrp_v2.computercomponents.block;

import mrp_v2.computercomponents.util.ObjectHolder;
import mrp_v2.mrplibrary.world.WorldOverrideWrapper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class InfiniwireBlock extends AltRedstoneWireBlock
{
    public static final String ID = "infiniwire";
    private static final Vector3f[] powerRGB = new Vector3f[16];

    static
    {
        for (int i = 0; i <= 15; ++i)
        {
            float f = (float) i / 15.0F;
            float r = MathHelper.clamp(f * f * 0.6F - 0.7F, 0.0F, 1.0F);
            float g = MathHelper.clamp(f * f * 0.7F - 0.5F, 0.0F, 1.0F);
            float b = f * 0.6F + (f > 0.0F ? 0.4F : 0.3F);
            powerRGB[i] = new Vector3f(r, g, b);
        }
    }

    private boolean doingUpdate = false;

    public InfiniwireBlock()
    {
        super(Properties.from(Blocks.REDSTONE_WIRE));
        this.setRegistryName(ID);
    }

    public static int getColor(int power)
    {
        Vector3f vector3f = powerRGB[power];
        return MathHelper.rgb(vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }

    @Override public BlockItem createBlockItem()
    {
        BlockItem item = new BlockItem(this, new Item.Properties().group(ItemGroup.REDSTONE));
        item.setRegistryName(ID);
        return item;
    }

    @Override protected void func_235547_a_(World world, BlockPos pos, BlockState state)
    {
        if (!doingUpdate)
        {
            doingUpdate = true;
            HashMap<BlockPos, BlockState> overrides = new HashMap<>();
            overrides.put(pos, state);
            updateChain(new WorldOverrideWrapper(world, overrides), pos);
            doingUpdate = false;
        }
    }

    @Override
    protected boolean canThisConnectTo(BlockState blockState, IBlockReader world, BlockPos pos,
            @Nullable Direction side)
    {
        return !blockState.isIn(ObjectHolder.ALT_REDSTONE_WIRE_BLOCK) &&
                RedstoneWireBlock.canConnectTo(blockState, world, pos, side);
    }

    private void updateNeighbors(World world, HashSet<BlockPos> updatedBlocks)
    {
        for (BlockPos pos : updatedBlocks)
        {
            for (BlockPos blockpos : getRelevantNeighbors(pos))
            {
                if (world.getBlockState(blockpos).getBlock() != this)
                {
                    world.notifyNeighborsOfStateChange(blockpos, this);
                }
            }
        }
    }

    private HashSet<BlockPos> getRelevantNeighbors(BlockPos pos)
    {
        HashSet<BlockPos> relevantNeighbors = new HashSet<>();
        for (Direction direction : Direction.values())
        {
            relevantNeighbors.add(pos.offset(direction));
        }
        return relevantNeighbors;
    }

    private void setPower(World world, BlockPos pos, int power)
    {
        world.setBlockState(pos, world.getBlockState(pos).with(POWER, power), 2);
    }

    private HashSet<BlockPos> updateInfiniwireChain(World world, HashSet<BlockPos> chain, int strength)
    {
        HashSet<BlockPos> updatedBlocks = new HashSet<>();
        for (BlockPos pos : chain)
        {
            if (strength != world.getBlockState(pos).get(POWER))
            {
                setPower(world, pos, strength);
                updatedBlocks.add(pos);
            }
        }
        return updatedBlocks;
    }

    private void updateChain(WorldOverrideWrapper world, BlockPos pos)
    {
        HashSet<BlockPos> chain = getBlocksInChain(world, pos);
        int newStrength = getStrongestSignalChain(world.getWorld(), chain);
        updateNeighbors(world.getWorld(), updateInfiniwireChain(world.getWorld(), chain, newStrength));
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

    private HashSet<BlockPos> getBlocksInChain(WorldOverrideWrapper world, BlockPos pos)
    {
        HashSet<BlockPos> blocks = new HashSet<>();
        if (world.getBlockState(pos) == world.getWorld().getBlockState(pos))
        {
            blocks.add(pos);
        }
        getBlocksInChain(world.getWorld(), pos, blocks);
        return blocks;
    }

    private void getBlocksInChain(World world, BlockPos pos, HashSet<BlockPos> foundBlocks)
    {
        for (BlockPos neighborPos : getRelevantNeighbors(pos))
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
                        this.spawnPoweredParticle(worldIn, rand, pos, powerRGB[i], direction, Direction.UP, -0.5F,
                                0.5F);
                    case SIDE:
                        this.spawnPoweredParticle(worldIn, rand, pos, powerRGB[i], Direction.DOWN, direction, 0.0F,
                                0.5F);
                        break;
                    case NONE:
                    default:
                        this.spawnPoweredParticle(worldIn, rand, pos, powerRGB[i], Direction.DOWN, direction, 0.0F,
                                0.3F);
                }
            }
        }
    }
}