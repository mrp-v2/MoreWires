package com.morewires.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.enums.WireConnection;
import net.minecraft.data.client.*;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;

import java.util.Optional;

import static com.morewires.MoreWires.MODID;
import static com.morewires.util.ObjectHolder.*;

public class ModelDataGenerator extends FabricModelProvider {
    public ModelDataGenerator(FabricDataOutput generator){
        super(generator);
    }
    @Override
    public void generateBlockStateModels(BlockStateModelGenerator blockStateModelGenerator) {
        for(Block block : INFINIWIRE_BLOCKS.values()){
            blockStateModelGenerator.blockStateCollector.accept(MultipartBlockStateSupplier.create(block)
                    .with(When.anyOf(
                                    When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.NONE).set(Properties.NORTH_WIRE_CONNECTION, WireConnection.NONE).set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.NONE).set(Properties.WEST_WIRE_CONNECTION, WireConnection.NONE),
                                    When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP).set(Properties.NORTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
                                    When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP).set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
                                    When.create().set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP).set(Properties.WEST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
                                    When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP).set(Properties.WEST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP)
                            ),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier(MODID, "block/infiniwire/infiniwire_dot"))
                    )
                    .with(When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier(MODID, "block/infiniwire/infiniwire_side0"))
                    )
                    .with(When.create().set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier(MODID, "block/infiniwire/infiniwire_side_alt0"))
                    )
                    .with(When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier(MODID, "block/infiniwire/infiniwire_side_alt1")).put(VariantSettings.Y, VariantSettings.Rotation.R270)
                    )
                    .with(When.create().set(Properties.WEST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier(MODID, "block/infiniwire/infiniwire_side1")).put(VariantSettings.Y, VariantSettings.Rotation.R270)
                    )
                    .with(When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier(MODID, "block/infiniwire/infiniwire_up"))
                    )
                    .with(When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier(MODID, "block/infiniwire/infiniwire_up")).put(VariantSettings.Y, VariantSettings.Rotation.R90)
                    )
                    .with(When.create().set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier(MODID, "block/infiniwire/infiniwire_up")).put(VariantSettings.Y, VariantSettings.Rotation.R180)
                    )
                    .with(When.create().set(Properties.WEST_WIRE_CONNECTION, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier(MODID, "block/infiniwire/infiniwire_up")).put(VariantSettings.Y, VariantSettings.Rotation.R270)
                    )
            );
        }
        for(Block block : WIRE_BLOCKS.values()){
            blockStateModelGenerator.blockStateCollector.accept(MultipartBlockStateSupplier.create(block)
                    .with(When.anyOf(
                                    When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.NONE).set(Properties.NORTH_WIRE_CONNECTION, WireConnection.NONE).set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.NONE).set(Properties.WEST_WIRE_CONNECTION, WireConnection.NONE),
                                    When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP).set(Properties.NORTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
                                    When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP).set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
                                    When.create().set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP).set(Properties.WEST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
                                    When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP).set(Properties.WEST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP)
                            ),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier("block/redstone_dust_dot"))
                    )
                    .with(When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier("block/redstone_dust_side0"))
                    )
                    .with(When.create().set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier("block/redstone_dust_side_alt0"))
                    )
                    .with(When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier("block/redstone_dust_side_alt1")).put(VariantSettings.Y, VariantSettings.Rotation.R270)
                    )
                    .with(When.create().set(Properties.WEST_WIRE_CONNECTION, WireConnection.SIDE, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier("block/redstone_dust_side1")).put(VariantSettings.Y, VariantSettings.Rotation.R270)
                    )
                    .with(When.create().set(Properties.NORTH_WIRE_CONNECTION, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier("block/redstone_dust_up"))
                    )
                    .with(When.create().set(Properties.EAST_WIRE_CONNECTION, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier("block/redstone_dust_up")).put(VariantSettings.Y, VariantSettings.Rotation.R90)
                    )
                    .with(When.create().set(Properties.SOUTH_WIRE_CONNECTION, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier("block/redstone_dust_up")).put(VariantSettings.Y, VariantSettings.Rotation.R180)
                    )
                    .with(When.create().set(Properties.WEST_WIRE_CONNECTION, WireConnection.UP),
                            BlockStateVariant.create().put(VariantSettings.MODEL, new Identifier("block/redstone_dust_up")).put(VariantSettings.Y, VariantSettings.Rotation.R270)
                    )
            );
        }
    }

    private static Model item(String parent) {
        return new Model(Optional.of(new Identifier(MODID,"item/" + parent)), Optional.empty());
    }

    @Override
    public void generateItemModels(ItemModelGenerator itemModelGenerator){
        for(String key : WIRE_BLOCK_ITEMS.keySet()){
            itemModelGenerator.register(WIRE_BLOCK_ITEMS.get(key), item("infiniwire"));
        }
        for(String key : INFINIWIRE_BLOCK_ITEMS.keySet()){
            itemModelGenerator.register(INFINIWIRE_BLOCK_ITEMS.get(key), item("infiniwire"));
        }
    }
}
