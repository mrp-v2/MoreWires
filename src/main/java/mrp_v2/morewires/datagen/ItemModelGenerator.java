package mrp_v2.morewires.datagen;

import mrp_v2.morewires.item.AdjustedRedstoneItem;
import mrp_v2.morewires.item.InfiniwireItem;
import mrp_v2.morewires.util.ObjectHolder;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ItemModelGenerator extends ItemModelProvider
{
    public ItemModelGenerator(PackOutput output, String modId, ExistingFileHelper existingFileHelper)
    {
        super(output, modId, existingFileHelper);
    }

    @Override protected void registerModels()
    {
        for (RegistryObject<AdjustedRedstoneItem> item : ObjectHolder.WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE.values())
        {
            registerItemModel(item.get());
        }
        for (RegistryObject<InfiniwireItem> item : ObjectHolder.INFINIWIRE_BLOCK_ITEMS.values())
        {
            registerInfiniwireItemModel(item.get());
        }
    }

    protected void registerItemModel(Item item)
    {
        this.registerItemModel(item, modLoc("item/" + ForgeRegistries.ITEMS.getKey(item).getPath()));
    }

    protected void registerItemModel(Item item, ResourceLocation texture)
    {
        super.singleTexture(ForgeRegistries.ITEMS.getKey(item).getPath(), new ResourceLocation("item/generated"), "layer0",
                texture);
    }

    private void registerInfiniwireItemModel(Item item)
    {
        String path = ForgeRegistries.ITEMS.getKey(item).getPath().replace("infini", "");
        if (path.contains("red_wire"))
        {
            this.registerItemModel(item, new ResourceLocation("minecraft:item/redstone"));
        } else
        {
            this.registerItemModel(item, modLoc("item/" + path));
        }
    }
}
