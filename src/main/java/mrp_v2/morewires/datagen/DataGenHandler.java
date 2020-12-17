package mrp_v2.morewires.datagen;

import mrp_v2.morewires.MoreWires;
import mrp_v2.mrplibrary.datagen.DataGeneratorHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = MoreWires.ID, bus = Mod.EventBusSubscriber.Bus.MOD) public class DataGenHandler
{
    @SubscribeEvent public static void registerDataProvider(final GatherDataEvent event)
    {
        DataGeneratorHelper helper = new DataGeneratorHelper(event, MoreWires.ID);
        if (event.includeServer())
        {
            helper.addLootTables(new LootTables());
            helper.addRecipeProvider(RecipeGenerator::new);
            helper.addItemTagsProvider(ItemTagGenerator::new);
        }
        if (event.includeClient())
        {
            helper.addBlockStateProvider(BlockStateGenerator::new);
            helper.addItemModelProvider(ItemModelGenerator::new);
            helper.addLanguageProvider(EN_USTranslationGenerator::new);
        }
    }
}
