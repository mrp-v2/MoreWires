package mrp_v2.morewires.datagen;

import mrp_v2.morewires.MoreWires;
import mrp_v2.mrplibrary.datagen.providers.TextureProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.function.Supplier;

public class TextureGenerator extends TextureProvider
{
    public TextureGenerator(DataGenerator generator, ExistingFileHelper existingFileHelper, String modId)
    {
        super(generator, existingFileHelper, modId);
    }

    @Override protected void addTextures(FinishedTextureConsumer finishedTextureConsumer)
    {
        Supplier<Texture> redstoneTextureGetter = () -> getTexture(new ResourceLocation("item/redstone"));
        Texture blueWireTexture = redstoneTextureGetter.get(), greenWireTexture = redstoneTextureGetter.get(),
                orangeWireTexture = redstoneTextureGetter.get(), pinkWireTexture = redstoneTextureGetter.get(),
                yellowWireTexture = redstoneTextureGetter.get();
        adjustHSB(blueWireTexture.getTexture(), -120, 100, 0);
        adjustHSB(greenWireTexture.getTexture(), 120, 100, 0);
        adjustHSB(orangeWireTexture.getTexture(), 30, 100, 0);
        adjustHSB(pinkWireTexture.getTexture(), -60, 100, 0);
        adjustHSB(yellowWireTexture.getTexture(), 60, 100, 0);
        finish(blueWireTexture, new ResourceLocation(MoreWires.ID, "item/blue_wire"), finishedTextureConsumer);
        finish(greenWireTexture, new ResourceLocation(MoreWires.ID, "item/green_wire"), finishedTextureConsumer);
        finish(orangeWireTexture, new ResourceLocation(MoreWires.ID, "item/orange_wire"), finishedTextureConsumer);
        finish(pinkWireTexture, new ResourceLocation(MoreWires.ID, "item/pink_wire"), finishedTextureConsumer);
        finish(yellowWireTexture, new ResourceLocation(MoreWires.ID, "item/yellow_wire"), finishedTextureConsumer);
    }
}
