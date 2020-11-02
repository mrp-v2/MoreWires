package mrp_v2.morewires.datagen;

import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.mrp_v2datagenlibrary.datagen.TranslationGenerator;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;

public class EN_USTranslationGenerator extends TranslationGenerator
{
    public EN_USTranslationGenerator(DataGenerator gen, String modid)
    {
        super(gen, modid, "en_us");
    }

    @Override protected void addTranslations()
    {
        for (Block block : ObjectHolder.WIRE_BLOCKS_EXCLUDING_REDSTONE)
        {
            addWireTranslation(block);
        }
        for (Block block : ObjectHolder.INFINIWIRE_BLOCKS)
        {
            addWireTranslation(block);
        }
    }

    private void addWireTranslation(Block block)
    {
        String name = block.getRegistryName().getPath().replace("_", " ");
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
