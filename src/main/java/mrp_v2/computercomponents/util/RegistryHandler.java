package mrp_v2.computercomponents.util;

import mrp_v2.computercomponents.ComputerComponents;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ComputerComponents.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryHandler
{
    @SubscribeEvent public static void registerBlocks(final RegistryEvent.Register<Block> event)
    {
        Util.doOperationOn(event.getRegistry()::register, ObjectHolder.INFINIWIRE_BLOCKS);
        event.getRegistry().register(ObjectHolder.ALT_REDSTONE_WIRE_BLOCK);
    }

    @SubscribeEvent public static void registerItems(final RegistryEvent.Register<Item> event)
    {
        Util.doOperationOn(event.getRegistry()::register, ObjectHolder.INFINIWIRE_BLOCK_ITEMS);
        event.getRegistry().register(ObjectHolder.ALT_REDSTONE_BLOCK_ITEM);
    }
}
