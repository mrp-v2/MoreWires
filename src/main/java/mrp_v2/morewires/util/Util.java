package mrp_v2.morewires.util;

import mrp_v2.morewires.MoreWires;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import net.minecraftforge.registries.IForgeRegistryEntry;

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

    public static <T extends IForgeRegistryEntry<T>> String getId(ForgeRegistryEntry<T> entry)
    {
        return entry.getRegistryName().getPath();
    }
}
