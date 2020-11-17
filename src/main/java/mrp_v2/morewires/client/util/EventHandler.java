package mrp_v2.morewires.client.util;

import mrp_v2.morewires.MoreWires;
import mrp_v2.morewires.block.AdjustedRedstoneWireBlock;
import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.morewires.util.Util;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.function.Consumer;

@Mod.EventBusSubscriber(modid = MoreWires.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler
{
    @SubscribeEvent public static void clientSetup(final FMLClientSetupEvent event)
    {
        Consumer<Block> renderLayerSetter = (block) -> RenderTypeLookup.setRenderLayer(block, RenderType.getCutout());
        Util.doOperationOn((blockObject) -> renderLayerSetter.accept(blockObject.get()),
                ObjectHolder.INFINIWIRE_BLOCKS.values());
        Util.doOperationOn((blockObject) -> renderLayerSetter.accept(blockObject.get()),
                ObjectHolder.WIRE_BLOCKS.values());
    }

    @SubscribeEvent public static void registerBlockColors(final ColorHandlerEvent.Block event)
    {
        IBlockColor colorer =
                (blockState, iBlockDisplayReader, blockPos, tint) -> AdjustedRedstoneWireBlock.getColor(blockState);
        Consumer<Block> colorRegisterer = (block) -> event.getBlockColors().register(colorer, block);
        Util.doOperationOn((blockObject) -> colorRegisterer.accept(blockObject.get()),
                ObjectHolder.INFINIWIRE_BLOCKS.values());
        Util.doOperationOn((blockObject) -> colorRegisterer.accept(blockObject.get()),
                ObjectHolder.WIRE_BLOCKS.values());
    }
}
