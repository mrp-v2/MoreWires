package com.morewires.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootTableProvider;

import static com.morewires.util.ObjectHolder.*;

public class BlockLootTablesGenerator extends FabricBlockLootTableProvider {
    public BlockLootTablesGenerator(FabricDataOutput dataOutput){
        super(dataOutput);
    }

    @Override
    public void generate() {
        for(String color : WIRE_BLOCKS.keySet()){
            this.addDrop(WIRE_BLOCKS.get(color));
            this.addDrop(INFINIWIRE_BLOCKS.get(color));
        }
    }
}
