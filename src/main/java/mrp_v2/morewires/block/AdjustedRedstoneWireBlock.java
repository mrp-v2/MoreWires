package mrp_v2.morewires.block;

import mrp_v2.morewires.item.AdjustedRedstoneItem;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Items;
import net.minecraft.state.properties.RedstoneSide;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;

public class AdjustedRedstoneWireBlock extends RedstoneWireBlock
{
    private static final HashMap<AdjustedRedstoneWireBlock, HashMap<Integer, Pair<Integer, Vector3f>>>
            blockAndStrengthToColorMap = new HashMap<>();
    private static final HashSet<Block> redstoneWires = new HashSet<>();

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
}
