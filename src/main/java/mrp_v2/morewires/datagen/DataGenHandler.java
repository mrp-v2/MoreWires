package mrp_v2.morewires.datagen;

import mrp_v2.morewires.MoreWires;
import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

@Mod.EventBusSubscriber(modid = MoreWires.ID, bus = Mod.EventBusSubscriber.Bus.MOD) public class DataGenHandler
{
    @SubscribeEvent public static void registerDataProvider(final GatherDataEvent event)
    {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        if (event.includeServer())
        {
            generator.addProvider(new LootTableGenerator(generator));
            generator.addProvider(new RecipeGenerator(generator));
            BlockTagsProvider blockTagsProvider = new BlockTagsProvider(generator, MoreWires.ID, existingFileHelper);
            generator.addProvider(new ItemTagGenerator(generator, blockTagsProvider, existingFileHelper));
        }
        if (event.includeClient())
        {
            generator.addProvider(new ItemModelGenerator(generator, existingFileHelper));
        }
    }
}
