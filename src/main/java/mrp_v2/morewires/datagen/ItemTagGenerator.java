package mrp_v2.morewires.datagen;

import mrp_v2.morewires.MoreWires;
import mrp_v2.morewires.util.ObjectHolder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class ItemTagGenerator extends ItemTagsProvider
{
    public ItemTagGenerator(DataGenerator dataGenerator)
    {
        super(dataGenerator);
    }

    @Override protected void registerTags()
    {
        this.getBuilder(ObjectHolder.WIRES_TAG)
                .add(ObjectHolder.WIRE_BLOCK_ITEMS)
                .addOptionalTag(new ResourceLocation(Tags.Items.DUSTS_REDSTONE.getId().toString()));
        this.getBuilder(ObjectHolder.INFINIWIRES_TAG).add(ObjectHolder.INFINIWIRE_BLOCK_ITEMS);
    }

    @Override public String getName()
    {
        return super.getName() + ": " + MoreWires.ID;
    }
}
