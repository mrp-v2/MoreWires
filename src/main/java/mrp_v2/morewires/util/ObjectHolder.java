package mrp_v2.morewires.util;

import mrp_v2.morewires.MoreWires;
import mrp_v2.morewires.block.AdjustedRedstoneWireBlock;
import mrp_v2.morewires.block.InfiniwireBlock;
import mrp_v2.morewires.item.AdjustedRedstoneItem;
import mrp_v2.morewires.item.InfiniwireItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;

public class ObjectHolder
{
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MoreWires.ID);
    public static final DeferredRegister<Block> VANILLA_BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, "minecraft");
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MoreWires.ID);
    public static final DeferredRegister<Item> VANILLA_ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, "minecraft");
    public static final HashMap<String, RegistryObject<AdjustedRedstoneWireBlock>> WIRE_BLOCKS;
    public static final HashMap<String, RegistryObject<AdjustedRedstoneWireBlock>> WIRE_BLOCKS_EXCLUDING_REDSTONE;
    public static final HashMap<String, RegistryObject<AdjustedRedstoneItem>> WIRE_BLOCK_ITEMS;
    public static final HashMap<String, RegistryObject<AdjustedRedstoneItem>> WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE;
    public static final HashMap<String, RegistryObject<InfiniwireBlock>> INFINIWIRE_BLOCKS;
    public static final HashMap<String, RegistryObject<InfiniwireItem>> INFINIWIRE_BLOCK_ITEMS;
    public static final HashMap<String, Pair<Float, ITag<Item>>> COLORS = new HashMap<>();
    public static final Tags.IOptionalNamedTag<Item> WIRES_TAG =
            ItemTags.createOptional(Util.makeResourceLocation("wires"));
    public static final Tags.IOptionalNamedTag<Item> INFINIWIRES_TAG =
            ItemTags.createOptional(Util.makeResourceLocation("infiniwires"));

    static
    {
        COLORS.put("blue", Pair.of(2.0F / 3.0F, Tags.Items.DYES_BLUE));
        COLORS.put("green", Pair.of(1.0F / 3.0F, Tags.Items.DYES_GREEN));
        COLORS.put("orange", Pair.of(1.0F / 12.0F, Tags.Items.DYES_ORANGE));
        COLORS.put("pink", Pair.of(5.0F / 6.0F, Tags.Items.DYES_PINK));
        COLORS.put("yellow", Pair.of(1.0F / 6.0F, Tags.Items.DYES_YELLOW));
        WIRE_BLOCKS = new HashMap<>(COLORS.size() + 1);
        WIRE_BLOCKS_EXCLUDING_REDSTONE = new HashMap<>(COLORS.size());
        WIRE_BLOCK_ITEMS = new HashMap<>(COLORS.size() + 1);
        WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE = new HashMap<>(COLORS.size());
        for (String color : COLORS.keySet())
        {
            String id = color + "_wire";
            RegistryObject<AdjustedRedstoneWireBlock> wireBlockObject =
                    BLOCKS.register(id, () -> new AdjustedRedstoneWireBlock(COLORS.get(color).getLeft()));
            WIRE_BLOCKS.put(color, wireBlockObject);
            WIRE_BLOCKS_EXCLUDING_REDSTONE.put(color, wireBlockObject);
            RegistryObject<AdjustedRedstoneItem> wireItemObject = ITEMS.register(id,
                    () -> WIRE_BLOCKS.get(color).get().createBlockItem(COLORS.get(color).getRight()));
            WIRE_BLOCK_ITEMS.put(color, wireItemObject);
            WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE.put(color, wireItemObject);
        }
        String red = "red";
        COLORS.put(red, Pair.of(0.0F, Tags.Items.DYES_RED));
        WIRE_BLOCKS.put(red, VANILLA_BLOCKS.register("redstone_wire",
                () -> new AdjustedRedstoneWireBlock(COLORS.get(red).getLeft())));
        WIRE_BLOCK_ITEMS.put(red, VANILLA_ITEMS.register("redstone",
                () -> WIRE_BLOCKS.get(red).get().createBlockItem(COLORS.get(red).getRight())));
        INFINIWIRE_BLOCKS = new HashMap<>(COLORS.size());
        INFINIWIRE_BLOCK_ITEMS = new HashMap<>(COLORS.size());
        for (String color : COLORS.keySet())
        {
            String id = color + "_infiniwire";
            INFINIWIRE_BLOCKS.put(color, BLOCKS.register(id, () -> new InfiniwireBlock(COLORS.get(color).getLeft())));
            INFINIWIRE_BLOCK_ITEMS.put(color, ITEMS.register(id,
                    () -> INFINIWIRE_BLOCKS.get(color).get().createBlockItem(COLORS.get(color).getRight())));
        }
    }

    public static void registerListeners(IEventBus bus)
    {
        BLOCKS.register(bus);
        VANILLA_BLOCKS.register(bus);
        ITEMS.register(bus);
        VANILLA_ITEMS.register(bus);
    }
}
