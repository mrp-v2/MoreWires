package mrp_v2.morewires.block;

import mrp_v2.morewires.item.AdjustedRedstoneItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.math.vector.Vector3i;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

public class AdjustedRedstoneWireBlock extends RedstoneWireBlock
{
    private static final HashMap<AdjustedRedstoneWireBlock, HashMap<Integer, Pair<Integer, Vector3f>>>
            blockAndStrengthToColorMap = new HashMap<>();
    private static final HashSet<Block> redstoneWires = new HashSet<>();
    protected static boolean globalCanProvidePower = true;

    public AdjustedRedstoneWireBlock(float hueChange)
    {
        this(Properties.from(Blocks.REDSTONE_WIRE), hueChange);
    }

    protected AdjustedRedstoneWireBlock(Properties properties, float hueChange)
    {
        super(properties);
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
            Vector3f colorVec = new Vector3f(((color >> 16) & 0xFF) / 255.0F, ((color >> 8) & 0xFF) / 255.0F,
                    (color & 0xFF) / 255.0F);
            colors.put(i, Pair.of(color, colorVec));
        }
        return colors;
    }

    public static int getColor(BlockState state)
    {
        return blockAndStrengthToColorMap.get(state.getBlock()).get(state.get(POWER)).getLeft();
    }

    protected boolean isWireBlock(BlockState state)
    {
        return isWireBlock(state.getBlock());
    }

    protected boolean isWireBlock(Block block)
    {
        return redstoneWires.contains(block);
    }

    public AdjustedRedstoneItem createBlockItem(ITag<Item> dyeTag)
    {
        return new AdjustedRedstoneItem(this, new Item.Properties().group(ItemGroup.REDSTONE), dyeTag);
    }

    @Override
    protected RedstoneSide recalculateSide(IBlockReader reader, BlockPos pos, Direction direction,
            boolean nonNormalCubeAbove)
    {
        BlockPos offsetPos = pos.offset(direction);
        BlockState offsetState = reader.getBlockState(offsetPos);
        if (nonNormalCubeAbove)
        {
            boolean canPlaceOnTopOfOffset = this.canPlaceOnTopOf(reader, offsetPos, offsetState);
            if (canPlaceOnTopOfOffset &&
                    this.canThisConnectTo(reader.getBlockState(offsetPos.up()), reader, offsetPos.up(), null))
            {
                if (offsetState.isSolidSide(reader, offsetPos, direction.getOpposite()))
                {
                    return RedstoneSide.UP;
                }
                return RedstoneSide.SIDE;
            }
        }
        return !this.canThisConnectTo(offsetState, reader, offsetPos, direction) &&
                (offsetState.isNormalCube(reader, offsetPos) ||
                        !this.canThisConnectTo(reader.getBlockState(offsetPos.down()), reader, offsetPos.down(),
                                null)) ? RedstoneSide.NONE : RedstoneSide.SIDE;
    }

    @Override protected int getStrongestSignal(World world, BlockPos pos)
    {
        globalCanProvidePower = false;
        int i = world.getRedstonePowerFromNeighbors(pos);
        globalCanProvidePower = true;
        int j = 0;
        if (i < 15)
        {
            for (Direction direction : Direction.Plane.HORIZONTAL)
            {
                BlockPos offsetPos = pos.offset(direction);
                BlockState offsetState = world.getBlockState(offsetPos);
                j = Math.max(j, this.getPower(offsetState));
                BlockPos upPos = pos.up();
                if (offsetState.isNormalCube(world, offsetPos) &&
                        !world.getBlockState(upPos).isNormalCube(world, upPos))
                {
                    j = Math.max(j, this.getPower(world.getBlockState(offsetPos.up())));
                } else if (!offsetState.isNormalCube(world, offsetPos))
                {
                    j = Math.max(j, this.getPower(world.getBlockState(offsetPos.down())));
                }
            }
        }
        return Math.max(i, j - 1);
    }

    @Override public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return !globalCanProvidePower ? 0 : blockState.getWeakPower(blockAccess, pos, side);
    }

    @Override public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        if (globalCanProvidePower && side != Direction.DOWN)
        {
            int i = blockState.get(POWER);
            if (i == 0)
            {
                return 0;
            } else
            {
                return side != Direction.UP &&
                        !this.getUpdatedState(blockAccess, blockState, pos)
                                .get(FACING_PROPERTY_MAP.get(side.getOpposite()))
                                .func_235921_b_() ? 0 : i;
            }
        } else
        {
            return 0;
        }
    }

    @Override public boolean canProvidePower(BlockState state)
    {
        return globalCanProvidePower;
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

    protected boolean canThisConnectTo(BlockState blockState, IBlockReader world, BlockPos pos,
            @Nullable Direction side)
    {
        if (blockState.isIn(this))
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
