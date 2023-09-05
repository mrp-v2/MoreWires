package mrp_v2.morewires.datagen;

import mrp_v2.morewires.MoreWires;
import mrp_v2.mrplibrary.datagen.DataGeneratorHelper;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreWires.ID, bus = Mod.EventBusSubscriber.Bus.MOD) public class DataGenHandler
{
    @SubscribeEvent public static void registerDataProvider(final GatherDataEvent event)
    {
        DataGeneratorHelper helper = new DataGeneratorHelper(event, MoreWires.ID);
        helper.addLootTables(new LootTableProvider.SubProviderEntry(LootTables::new, LootContextParamSets.BLOCK));
        helper.addRecipeProvider(RecipeGenerator::new);
        helper.addBlockTagsProvider(BlockTagGenerator::new);
        helper.addItemTagsProvider(ItemTagGenerator::new);
        helper.addTextureProvider(TextureGenerator::new);
        helper.addBlockStateProvider(BlockStateGenerator::new);
        helper.addItemModelProvider(ItemModelGenerator::new);
        helper.addLanguageProvider(EN_USTranslationGenerator::new);
    }
}
