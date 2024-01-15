package com.morewires.block;

import com.google.common.collect.Sets;
import com.morewires.item.AdjustedRedstoneItem;
import com.morewires.util.Color;
import net.minecraft.block.*;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;

public class AdjustedRedstoneWireBlock extends RedstoneWireBlock {
    private static final HashMap<AdjustedRedstoneWireBlock, HashMap<Integer, Pair<Integer, Vec3d>>>
            blockAndStrengthToColorMap = new HashMap<>();
    private static final HashSet<Block> redstoneWires = new HashSet<>();
    protected static boolean globalCanProvidePower = true;
    private final BlockState dotState;
    public AdjustedRedstoneWireBlock(int RGBInt) {
        this(Settings.copy(Blocks.REDSTONE_WIRE), RGBInt);
    }

    public AdjustedRedstoneWireBlock(Settings settings, int RGBInt) {
        super(settings);
        redstoneWires.add(this);
        blockAndStrengthToColorMap.put(this, calculateColors(RGBInt));
        this.dotState = this.getDefaultState().with(WIRE_CONNECTION_NORTH, WireConnection.SIDE).with(WIRE_CONNECTION_EAST, WireConnection.SIDE).with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE).with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
    }

    protected static HashMap<Integer, Pair<Integer, Vec3d>> calculateColors(int RGBInt) {
        Vec3d RGB = Color.rgbIntToVec3D(RGBInt);
        double A = 3;
        Vec3d RGB0 = RGB.multiply(1/A);
        Vec3d Multipliers = RGB.subtract(RGB0).multiply(1/15d);
        HashMap<Integer, Pair<Integer, Vec3d>> colors = new HashMap<>();
        for(int i = 0; i<16; i++){
            Vec3d Colors = RGB0.add(Multipliers.multiply(i));
            colors.put(i, Pair.of(Color.rgbToRgbInt(Colors), Colors.multiply((double) 1/255)));
        }
        return colors;
    } // Made another calculateColors method, and it's easier to understand (for me)


    public static int getColor(BlockState state) {
        return blockAndStrengthToColorMap.get(state.getBlock()).get(state.get(POWER)).getLeft();
    }

    public int getMaxColor(){
        return blockAndStrengthToColorMap.get(this).get(15).getLeft();
    }

    protected boolean isWireBlock(BlockState state) {
        return isWireBlock(state.getBlock());
    }

    protected boolean isWireBlock(Block block) {
        return redstoneWires.contains(block);
    }

    public AdjustedRedstoneItem createBlockItem(Item dye) {
        return new AdjustedRedstoneItem(this, new Item.Settings(), dye);
    }

    private WireConnection getRenderConnectionType(BlockView world, BlockPos pos, Direction direction) {
        return this.getRenderConnectionType(world, pos, direction, !world.getBlockState(pos.up()).isSolidBlock(world, pos));
    }

    protected WireConnection getRenderConnectionType(BlockView reader, BlockPos pos, Direction direction, boolean nonNormalCubeAbove) {
        BlockPos offsetPos = pos.offset(direction);
        BlockState offsetState = reader.getBlockState(offsetPos);
        if (nonNormalCubeAbove) {
            boolean canPlaceOnTopOfOffset = this.canRunOnTop(reader, offsetPos, offsetState);
            if (canPlaceOnTopOfOffset &&
                    this.canThisConnectTo(reader.getBlockState(offsetPos.up()), null)) {
                if (offsetState.isSideSolidFullSquare(reader, offsetPos, direction.getOpposite())) {
                    return WireConnection.UP;
                }
                return WireConnection.SIDE;
            }
        }
        return !this.canThisConnectTo(offsetState, direction) && (offsetState.isSolidBlock(reader, offsetPos) ||
                !this.canThisConnectTo(reader.getBlockState(offsetPos.down()), null)) ? WireConnection.NONE : WireConnection.SIDE;
    }

    private boolean canRunOnTop(BlockView world, BlockPos pos, BlockState floor) {
        return floor.isSideSolidFullSquare(world, pos, Direction.UP) || floor.isOf(Blocks.HOPPER);
    }

    private void update(World world, BlockPos pos, BlockState state) {
        int i = this.getReceivedRedstonePower(world, pos);
        if (state.get(POWER) != i) {
            if (world.getBlockState(pos) == state) {
                world.setBlockState(pos, state.with(POWER, i), Block.NOTIFY_LISTENERS);
            }
            HashSet<BlockPos> set = Sets.newHashSet();
            set.add(pos);
            for (Direction direction : Direction.values()) {
                set.add(pos.offset(direction));
            }
            for (BlockPos blockPos : set) {
                world.updateNeighborsAlways(blockPos, this);
            }
        }
    }
    protected int getReceivedRedstonePower(World world, BlockPos pos) {
        globalCanProvidePower = false;
        int i = world.getReceivedRedstonePower(pos);
        globalCanProvidePower = true;
        int j = 0;
        if (i < 15) {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                BlockPos offsetPos = pos.offset(direction);
                BlockState offsetState = world.getBlockState(offsetPos);
                j = Math.max(j, this.increasePower(offsetState));
                BlockPos upPos = pos.up();
                if (offsetState.isSolidBlock(world, offsetPos) &&
                        !world.getBlockState(upPos).isSolidBlock(world, upPos)) {
                    j = Math.max(j, this.increasePower(world.getBlockState(offsetPos.up())));
                } else if (!offsetState.isSolidBlock(world, offsetPos)) {
                    j = Math.max(j, this.increasePower(world.getBlockState(offsetPos.down())));
                }
            }
        }
        return Math.max(i, j - 1);
    }

    private int increasePower(BlockState state) {
        return state.isOf(this) ? state.get(POWER) : 0;
    }

    private void updateNeighbors(World world, BlockPos pos) {
        if (!world.getBlockState(pos).isOf(this)) {
            return;
        }
        world.updateNeighborsAlways(pos, this);
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (oldState.isOf(state.getBlock()) || world.isClient) {
            return;
        }
        this.update(world, pos, state);
        for (Direction direction : Direction.Type.VERTICAL) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
        this.updateOffsetNeighbors(world, pos);
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (moved || state.isOf(newState.getBlock())) {
            return;
        }
        super.onStateReplaced(state, world, pos, newState, moved);
        if (world.isClient) {
            return;
        }
        for (Direction direction : Direction.values()) {
            world.updateNeighborsAlways(pos.offset(direction), this);
        }
        this.update(world, pos, state);
        this.updateOffsetNeighbors(world, pos);
    }

    private void updateOffsetNeighbors(World world, BlockPos pos) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            this.updateNeighbors(world, pos.offset(direction));
        }
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            if (world.getBlockState(blockPos).isSolidBlock(world, blockPos)) {
                this.updateNeighbors(world, blockPos.up());
                continue;
            }
            this.updateNeighbors(world, blockPos.down());
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        if (world.isClient) {
            return;
        }
        if (state.canPlaceAt(world, pos)) {
            this.update(world, pos, state);
        } else {
            RedstoneWireBlock.dropStacks(state, world, pos);
            world.removeBlock(pos, false);
        }
    }

    @Override
    public int getStrongRedstonePower(BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side) {
        return !globalCanProvidePower ? 0 : blockState.getWeakRedstonePower(blockAccess, pos, side);
    }

    @Override
    public int getWeakRedstonePower(BlockState blockState, BlockView blockAccess, BlockPos pos, Direction side) {
        if (globalCanProvidePower && side != Direction.DOWN) {
            int i = blockState.get(POWER);
            if (i == 0) {
                return 0;
            } else {
                return side != Direction.UP &&
                        !this.getPlacementState(blockAccess, blockState, pos)
                                .get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(side.getOpposite()))
                                .isConnected() ? 0 : i;
            }
        } else {
            return 0;
        }
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        return globalCanProvidePower;
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getPlacementState(ctx.getWorld(), this.dotState, ctx.getBlockPos());
    }

    private BlockState getPlacementState(BlockView world, BlockState state, BlockPos pos) {
        boolean bl7;
        boolean bl = AdjustedRedstoneWireBlock.isNotConnected(state);
        state = this.getDefaultWireState(world, this.getDefaultState().with(POWER, state.get(POWER)), pos);
        if (bl && AdjustedRedstoneWireBlock.isNotConnected(state)) {
            return state;
        }
        boolean bl2 = state.get(WIRE_CONNECTION_NORTH).isConnected();
        boolean bl3 = state.get(WIRE_CONNECTION_SOUTH).isConnected();
        boolean bl4 = state.get(WIRE_CONNECTION_EAST).isConnected();
        boolean bl5 = state.get(WIRE_CONNECTION_WEST).isConnected();
        boolean bl6 = !bl2 && !bl3;
        boolean bl8 = bl7 = !bl4 && !bl5;
        if (!bl5 && bl6) {
            state = state.with(WIRE_CONNECTION_WEST, WireConnection.SIDE);
        }
        if (!bl4 && bl6) {
            state = state.with(WIRE_CONNECTION_EAST, WireConnection.SIDE);
        }
        if (!bl2 && bl7) {
            state = state.with(WIRE_CONNECTION_NORTH, WireConnection.SIDE);
        }
        if (!bl3 && bl7) {
            state = state.with(WIRE_CONNECTION_SOUTH, WireConnection.SIDE);
        }
        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            return state;
        }
        if (direction == Direction.UP) {
            return this.getPlacementState(world, state, pos);
        }
        WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction, !world.getBlockState(pos.up()).isSolidBlock(world, pos));
        if (wireConnection.isConnected() == state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected() && !AdjustedRedstoneWireBlock.isFullyConnected(state)) {
            return state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection);
        }
        return this.getPlacementState(world, (this.dotState.with(POWER, state.get(POWER))).with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection), pos);
    }

    private static boolean isFullyConnected(BlockState state) {
        return state.get(WIRE_CONNECTION_NORTH).isConnected() && state.get(WIRE_CONNECTION_SOUTH).isConnected() && state.get(WIRE_CONNECTION_EAST).isConnected() && state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    private static boolean isNotConnected(BlockState state) {
        return !state.get(WIRE_CONNECTION_NORTH).isConnected() && !state.get(WIRE_CONNECTION_SOUTH).isConnected() && !state.get(WIRE_CONNECTION_EAST).isConnected() && !state.get(WIRE_CONNECTION_WEST).isConnected();
    }

    private BlockState getDefaultWireState(BlockView world, BlockState state, BlockPos pos) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            if ((state.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected()) continue;
            WireConnection wireConnection = this.getRenderConnectionType(world, pos, direction);
            state = state.with(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction), wireConnection);
        }
        return state;
    }

    @Override
    public void randomDisplayTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
        int i = stateIn.get(POWER);
        if (i != 0) {
            for (Direction direction : Direction.Type.HORIZONTAL) {
                WireConnection redstoneside = stateIn.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction));
                switch (redstoneside) {
                    case UP:
                        this.addPoweredParticles(worldIn, rand, pos,
                                blockAndStrengthToColorMap.get(this).get(i).getRight(), direction, Direction.UP, -0.5F,
                                0.5F);
                    case SIDE:
                        this.addPoweredParticles(worldIn, rand, pos,
                                blockAndStrengthToColorMap.get(this).get(i).getRight(), Direction.DOWN, direction, 0.0F,
                                0.5F);
                        break;
                    case NONE:
                    default:
                        this.addPoweredParticles(worldIn, rand, pos,
                                blockAndStrengthToColorMap.get(this).get(i).getRight(), Direction.DOWN, direction, 0.0F,
                                0.3F);
                }
            }
        }
    }

    private void addPoweredParticles(World world, Random random, BlockPos pos, Vec3d color, Direction direction, Direction direction2, float f, float g) {
        float h = g - f;
        if (random.nextFloat() >= 0.2f * h) {
            return;
        }
        float i = 0.4375f;
        float j = f + h * random.nextFloat();
        double d = 0.5 + (double)(0.4375f * (float)direction.getOffsetX()) + (double)(j * (float)direction2.getOffsetX());
        double e = 0.5 + (double)(0.4375f * (float)direction.getOffsetY()) + (double)(j * (float)direction2.getOffsetY());
        double k = 0.5 + (double)(0.4375f * (float)direction.getOffsetZ()) + (double)(j * (float)direction2.getOffsetZ());
        world.addParticle(new DustParticleEffect(color.toVector3f(), 1.0f), (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + k, 0.0, 0.0, 0.0);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        }
        if (AdjustedRedstoneWireBlock.isFullyConnected(state) || AdjustedRedstoneWireBlock.isNotConnected(state)) {
            BlockState blockState = AdjustedRedstoneWireBlock.isFullyConnected(state) ? this.getDefaultState() : this.dotState;
            blockState = blockState.with(POWER, state.get(POWER));
            if ((blockState = this.getPlacementState(world, blockState, pos)) != state) {
                world.setBlockState(pos, blockState, Block.NOTIFY_ALL);
                this.updateForNewState(world, pos, state, blockState);
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    private void updateForNewState(World world, BlockPos pos, BlockState oldState, BlockState newState) {
        for (Direction direction : Direction.Type.HORIZONTAL) {
            BlockPos blockPos = pos.offset(direction);
            if ((oldState.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction))).isConnected() == newState.get(DIRECTION_TO_WIRE_CONNECTION_PROPERTY.get(direction)).isConnected() || !world.getBlockState(blockPos).isSolidBlock(world, blockPos)) continue;
            world.updateNeighborsExcept(blockPos, newState.getBlock(), direction.getOpposite());
        }
    }

    protected boolean canThisConnectTo(BlockState blockState, @Nullable Direction side) {
        if (blockState.isOf(this)) {
            return true;
        }
        if (redstoneWires.contains(blockState.getBlock())) {
            return false;
        }
        return RedstoneWireBlock.connectsTo(blockState, side);
    }
}
