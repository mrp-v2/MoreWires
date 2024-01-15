package com.morewires;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.morewires.util.ObjectHolder.*;

public class MoreWires implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final String MODID = "more"+"wires";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		//LOGGER.info("Hello Fabric world!");
		ItemGroupEvents.modifyEntriesEvent(ItemGroups.REDSTONE).register(content -> {
			content.add(INFINIWIRE_BLOCK_ITEMS.get("red"));
			for(String color : WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE.keySet()){
				content.add(WIRE_BLOCK_ITEMS.get(color));
				content.add(INFINIWIRE_BLOCK_ITEMS.get(color));
			}
		});
	}
}