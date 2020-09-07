package mrp_v2.computercomponents.block;

import mrp_v2.computercomponents.util.ObjectHolder;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class AltRedstoneWireBlock extends RedstoneWireBlock
{
    private static final ArrayList<AltRedstoneWireBlock> redstoneWireBlocks = new ArrayList<>();
    protected static boolean canProvidePower = true;

    public AltRedstoneWireBlock()
    {
        this(Properties.from(Blocks.REDSTONE_WIRE));
        this.setRegistryName(Blocks.REDSTONE_WIRE.getRegistryName());
    }

    protected AltRedstoneWireBlock(Properties properties)
    {
        super(properties);
        redstoneWireBlocks.add(this);
    }

    public BlockItem createBlockItem()
    {
        BlockItem item = new BlockItem(this, new Item.Properties().group(ItemGroup.REDSTONE));
        item.setRegistryName(Items.REDSTONE.getRegistryName());
        return item;
    }

    @Override
    public void updateDiagonalNeighbors(BlockState state, IWorld worldIn, BlockPos pos, int flags, int recursionLeft)
    {
        BlockPos.Mutable mutableBlockPos = new BlockPos.Mutable();
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            RedstoneSide redstoneside = state.get(FACING_PROPERTY_MAP.get(direction));
            if (redstoneside != RedstoneSide.NONE &&
                    !isWire(worldIn.getBlockState(mutableBlockPos.setAndMove(pos, direction))))
            {
                mutableBlockPos.move(Direction.DOWN);
                BlockState blockstate = worldIn.getBlockState(mutableBlockPos);
                if (!blockstate.isIn(Blocks.OBSERVER))
                {
                    BlockPos blockpos = mutableBlockPos.offset(direction.getOpposite());
                    BlockState blockstate1 =
                            blockstate.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(blockpos),
                                    worldIn, mutableBlockPos, blockpos);
                    replaceBlockState(blockstate, blockstate1, worldIn, mutableBlockPos, flags, recursionLeft);
                }
                mutableBlockPos.setAndMove(pos, direction).move(Direction.UP);
                BlockState blockstate3 = worldIn.getBlockState(mutableBlockPos);
                if (!blockstate3.isIn(Blocks.OBSERVER))
                {
                    BlockPos blockpos1 = mutableBlockPos.offset(direction.getOpposite());
                    BlockState blockstate2 =
                            blockstate3.updatePostPlacement(direction.getOpposite(), worldIn.getBlockState(blockpos1),
                                    worldIn, mutableBlockPos, blockpos1);
                    replaceBlockState(blockstate3, blockstate2, worldIn, mutableBlockPos, flags, recursionLeft);
                }
            }
        }
    }

    @Override
    protected RedstoneSide recalculateSide(IBlockReader reader, BlockPos pos, Direction direction,
            boolean nonNormalCubeAbove)
    {
        BlockPos offsetPos = pos.offset(direction);
        BlockState offsetState = reader.getBlockState(offsetPos);
        if (nonNormalCubeAbove)
        {
            boolean canPlaceOnTopOf = this.canPlaceOnTopOf(reader, offsetPos, offsetState);
            if (canPlaceOnTopOf &&
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

    /**
     * Checks for a change in signal strength and does updates if there are changes
     */
    @Override protected void func_235547_a_(World world, BlockPos pos, BlockState state)
    {
        super.func_235547_a_(world, pos, state);
    }

    @Override protected int getStrongestSignal(World world, BlockPos pos)
    {
        canProvidePower = false;
        int i = world.getRedstonePowerFromNeighbors(pos);
        canProvidePower = true;
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

    @Override protected int getPower(BlockState state)
    {
        return isWire(state) ? state.get(POWER) : 0;
    }

    @Override protected void notifyWireNeighborsOfStateChange(World worldIn, BlockPos pos)
    {
        if (isWire(worldIn.getBlockState(pos)))
        {
            worldIn.notifyNeighborsOfStateChange(pos, this);
            for (Direction direction : Direction.values())
            {
                worldIn.notifyNeighborsOfStateChange(pos.offset(direction), this);
            }
        }
    }

    @Override public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return !canProvidePower ? 0 : blockState.getWeakPower(blockAccess, pos, side);
    }

    @Override public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        if (canProvidePower && side != Direction.DOWN)
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
        return canProvidePower;
    }

    protected boolean canThisConnectTo(BlockState blockState, IBlockReader world, BlockPos pos,
            @Nullable Direction side)
    {
        return !blockState.isIn(ObjectHolder.INFINIWIRE_BLOCK) &&
                RedstoneWireBlock.canConnectTo(blockState, world, pos, side);
    }

    protected static boolean isWire(BlockState state)
    {
        return redstoneWireBlocks.contains(state.getBlock());
    }
}
