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
        event.getRegistry().registerAll(ObjectHolder.ALT_REDSTONE_WIRE_BLOCK, ObjectHolder.INFINIWIRE_BLOCK);
    }

    @SubscribeEvent public static void registerItems(final RegistryEvent.Register<Item> event)
    {
        event.getRegistry().registerAll(ObjectHolder.INFINIWIRE_BLOCK_ITEM);
    }
}
