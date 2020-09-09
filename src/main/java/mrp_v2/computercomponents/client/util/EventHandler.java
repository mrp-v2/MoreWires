package mrp_v2.computercomponents.client.util;

import mrp_v2.computercomponents.ComputerComponents;
import mrp_v2.computercomponents.block.InfiniwireBlock;
import mrp_v2.computercomponents.util.ObjectHolder;
import mrp_v2.computercomponents.util.Util;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.IBlockColor;
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
        Util.doOperationOn((block) -> RenderTypeLookup.setRenderLayer(block, RenderType.getCutout()),
                ObjectHolder.INFINIWIRE_BLOCKS);
    }

    @SubscribeEvent public static void registerBlockColors(final ColorHandlerEvent.Block event)
    {
        IBlockColor colorer = (blockState, iBlockDisplayReader, blockPos, tint) -> InfiniwireBlock.getColor(blockState);
        Util.doOperationOn((block) -> event.getBlockColors().register(colorer, block), ObjectHolder.INFINIWIRE_BLOCKS);
    }
}
