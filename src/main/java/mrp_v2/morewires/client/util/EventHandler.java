package mrp_v2.morewires.client.util;

import mrp_v2.morewires.MoreWires;
import mrp_v2.morewires.block.AdjustedRedstoneWireBlock;
import mrp_v2.morewires.util.ObjectHolder;
import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = MoreWires.ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EventHandler
{
    @SubscribeEvent public static void clientSetup(final FMLClientSetupEvent event)
    {
        List<RegistryObject<? extends Block>> blockObjects = new ArrayList<>();
        blockObjects.addAll(ObjectHolder.INFINIWIRE_BLOCKS.values());
        blockObjects.addAll(ObjectHolder.WIRE_BLOCKS.values());
        for (RegistryObject<? extends Block> blockObject : blockObjects)
        {
            ItemBlockRenderTypes.setRenderLayer(blockObject.get(), RenderType.cutout());
        }
    }

    @SubscribeEvent public static void registerBlockColors(final ColorHandlerEvent.Block event)
    {
        List<RegistryObject<? extends Block>> blockObjects = new ArrayList<>();
        blockObjects.addAll(ObjectHolder.INFINIWIRE_BLOCKS.values());
        blockObjects.addAll(ObjectHolder.WIRE_BLOCKS.values());
        List<Block> blocks = new ArrayList<>();
        for (RegistryObject<? extends Block> blockObject : blockObjects)
        {
            blocks.add(blockObject.get());
        }
        BlockColor colorer =
                (blockState, iBlockDisplayReader, blockPos, tint) -> AdjustedRedstoneWireBlock.getColor(blockState);
        event.getBlockColors().register(colorer, blocks.toArray(new Block[0]));
    }
}
