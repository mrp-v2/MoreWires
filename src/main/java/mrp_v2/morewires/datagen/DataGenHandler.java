package mrp_v2.morewires.datagen;

import mrp_v2.morewires.MoreWires;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
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
            generator.addProvider(new ItemTagGenerator(generator));
        }
        if (event.includeClient())
        {
            generator.addProvider(new ItemModelGenerator(generator, existingFileHelper));
            generator.addProvider(new BlockStateGenerator(generator, existingFileHelper));
        }
    }
}
