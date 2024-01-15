package com.morewires;

import com.morewires.block.AdjustedRedstoneWireBlock;
import com.morewires.item.AdjustedRedstoneItem;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.minecraft.client.render.RenderLayer;

import static com.morewires.util.ObjectHolder.*;

public class MoreWiresClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		for(String color : WIRE_BLOCK_ITEMS.keySet()){
			ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ((AdjustedRedstoneItem) stack.getItem()).getColor(), INFINIWIRE_BLOCK_ITEMS.get(color));
			ColorProviderRegistry.BLOCK.register((state, view, pos, tintIndex) -> AdjustedRedstoneWireBlock.getColor(state), WIRE_BLOCKS.get(color), INFINIWIRE_BLOCKS.get(color));
			if(!color.equals("red")){
				ColorProviderRegistry.ITEM.register((stack, tintIndex) -> ((AdjustedRedstoneItem) stack.getItem()).getColor(), WIRE_BLOCK_ITEMS.get(color));
			}
		}
		for(String color : WIRE_BLOCKS.keySet()){
			BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(), WIRE_BLOCKS.get(color), INFINIWIRE_BLOCKS.get(color));
		}
	}
}