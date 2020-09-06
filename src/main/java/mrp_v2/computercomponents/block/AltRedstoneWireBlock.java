package mrp_v2.computercomponents.block;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

public class AltRedstoneWireBlock extends RedstoneWireBlock
{
    public AltRedstoneWireBlock(Properties properties)
    {
        super(properties);
    }

    public AltRedstoneWireBlock()
    {
        super(Properties.from(Blocks.REDSTONE_WIRE));
        this.setRegistryName(Blocks.REDSTONE_WIRE.getRegistryName());
    }

    @Override public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        return !this.canProvidePower() ? 0 : blockState.getWeakPower(blockAccess, pos, side);
    }

    protected boolean canProvidePower()
    {
        return super.canProvidePower;
    }

    @Override public int getWeakPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side)
    {
        if (this.canProvidePower() && side != Direction.DOWN)
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
        return this.canProvidePower();
    }
}
