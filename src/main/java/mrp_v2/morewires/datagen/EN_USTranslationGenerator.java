package mrp_v2.morewires.datagen;

import mrp_v2.morewires.block.AdjustedRedstoneWireBlock;
import mrp_v2.morewires.block.InfiniwireBlock;
import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.mrplibrary.datagen.providers.LanguageProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class EN_USTranslationGenerator extends LanguageProvider
{
    public EN_USTranslationGenerator(PackOutput output, String modid)
    {
        super(output, modid, "en_us");
    }

    @Override protected void addTranslations()
    {
        for (RegistryObject<AdjustedRedstoneWireBlock> block : ObjectHolder.WIRE_BLOCKS_EXCLUDING_REDSTONE.values())
        {
            addWireTranslation(block.get());
        }
        for (RegistryObject<InfiniwireBlock> block : ObjectHolder.INFINIWIRE_BLOCKS.values())
        {
            addWireTranslation(block.get());
        }
    }

    protected void addWireTranslation(Block block)
    {
        String name = ForgeRegistries.BLOCKS.getKey(block).getPath().replace("_", " ");
        for (int i = 0; i < name.length() - 1; i++)
        {
            if (i == 0)
            {
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
            } else if (name.charAt(i - 1) == ' ')
            {
                name = name.substring(0, i) + name.substring(i, i + 1).toUpperCase() + name.substring(i + 1);
            }
        }
        this.add(block, name);
    }
}
