package mrp_v2.morewires.block;

import mrp_v2.morewires.item.AdjustedRedstoneItem;
import net.minecraft.block.*;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.particles.RedstoneParticleData;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class AdjustedRedstoneWireBlock extends RedstoneWireBlock
{
    private static final HashMap<AdjustedRedstoneWireBlock, HashMap<Integer, Pair<Integer, Vector3f>>>
            blockAndStrengthToColorMap = new HashMap<>();
    private static final HashSet<Block> redstoneWires = new HashSet<>();
    protected static boolean globalCanProvidePower = true;

    public AdjustedRedstoneWireBlock(float hueChange, String id)
    {
        this(Properties.from(Blocks.REDSTONE_WIRE), hueChange, id + "_wire");
    }

    protected AdjustedRedstoneWireBlock(Properties properties, float hueChange, String id)
    {
        super(properties);
        this.setRegistryName(id);
        redstoneWires.add(this);
        blockAndStrengthToColorMap.put(this, calculateColors(hueChange));
    }

    protected static HashMap<Integer, Pair<Integer, Vector3f>> calculateColors(float hueChange)
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
            int originalColorInt = RedstoneWireBlock.colorMultiplier(i);
            float[] hsb = Color.RGBtoHSB((originalColorInt >> 16) & 0xFF, (originalColorInt >> 8) & 0xFF,
                    originalColorInt & 0xFF, null);
            hsb[0] += hueChange;
            if (hsb[0] > 1)
            {
                hsb[0]--;
            }
            int color = Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]);
            Vector3f colorVec = new Vector3f(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F,
                    (color & 0xFF) / 255.0F);
            colors.put(i, Pair.of(color, colorVec));
        }
        return colors;
    }

    public static boolean isWireBlock(BlockState state)
    {
        return isWireBlock(state.getBlock());
    }

    public static boolean isWireBlock(Block block)
    {
        return redstoneWires.contains(block);
    }

    public static int getColor(BlockState state)
    {
        return blockAndStrengthToColorMap.get(state.getBlock()).get(state.get(POWER)).getLeft();
    }

    public AdjustedRedstoneItem createBlockItem(Tag<Item> dyeTag)
    {
        AdjustedRedstoneItem item =
                new AdjustedRedstoneItem(this, new Item.Properties().group(ItemGroup.REDSTONE), dyeTag);
        if (this.getRegistryName().equals(Blocks.REDSTONE_WIRE.getRegistryName()))
        {
            item.setRegistryName(Items.REDSTONE.getRegistryName());
        } else
        {
            item.setRegistryName(this.getRegistryName());
        }
        return item;
    }

    @Override public int hashCode()
    {
        return this.getRegistryName().hashCode();
    }

    @Override public boolean equals(Object obj)
    {
        if (obj == this)
        {
            return true;
        }
        if (obj == null)
        {
            return false;
        }
        if (!(obj instanceof AdjustedRedstoneWireBlock))
        {
            return false;
        }
        AdjustedRedstoneWireBlock other = (AdjustedRedstoneWireBlock) obj;
        return this.getRegistryName().equals(other.getRegistryName());
    }

    @Override protected RedstoneSide getSide(IBlockReader worldIn, BlockPos pos, Direction face)
    {
        BlockPos offsetPos = pos.offset(face);
        BlockState offsetState = worldIn.getBlockState(offsetPos);
        BlockPos upPos = pos.up();
        BlockState upState = worldIn.getBlockState(upPos);
        if (!upState.isNormalCube(worldIn, upPos))
        {
            boolean canPlaceOnTopOfOffset = offsetState.isSolidSide(worldIn, offsetPos, Direction.UP) ||
                    offsetState.getBlock() == Blocks.HOPPER;
            if (canPlaceOnTopOfOffset &&
                    this.canThisConnectTo(worldIn.getBlockState(offsetPos.up()), worldIn, offsetPos.up(), null))
            {
                if (offsetState.isCollisionShapeOpaque(worldIn, offsetPos))
                {
                    return RedstoneSide.UP;
                }
                return RedstoneSide.SIDE;
            }
        }
        return !this.canThisConnectTo(offsetState, worldIn, offsetPos, face) &&
                (offsetState.isNormalCube(worldIn, offsetPos) ||
                        !this.canThisConnectTo(worldIn.getBlockState(offsetPos.down()), worldIn, offsetPos.down(),
                                null)) ? RedstoneSide.NONE : RedstoneSide.SIDE;
    }

    @Override protected BlockState updatePower(World worldIn, BlockPos pos, BlockState state)
    {
        BlockState blockstate = state;
        int i = state.get(POWER);
        globalCanProvidePower = false;
        int j = worldIn.getRedstonePowerFromNeighbors(pos);
        globalCanProvidePower = true;
        int maxSignal = 0;
        if (j < 15)
        {
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                BlockPos offsetPos = pos.offset(direction);
                BlockState offsetState = worldIn.getBlockState(offsetPos);
                maxSignal = this.maxSignal(maxSignal, offsetState);
                BlockPos upPos = pos.up();
                if (offsetState.isNormalCube(worldIn, offsetPos) &&
                        !worldIn.getBlockState(upPos).isNormalCube(worldIn, upPos))
                {
                    maxSignal = this.maxSignal(maxSignal, worldIn.getBlockState(offsetPos.up()));
                } else if (!offsetState.isNormalCube(worldIn, offsetPos))
                {
                    maxSignal = this.maxSignal(maxSignal, worldIn.getBlockState(offsetPos.down()));
                }
            }
        }
        int maxSignalMinus1 = maxSignal - 1;
        if (j > maxSignalMinus1)
        {
            maxSignalMinus1 = j;
        }
        if (i != maxSignalMinus1)
        {
            state = state.with(POWER, maxSignalMinus1);
            if (worldIn.getBlockState(pos) == blockstate)
            {
                worldIn.setBlockState(pos, state, 2);
            }
            this.blocksNeedingUpdate.add(pos);
            for (Direction direction1 : Direction.values())
            {
                this.blocksNeedingUpdate.add(pos.offset(direction1));
            }
        }
        return state;
    }

    @Override public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return !globalCanProvidePower ? 0 : blockState.getWeakPower(blockAccess, pos, side);
    }

    @Override public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        if (!globalCanProvidePower)
        {
            return 0;
        } else
        {
            int power = blockState.get(POWER);
            if (power == 0)
            {
                return 0;
            } else if (side == Direction.UP)
            {
                return power;
            } else
            {
                EnumSet<Direction> connectedDirections = EnumSet.noneOf(Direction.class);
                for (Direction horizontalDir : Direction.Plane.HORIZONTAL)
                {
                    if (this.isPowerSourceAt(blockAccess, pos, horizontalDir))
                    {
                        connectedDirections.add(horizontalDir);
                    }
                }
                if (side.getAxis().isHorizontal() && connectedDirections.isEmpty())
                {
                    return power;
                } else
                {
                    return connectedDirections.contains(side) &&
                            !connectedDirections.contains(side.rotateYCCW()) &&
                            !connectedDirections.contains(side.rotateY()) ? power : 0;
                }
            }
        }
    }

    @Override protected boolean isPowerSourceAt(IBlockReader worldIn, BlockPos pos, Direction side)
    {
        BlockPos sidePos = pos.offset(side);
        BlockState sideState = worldIn.getBlockState(sidePos);
        boolean sideIsNormalCube = sideState.isNormalCube(worldIn, sidePos);
        BlockPos upPos = pos.up();
        boolean upIsNormalCube = worldIn.getBlockState(upPos).isNormalCube(worldIn, upPos);
        if (!upIsNormalCube &&
                sideIsNormalCube &&
                canThisConnectTo(worldIn.getBlockState(sidePos.up()), worldIn, sidePos.up(), null))
        {
            return true;
        } else if (canThisConnectTo(sideState, worldIn, sidePos, side))
        {
            return true;
        } else if (sideState.getBlock() == Blocks.REPEATER &&
                sideState.get(RedstoneDiodeBlock.POWERED) &&
                sideState.get(RedstoneDiodeBlock.HORIZONTAL_FACING) == side)
        {
            return true;
        } else
        {
            return !sideIsNormalCube &&
                    canThisConnectTo(worldIn.getBlockState(sidePos.down()), worldIn, sidePos.down(), null);
        }
    }

    @Override public boolean canProvidePower(BlockState state)
    {
        return globalCanProvidePower;
    }

    @Override public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        int i = stateIn.get(POWER);
        Vector3f color = blockAndStrengthToColorMap.get(this).get(i).getRight();
        if (i != 0)
        {
            double d0 = (double) pos.getX() + 0.5D + ((double) rand.nextFloat() - 0.5D) * 0.2D;
            double d1 = (float) pos.getY() + 0.0625F;
            double d2 = (double) pos.getZ() + 0.5D + ((double) rand.nextFloat() - 0.5D) * 0.2D;
            worldIn.addParticle(new RedstoneParticleData(color.getX(), color.getY(), color.getZ(), 1.0F), d0, d1, d2,
                    0.0D, 0.0D, 0.0D);
        }
    }

    protected boolean canThisConnectTo(BlockState blockState, IBlockReader world, BlockPos pos,
            @Nullable Direction side)
    {
        if (blockState.getBlock() == this)
        {
            return true;
        }
        if (redstoneWires.contains(blockState.getBlock()))
        {
            return false;
        }
        return RedstoneWireBlock.canConnectTo(blockState, world, pos, side);
    }
}
