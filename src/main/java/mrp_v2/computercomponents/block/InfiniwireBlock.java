package mrp_v2.computercomponents.block;

import mrp_v2.computercomponents.util.ObjectHolder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Random;

public class InfiniwireBlock extends AltRedstoneWireBlock
{
    public static final String ID = "infiniwire";
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
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

    public InfiniwireBlock()
    {
        super(Properties.from(Blocks.REDSTONE_WIRE));
        this.setRegistryName(ID);
        this.setDefaultState(this.getDefaultState().with(POWERED, false));
        this.sideBaseState = this.sideBaseState.with(POWERED, false);
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
        if (!state.get(POWERED) && !updatePowered(world, pos))
        {
            return;
        }
        int newSignal = this.getStrongestSignal(world, pos);
        if (state.get(POWER) != newSignal)
        {
            if (world.getBlockState(pos) == state)
            {
                setPower(world, pos, newSignal);
            }
            updateNeighbors(world, pos, newSignal);
        }
    }

    @Override protected int getStrongestSignal(World world, BlockPos pos)
    {
        canProvidePower = false;
        int i = world.getRedstonePowerFromNeighbors(pos);
        canProvidePower = true;
        return i;
    }

    @Override
    protected boolean canThisConnectTo(BlockState blockState, IBlockReader world, BlockPos pos,
            @Nullable Direction side)
    {
        return !blockState.isIn(ObjectHolder.ALT_REDSTONE_WIRE_BLOCK) &&
                RedstoneWireBlock.canConnectTo(blockState, world, pos, side);
    }

    private boolean updatePowered(World world, BlockPos pos)
    {
        boolean isPowered = getStrongestSignal(world, pos) > 0;
        BlockState state = world.getBlockState(pos);
        if (state.get(POWERED) != isPowered)
        {
            world.setBlockState(pos, state.with(POWERED, isPowered), 2);
        }
        return isPowered;
    }

    protected void updateNeighbors(World world, BlockPos pos, int strength)
    {
        HashSet<BlockPos> blocksToUpdate = new HashSet<>();
        blocksToUpdate.add(pos);
        for (Direction direction : Direction.values())
        {
            blocksToUpdate.add(pos.offset(direction));
        }
        for (BlockPos blockpos : blocksToUpdate)
        {
            BlockState state = world.getBlockState(blockpos);
            if (state.getBlock() == this)
            {
                updateInfiniwireChain(world, blockpos, strength);
            } else
            {
                world.notifyNeighborsOfStateChange(blockpos, this);
            }
        }
    }

    private void updateInfiniwireChain(World world, BlockPos pos, int strength)
    {
        int targetPower = Math.max(this.getStrongestSignal(world, pos), strength);
        if (targetPower != world.getBlockState(pos).get(POWER))
        {
            setPower(world, pos, targetPower);
            updateNeighbors(world, pos, targetPower);
        }
    }

    private void setPower(World world, BlockPos pos, int power)
    {
        world.setBlockState(pos, world.getBlockState(pos).with(POWER, power), 2);
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
                        this.addParticle(worldIn, rand, pos, powerRGB[i], direction, Direction.UP, -0.5F, 0.5F);
                    case SIDE:
                        this.addParticle(worldIn, rand, pos, powerRGB[i], Direction.DOWN, direction, 0.0F, 0.5F);
                        break;
                    case NONE:
                    default:
                        this.addParticle(worldIn, rand, pos, powerRGB[i], Direction.DOWN, direction, 0.0F, 0.3F);
                }
            }
        }
    }

    @Override protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        super.fillStateContainer(builder);
        builder.add(POWERED);
    }

    private void addParticle(World world, Random random, BlockPos pos, Vector3f color, Direction direction1,
            Direction direction2, float f1, float f2)
    {
        float f3 = f2 - f1;
        if (!(random.nextFloat() >= 0.2F * f3))
        {
            float f5 = f1 + f3 * random.nextFloat();
            double xOffset = 0.5D + (0.4375F * direction1.getXOffset()) + (f5 * direction2.getXOffset());
            double yOffset = 0.5D + (0.4375F * direction1.getYOffset()) + (f5 * direction2.getYOffset());
            double zOffset = 0.5D + (0.4375F * direction1.getZOffset()) + (f5 * direction2.getZOffset());
            world.addParticle(new RedstoneParticleData(color.getX(), color.getY(), color.getZ(), 1.0F),
                    pos.getX() + xOffset, pos.getY() + yOffset, pos.getZ() + zOffset, 0.0D, 0.0D, 0.0D);
        }
    }
}