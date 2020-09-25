package mrp_v2.morewires.block;

import net.minecraft.block.*;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.ComparatorMode;
import net.minecraft.tileentity.ComparatorTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.TickPriority;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class AdjustedComparatorBlock extends AdjustedRedstoneDiodeBlock implements ITileEntityProvider
{
    public static final EnumProperty<ComparatorMode> MODE = BlockStateProperties.COMPARATOR_MODE;
    private final ComparatorBlock vanilla;

    public AdjustedComparatorBlock()
    {
        super(Properties.from(Blocks.COMPARATOR));
        this.vanilla = (ComparatorBlock) Blocks.COMPARATOR;
        this.setDefaultState(this.stateContainer.getBaseState()
                .with(HORIZONTAL_FACING, Direction.NORTH)
                .with(POWERED, Boolean.FALSE)
                .with(MODE, ComparatorMode.COMPARE));
        this.setRegistryName(vanilla.getRegistryName());
    }

    private int calculateOutput(World worldIn, BlockPos pos, BlockState state)
    {
        return state.get(MODE) == ComparatorMode.SUBTRACT ?
                Math.max(this.calculateInputStrength(worldIn, pos, state) - this.getPowerOnSides(worldIn, pos, state),
                        0) :
                this.calculateInputStrength(worldIn, pos, state);
    }

    protected int calculateInputStrength(World worldIn, BlockPos pos, BlockState state)
    {
        int i = super.calculateInputStrength(worldIn, pos, state);
        Direction direction = state.get(HORIZONTAL_FACING);
        BlockPos blockpos = pos.offset(direction);
        BlockState blockstate = worldIn.getBlockState(blockpos);
        if (blockstate.hasComparatorInputOverride())
        {
            i = blockstate.getComparatorInputOverride(worldIn, blockpos);
        } else if (i < 15 && blockstate.isNormalCube(worldIn, blockpos))
        {
            blockpos = blockpos.offset(direction);
            blockstate = worldIn.getBlockState(blockpos);
            if (blockstate.hasComparatorInputOverride())
            {
                i = blockstate.getComparatorInputOverride(worldIn, blockpos);
            } else if (blockstate.isAir(worldIn, blockpos))
            {
                ItemFrameEntity itemframeentity = this.findItemFrame(worldIn, direction, blockpos);
                if (itemframeentity != null)
                {
                    i = itemframeentity.getAnalogOutput();
                }
            }
        }
        return i;
    }

    @Nullable private ItemFrameEntity findItemFrame(World worldIn, Direction facing, BlockPos pos)
    {
        List<ItemFrameEntity> entitiesWithinAABB = worldIn.getEntitiesWithinAABB(ItemFrameEntity.class,
                new AxisAlignedBB(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1),
                (itemFrameEntity) -> itemFrameEntity != null && itemFrameEntity.getHorizontalFacing() == facing);
        return entitiesWithinAABB.size() == 1 ? entitiesWithinAABB.get(0) : null;
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
            Hand handIn, BlockRayTraceResult hit)
    {
        if (!player.abilities.allowEdit)
        {
            return ActionResultType.PASS;
        } else
        {
            state = state.cycle(MODE);
            float f = state.get(MODE) == ComparatorMode.SUBTRACT ? 0.55F : 0.5F;
            worldIn.playSound(player, pos, SoundEvents.BLOCK_COMPARATOR_CLICK, SoundCategory.BLOCKS, 0.3F, f);
            worldIn.setBlockState(pos, state, 2);
            this.onStateChange(worldIn, pos, state);
            return ActionResultType.SUCCESS;
        }
    }

    public boolean eventReceived(BlockState state, World worldIn, BlockPos pos, int id, int param)
    {
        return vanilla.eventReceived(state, worldIn, pos, id, param);
    }

    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(HORIZONTAL_FACING, MODE, POWERED);
    }

    private void onStateChange(World worldIn, BlockPos pos, BlockState state)
    {
        int output = this.calculateOutput(worldIn, pos, state);
        TileEntity tileEntity = worldIn.getTileEntity(pos);
        int j = 0;
        if (tileEntity instanceof ComparatorTileEntity)
        {
            ComparatorTileEntity comparatorTileEntity = (ComparatorTileEntity) tileEntity;
            j = comparatorTileEntity.getOutputSignal();
            comparatorTileEntity.setOutputSignal(output);
        }
        if (j != output || state.get(MODE) == ComparatorMode.COMPARE)
        {
            boolean shouldBePowered = this.shouldBePowered(worldIn, pos, state);
            boolean isPowered = state.get(POWERED);
            if (isPowered && !shouldBePowered)
            {
                worldIn.setBlockState(pos, state.with(POWERED, Boolean.FALSE), 2);
            } else if (!isPowered && shouldBePowered)
            {
                worldIn.setBlockState(pos, state.with(POWERED, Boolean.TRUE), 2);
            }
            this.notifyNeighbors(worldIn, pos, state);
        }
    }

    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random rand)
    {
        this.onStateChange(worldIn, pos, state);
    }

    protected void updateState(World worldIn, BlockPos pos, BlockState state)
    {
        if (!worldIn.getPendingBlockTicks().isTickPending(pos, this))
        {
            int i = this.calculateOutput(worldIn, pos, state);
            TileEntity tileentity = worldIn.getTileEntity(pos);
            int j = tileentity instanceof ComparatorTileEntity ?
                    ((ComparatorTileEntity) tileentity).getOutputSignal() :
                    0;
            if (i != j || state.get(POWERED) != this.shouldBePowered(worldIn, pos, state))
            {
                TickPriority tickpriority =
                        this.isFacingTowardsRepeater(worldIn, pos, state) ? TickPriority.HIGH : TickPriority.NORMAL;
                worldIn.getPendingBlockTicks().scheduleTick(pos, this, 2, tickpriority);
            }
        }
    }

    protected boolean shouldBePowered(World worldIn, BlockPos pos, BlockState state)
    {
        int i = this.calculateInputStrength(worldIn, pos, state);
        if (i == 0)
        {
            return false;
        } else
        {
            int j = this.getPowerOnSides(worldIn, pos, state);
            if (i > j)
            {
                return true;
            } else
            {
                return i == j && state.get(MODE) == ComparatorMode.COMPARE;
            }
        }
    }

    protected int getActiveSignal(IBlockReader worldIn, BlockPos pos, BlockState state)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof ComparatorTileEntity ? ((ComparatorTileEntity) tileentity).getOutputSignal() : 0;
    }

    protected int getDelay(BlockState state)
    {
        return 2;
    }

    public TileEntity createNewTileEntity(IBlockReader worldIn)
    {
        return vanilla.createNewTileEntity(worldIn);
    }

    @Override
    public void onNeighborChange(BlockState state, net.minecraft.world.IWorldReader world, BlockPos pos,
            BlockPos neighbor)
    {
        vanilla.onNeighborChange(state, world, pos, neighbor);
    }

    @Override public boolean getWeakChanges(BlockState state, net.minecraft.world.IWorldReader world, BlockPos pos)
    {
        return vanilla.getWeakChanges(state, world, pos);
    }
}
