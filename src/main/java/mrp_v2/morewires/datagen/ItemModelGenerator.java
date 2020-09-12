package mrp_v2.morewires.datagen;

import mrp_v2.morewires.MoreWires;
import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.morewires.util.Util;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemModelGenerator extends ItemModelProvider
{
    public ItemModelGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper)
    {
        super(generator, MoreWires.ID, existingFileHelper);
    }

    @Override protected void registerModels()
    {
        for (Item item : ObjectHolder.WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE)
        {
            registerWireItemModel(item);
        }
        for (Item item : ObjectHolder.INFINIWIRE_BLOCK_ITEMS)
        {
            registerInfiniwireItemModel(item);
        }
    }

    private void registerWireItemModel(Item item)
    {
        this.registerItemModel(item);
    }

    private void registerItemModel(Item item)
    {
        this.registerItemModel(item, Util.makeResourceLocation("item", Util.getId(item)));
    }

    private void registerItemModel(Item item, ResourceLocation texture)
    {
        super.singleTexture(Util.getId(item), new ResourceLocation("item/generated"), "layer0", texture);
    }

    private void registerInfiniwireItemModel(Item item)
    {
        String path = Util.getId(item).replace("infini", "");
        if (path.contains("red_wire"))
        {
            this.registerItemModel(item, new ResourceLocation("minecraft:item/redstone"));
        } else
        {
            this.registerItemModel(item, Util.makeResourceLocation("item", path));
        }
    }
}
