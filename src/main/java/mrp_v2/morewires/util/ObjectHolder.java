package mrp_v2.morewires.util;

import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.types.Type;
import mrp_v2.morewires.MoreWires;
import mrp_v2.morewires.block.AdjustedComparatorBlock;
import mrp_v2.morewires.block.AdjustedRedstoneWireBlock;
import mrp_v2.morewires.block.AdjustedRepeaterBlock;
import mrp_v2.morewires.block.InfiniwireBlock;
import mrp_v2.morewires.item.AdjustedRedstoneItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.ComparatorTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.TypeReferences;
import net.minecraftforge.common.Tags;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.commons.lang3.tuple.Triple;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = MoreWires.ID, bus = Mod.EventBusSubscriber.Bus.MOD) public class ObjectHolder
{
    public static final AdjustedRedstoneWireBlock[] WIRE_BLOCKS;
    public static final AdjustedRedstoneWireBlock[] WIRE_BLOCKS_EXCLUDING_REDSTONE;
    public static final AdjustedRedstoneItem[] WIRE_BLOCK_ITEMS;
    public static final AdjustedRedstoneItem[] WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE;
    public static final InfiniwireBlock[] INFINIWIRE_BLOCKS;
    public static final AdjustedRedstoneItem[] INFINIWIRE_BLOCK_ITEMS;
    public static final ArrayList<Triple<Float, String, Tag<Item>>> COLORS = new ArrayList<>();
    public static final Tag<Item> WIRES_TAG = new ItemTags.Wrapper(Util.makeResourceLocation("wires"));
    public static final Tag<Item> INFINIWIRES_TAG = new ItemTags.Wrapper(Util.makeResourceLocation("infiniwires"));
    public static final AdjustedComparatorBlock ADJUSTED_COMPARATOR_BLOCK = new AdjustedComparatorBlock();
    private static final ArrayList<Item> queuedItems = new ArrayList<>();

    static
    {
        COLORS.add(Triple.of(0.0F, "red", Tags.Items.DYES_RED));
        COLORS.add(Triple.of(2.0F / 3.0F, "blue", Tags.Items.DYES_BLUE));
        COLORS.add(Triple.of(1.0F / 3.0F, "green", Tags.Items.DYES_GREEN));
        COLORS.add(Triple.of(1.0F / 12.0F, "orange", Tags.Items.DYES_ORANGE));
        COLORS.add(Triple.of(5.0F / 6.0F, "pink", Tags.Items.DYES_PINK));
        COLORS.add(Triple.of(1.0F / 6.0F, "yellow", Tags.Items.DYES_YELLOW));
        WIRE_BLOCKS = new AdjustedRedstoneWireBlock[COLORS.size()];
        WIRE_BLOCKS_EXCLUDING_REDSTONE = new AdjustedRedstoneWireBlock[COLORS.size() - 1];
        WIRE_BLOCK_ITEMS = new AdjustedRedstoneItem[COLORS.size()];
        WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE = new AdjustedRedstoneItem[COLORS.size() - 1];
        int i = 0;
        WIRE_BLOCKS[i] = new AdjustedRedstoneWireBlock(COLORS.get(i).getLeft(), "minecraft:redstone");
        WIRE_BLOCK_ITEMS[i] = WIRE_BLOCKS[i].createBlockItem(COLORS.get(i).getRight());
        i++;
        for (Triple<Float, String, Tag<Item>> color : COLORS.subList(1, COLORS.size()))
        {
            WIRE_BLOCKS[i] = new AdjustedRedstoneWireBlock(color.getLeft(), color.getMiddle());
            WIRE_BLOCKS_EXCLUDING_REDSTONE[i - 1] = WIRE_BLOCKS[i];
            WIRE_BLOCK_ITEMS[i] = WIRE_BLOCKS[i].createBlockItem(color.getRight());
            WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE[i - 1] = WIRE_BLOCK_ITEMS[i];
            i++;
        }
        INFINIWIRE_BLOCKS = new InfiniwireBlock[COLORS.size()];
        INFINIWIRE_BLOCK_ITEMS = new AdjustedRedstoneItem[COLORS.size()];
        i = 0;
        for (Triple<Float, String, Tag<Item>> color : COLORS)
        {
            INFINIWIRE_BLOCKS[i] = new InfiniwireBlock(color.getLeft(), color.getMiddle());
            INFINIWIRE_BLOCK_ITEMS[i] = INFINIWIRE_BLOCKS[i].createBlockItem(color.getRight());
            i++;
        }
    }

    @SubscribeEvent public static void registerBlocks(final RegistryEvent.Register<Block> event)
    {
        Util.doOperationOn(event.getRegistry()::register, WIRE_BLOCKS);
        Util.doOperationOn(event.getRegistry()::register, INFINIWIRE_BLOCKS);
        event.getRegistry()
                .registerAll(queueRedstoneGroupBlockItem(ADJUSTED_COMPARATOR_BLOCK),
                        queueRedstoneGroupBlockItem(new AdjustedRepeaterBlock()));
    }

    private static Block queueRedstoneGroupBlockItem(Block block)
    {
        Item blockItem = new BlockItem(block, new Item.Properties().group(ItemGroup.REDSTONE));
        blockItem.setRegistryName(block.getRegistryName());
        queuedItems.add(blockItem);
        return block;
    }

    @SubscribeEvent public static void registerItems(final RegistryEvent.Register<Item> event)
    {
        Util.doOperationOn(event.getRegistry()::register, WIRE_BLOCK_ITEMS);
        Util.doOperationOn(event.getRegistry()::register, INFINIWIRE_BLOCK_ITEMS);
        event.getRegistry().registerAll(queuedItems.toArray(new Item[0]));
        queuedItems.clear();
    }

    @SubscribeEvent public static void registerTileEntityTypes(final RegistryEvent.Register<TileEntityType<?>> event)
    {
        event.getRegistry().registerAll(createAdjustedComparatorTileEntityType());
    }

    private static TileEntityType<ComparatorTileEntity> createAdjustedComparatorTileEntityType()
    {
        String key = "comparator";
        TileEntityType.Builder<ComparatorTileEntity> builder =
                TileEntityType.Builder.create(ComparatorTileEntity::new, ADJUSTED_COMPARATOR_BLOCK);
        Type<?> type = null;
        try
        {
            type = DataFixesManager.getDataFixer()
                    .getSchema(DataFixUtils.makeKey(SharedConstants.getVersion().getWorldVersion()))
                    .getChoiceType(TypeReferences.BLOCK_ENTITY, key);
        } catch (IllegalArgumentException illegalargumentexception)
        {
            LogManager.getLogger().error("No data fixer registered for block entity {}", key);
            if (SharedConstants.developmentMode)
            {
                throw illegalargumentexception;
            }
        }
        TileEntityType<ComparatorTileEntity> tileEntityType = builder.build(type);
        tileEntityType.setRegistryName(key);
        return tileEntityType;
    }
}
