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
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class AltRedstoneWireBlock extends RedstoneWireBlock
{
    protected static boolean canProvidePower = true;

    public AltRedstoneWireBlock()
    {
        this(Properties.from(Blocks.REDSTONE_WIRE));
        this.setRegistryName(Blocks.REDSTONE_WIRE.getRegistryName());
    }

    protected AltRedstoneWireBlock(Properties properties)
    {
        super(properties);
    }

    public BlockItem createBlockItem()
    {
        BlockItem item = new BlockItem(this, new Item.Properties().group(ItemGroup.REDSTONE));
        item.setRegistryName(Items.REDSTONE.getRegistryName());
        return item;
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
        return blockState.isIn(this) ||
                !blockState.isIn(ObjectHolder.INFINIWIRE_BLOCK) &&
                        RedstoneWireBlock.canConnectTo(blockState, world, pos, side);
    }
}
