package mrp_v2.computercomponents.client.util;

import mrp_v2.computercomponents.ComputerComponents;
import mrp_v2.computercomponents.block.InfiniwireBlock;
import mrp_v2.computercomponents.util.ObjectHolder;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = ComputerComponents.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler
{
    @SubscribeEvent public static void clientSetup(final FMLClientSetupEvent event)
    {
        RenderTypeLookup.setRenderLayer(ObjectHolder.INFINIWIRE_BLOCK, RenderType.getCutout());
    }

    @SubscribeEvent public static void registerBlockColors(final ColorHandlerEvent.Block event)
    {
        event.getBlockColors()
                .register((blockState, iBlockDisplayReader, blockPos, tint) -> InfiniwireBlock.getColor(
                        blockState.get(RedstoneWireBlock.POWER)), ObjectHolder.INFINIWIRE_BLOCK);
    }
}
