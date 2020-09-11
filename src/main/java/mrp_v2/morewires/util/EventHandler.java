package mrp_v2.morewires.util;

import mrp_v2.morewires.MoreWires;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = MoreWires.ID, bus = Mod.EventBusSubscriber.Bus.MOD) public class EventHandler
{
    @SubscribeEvent public static void registerBlocks(final RegistryEvent.Register<Block> event)
    {
        Util.doOperationOn(event.getRegistry()::register, ObjectHolder.WIRE_BLOCKS);
        Util.doOperationOn(event.getRegistry()::register, ObjectHolder.INFINIWIRE_BLOCKS);
    }

    @SubscribeEvent public static void registerItems(final RegistryEvent.Register<Item> event)
    {
        Util.doOperationOn(event.getRegistry()::register, ObjectHolder.WIRE_BLOCK_ITEMS);
        Util.doOperationOn(event.getRegistry()::register, ObjectHolder.INFINIWIRE_BLOCK_ITEMS);
    }
}
