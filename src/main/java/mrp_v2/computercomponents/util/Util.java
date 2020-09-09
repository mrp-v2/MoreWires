package mrp_v2.computercomponents.util;

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
}
