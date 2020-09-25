package mrp_v2.morewires.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Random;

public class AdjustedRepeaterBlock extends AdjustedRedstoneDiodeBlock
{
    public static final BooleanProperty LOCKED = BlockStateProperties.LOCKED;
    public static final IntegerProperty DELAY = BlockStateProperties.DELAY_1_4;
    private final RepeaterBlock vanilla;

    public AdjustedRepeaterBlock()
    {
        super(Properties.from(Blocks.REPEATER));
        this.vanilla = (RepeaterBlock) Blocks.REPEATER;
        this.setDefaultState(this.stateContainer.getBaseState()
                .with(HORIZONTAL_FACING, Direction.NORTH)
                .with(DELAY, 1)
                .with(LOCKED, Boolean.FALSE)
                .with(POWERED, Boolean.FALSE));
        this.setRegistryName(vanilla.getRegistryName());
    }

    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn,
            BlockPos currentPos, BlockPos facingPos)
    {
        return vanilla.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @OnlyIn(Dist.CLIENT) public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        vanilla.animateTick(stateIn, worldIn, pos, rand);
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
            Hand handIn, BlockRayTraceResult hit)
    {
        return vanilla.onBlockActivated(state, worldIn, pos, player, handIn, hit);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING, DELAY, LOCKED, POWERED);
    }

    public boolean isLocked(IWorldReader worldIn, BlockPos pos, BlockState state)
    {
        return this.getPowerOnSides(worldIn, pos, state) > 0;
    }

    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        BlockState blockstate = super.getStateForPlacement(context);
        return blockstate.with(LOCKED, this.isLocked(context.getWorld(), context.getPos(), blockstate));
    }

    protected boolean isAlternateInput(BlockState state)
    {
        return isDiode(state);
    }

    protected int getDelay(BlockState state)
    {
        return state.get(DELAY) * 2;
    }
}
