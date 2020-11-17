package mrp_v2.morewires;

import mrp_v2.morewires.util.ObjectHolder;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(MoreWires.ID) public class MoreWires
{
    public static final String ID = "more" + "wires";

    public MoreWires()
    {
        ObjectHolder.registerListeners(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
