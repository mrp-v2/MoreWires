package mrp_v2.morewires.util;

import mrp_v2.morewires.MoreWires;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class Util
{
    public static <T> void doOperationOn(Consumer<T> operation, T[] items)
    {
        for (T item : items)
        {
            operation.accept(item);
        }
    }

    public static ResourceLocation makeResourceLocation(String... parts)
    {
        return new ResourceLocation(MoreWires.ID, String.join("/", parts));
    }
}
