package mrp_v2.morewires.block;

import net.minecraft.block.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public abstract class AdjustedRedstoneDiodeBlock extends RedstoneDiodeBlock
{
    protected AdjustedRedstoneDiodeBlock(Properties builder)
    {
        super(builder);
    }

    @Override protected int calculateInputStrength(World worldIn, BlockPos pos, BlockState state)
    {
        Direction direction = state.get(HORIZONTAL_FACING);
        BlockPos blockpos = pos.offset(direction);
        int i = worldIn.getRedstonePower(blockpos, direction);
        if (i >= 15)
        {
            return i;
        } else
        {
            BlockState blockstate = worldIn.getBlockState(blockpos);
            return Math.max(i, AdjustedRedstoneWireBlock.isWireBlock(blockstate.getBlock()) ?
                    blockstate.get(RedstoneWireBlock.POWER) :
                    0);
        }
    }

    @Override protected int getPowerOnSide(IWorldReader worldIn, BlockPos pos, Direction side)
    {
        BlockState blockstate = worldIn.getBlockState(pos);
        Block block = blockstate.getBlock();
        if (this.isAlternateInput(blockstate))
        {
            if (block == Blocks.REDSTONE_BLOCK)
            {
                return 15;
            } else
            {
                return AdjustedRedstoneWireBlock.isWireBlock(block) ?
                        blockstate.get(RedstoneWireBlock.POWER) :
                        worldIn.getStrongPower(pos, side);
            }
        } else
        {
            return 0;
        }
    }
}
