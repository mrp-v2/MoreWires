package com.morewires;

import com.morewires.datagen.*;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MoreWiresDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator generator) {
		FabricDataGenerator.Pack pack = generator.createPack();

		pack.addProvider(ModelDataGenerator::new);
		pack.addProvider(BlockLootTablesGenerator::new);
		pack.addProvider(TranslationGenerator::new);
		pack.addProvider(ItemTagGenerator::new);
		pack.addProvider(RecipeGenerator::new);
	}
}
