package com.morewires.util;

import com.morewires.MoreWires;
import com.morewires.item.AdjustedRedstoneItem;
import com.morewires.item.InfiniwireItem;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import com.morewires.block.*;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.Pair;
import static com.morewires.MoreWires.MODID;

import java.util.HashMap;

public class ObjectHolder {
    public static final HashMap<String, AdjustedRedstoneWireBlock> WIRE_BLOCKS;
    public static final HashMap<String, AdjustedRedstoneWireBlock> WIRE_BLOCKS_EXCLUDING_REDSTONE;
    public static final HashMap<String, Item> WIRE_BLOCK_ITEMS;
    public static final HashMap<String, AdjustedRedstoneItem> WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE;
    public static final HashMap<String, InfiniwireBlock> INFINIWIRE_BLOCKS;
    public static final HashMap<String, InfiniwireItem> INFINIWIRE_BLOCK_ITEMS;
    public static final HashMap<String, Pair<Integer, Item>> COLORS = new HashMap<>();
    public static final TagKey<Item> WIRES_TAG_KEY = TagKey.of(Registries.ITEM.getKey(), new Identifier(MODID, "wires"));
    public static final TagKey<Item> INFINIWIRES_TAG_KEY = TagKey.of(Registries.ITEM.getKey(), new Identifier(MODID, "infiniwires"));

    static{
        COLORS.put("blue", Pair.of(Color.rgbToRgbInt(new Vec3d(0,0,255)), Items.BLUE_DYE));
        COLORS.put("green", Pair.of(Color.rgbToRgbInt(new Vec3d(0,255,0)), Items.GREEN_DYE));
        COLORS.put("orange", Pair.of(Color.rgbToRgbInt(new Vec3d(255,200,0)), Items.ORANGE_DYE));
        COLORS.put("pink", Pair.of(Color.rgbToRgbInt(new Vec3d(255,0,255)), Items.PINK_DYE));
        COLORS.put("yellow", Pair.of(Color.rgbToRgbInt(new Vec3d(255,255,0)), Items.YELLOW_DYE));
        WIRE_BLOCKS = new HashMap<>(COLORS.size()+1);
        WIRE_BLOCKS_EXCLUDING_REDSTONE = new HashMap<>(COLORS.size());
        WIRE_BLOCK_ITEMS = new HashMap<>(COLORS.size() + 1);
        WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE = new HashMap<>(COLORS.size());
        for (String color : COLORS.keySet()) {
            String id = color + "_wire";
            AdjustedRedstoneWireBlock wireBlockObject =
                    Registry.register(Registries.BLOCK ,new Identifier(MODID,id), new AdjustedRedstoneWireBlock(COLORS.get(color).getLeft()));
            WIRE_BLOCKS.put(color, wireBlockObject);
            WIRE_BLOCKS_EXCLUDING_REDSTONE.put(color, wireBlockObject);
            AdjustedRedstoneItem wireItemObject = Registry.register(Registries.ITEM, new Identifier(MODID, id),
                    WIRE_BLOCKS.get(color).createBlockItem(COLORS.get(color).getRight()));
            WIRE_BLOCK_ITEMS.put(color, wireItemObject);
            WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE.put(color, wireItemObject);
        }
        String red = "red";
        COLORS.put(red, Pair.of(Color.rgbToRgbInt(new Vec3d(255,0,0)),Items.RED_DYE));
        WIRE_BLOCKS.put(red, (AdjustedRedstoneWireBlock) Blocks.REDSTONE_WIRE);
        WIRE_BLOCK_ITEMS.put(red, Items.REDSTONE);
        INFINIWIRE_BLOCKS = new HashMap<>(COLORS.size());
        INFINIWIRE_BLOCK_ITEMS = new HashMap<>(COLORS.size());
        for (String color : COLORS.keySet()) {
            String id = color + "_infiniwire";
            INFINIWIRE_BLOCKS.put(color, Registry.register(Registries.BLOCK , new Identifier(MODID, id), new InfiniwireBlock(COLORS.get(color).getLeft())));
            INFINIWIRE_BLOCK_ITEMS.put(color, Registry.register(Registries.ITEM , new Identifier(MODID, id),
                    INFINIWIRE_BLOCKS.get(color).createBlockItem(COLORS.get(color).getRight())));
        }

    }
}
