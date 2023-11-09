package com.morewires.datagen;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.server.tag.ItemTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;

import java.util.concurrent.CompletableFuture;

import static com.morewires.util.ObjectHolder.*;

public class ItemTagGenerator extends FabricTagProvider.ItemTagProvider {
    public ItemTagGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }
    @Override
    protected void configure(RegistryWrapper.WrapperLookup lookup) {
        for(String color : WIRE_BLOCK_ITEMS.keySet()){
            getOrCreateTagBuilder(WIRES_TAG_KEY)
                    .add(WIRE_BLOCK_ITEMS.get(color));
            getOrCreateTagBuilder(INFINIWIRES_TAG_KEY)
                    .add(INFINIWIRE_BLOCK_ITEMS.get(color));
        }
        getOrCreateTagBuilder(WIRES_TAG_KEY).addOptional(Registries.ITEM.getId(Items.REDSTONE));
    }
}
