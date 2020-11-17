package mrp_v2.morewires.datagen;

import mrp_v2.morewires.item.AdjustedRedstoneItem;
import mrp_v2.morewires.item.InfiniwireItem;
import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.morewires.util.Util;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.RegistryObject;

public class ItemModelGenerator extends ItemModelProvider
{
    public ItemModelGenerator(DataGenerator generator, String modId, ExistingFileHelper existingFileHelper)
    {
        super(generator, modId, existingFileHelper);
    }

    @Override protected void registerModels()
    {
        for (RegistryObject<AdjustedRedstoneItem> item : ObjectHolder.WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE.values())
        {
            registerWireItemModel(item.get());
        }
        for (RegistryObject<InfiniwireItem> item : ObjectHolder.INFINIWIRE_BLOCK_ITEMS.values())
        {
            registerInfiniwireItemModel(item.get());
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
