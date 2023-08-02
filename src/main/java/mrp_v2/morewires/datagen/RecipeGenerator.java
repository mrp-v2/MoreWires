package mrp_v2.morewires.datagen;

import mrp_v2.morewires.item.AdjustedRedstoneItem;
import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.mrplibrary.datagen.providers.RecipeProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Consumer;

public class RecipeGenerator extends RecipeProvider
{
    public static final String DYEING_ID = "dyeing";

    public RecipeGenerator(DataGenerator generatorIn, String modId)
    {
        super(generatorIn, modId);
    }

    @Override
    protected void buildShapelessRecipes(Consumer<FinishedRecipe> iFinishedRecipeConsumer)
    {
        makeWireRecipes(iFinishedRecipeConsumer);
        makeInfiniwireRecipes(iFinishedRecipeConsumer);
    }

    private void makeWireRecipes(Consumer<FinishedRecipe> iFinishedRecipeConsumer)
    {
        for (RegistryObject<AdjustedRedstoneItem> item : ObjectHolder.WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE.values())
        {
            makeDyeingWireRecipe(iFinishedRecipeConsumer, item.get(), item.get().getDyeTag());
        }
    }

    private void makeDyeingWireRecipe(Consumer<FinishedRecipe> iFinishedRecipeConsumer, ItemLike result,
                                      Tag<Item> dyeTag)
    {
        ResourceLocation resultLoc = result.asItem().getRegistryName();
        ShapelessRecipeBuilder.shapeless(result, 8).requires(Ingredient.of(ObjectHolder.WIRES_TAG), 8)
                .requires(dyeTag).unlockedBy("has_wire", RecipeProvider.has(ObjectHolder.WIRES_TAG))
                .group(resultLoc.getPath())
                .save(iFinishedRecipeConsumer, modLoc(DYEING_ID + "/" + resultLoc.getPath()));
    }

    private void makeInfiniwireRecipes(Consumer<FinishedRecipe> iFinishedRecipeConsumer)
    {
        for (String color : ObjectHolder.COLORS.keySet())
        {
            AdjustedRedstoneItem item = ObjectHolder.INFINIWIRE_BLOCK_ITEMS.get(color).get();
            makeDyedInfiniwireRecipe(iFinishedRecipeConsumer, item, ObjectHolder.WIRE_BLOCK_ITEMS.get(color).get());
            makeDyeingInfiniwireRecipe(iFinishedRecipeConsumer, item, item.getDyeTag());
        }
    }

    private void makeDyeingInfiniwireRecipe(Consumer<FinishedRecipe> iFinishedRecipeConsumer, ItemLike result,
                                            Tag<Item> dyeTag)
    {
        ResourceLocation resultLoc = result.asItem().getRegistryName();
        ShapelessRecipeBuilder.shapeless(result, 8)
                .requires(Ingredient.of(ObjectHolder.INFINIWIRES_TAG), 8).requires(dyeTag)
                .unlockedBy("has_infiniwire", RecipeProvider.has(ObjectHolder.INFINIWIRES_TAG))
                .group(resultLoc.getPath())
                .save(iFinishedRecipeConsumer, modLoc(DYEING_ID + "/" + resultLoc.getPath()));
    }

    private void makeDyedInfiniwireRecipe(Consumer<FinishedRecipe> iFinishedRecipeConsumer, ItemLike result,
                                          ItemLike ingredient)
    {
        ShapelessRecipeBuilder.shapeless(result, 8).requires(ingredient, 8)
                .requires(Tags.Items.INGOTS_IRON).unlockedBy("has_wire", RecipeProvider.has(ingredient))
                .group(result.asItem().getRegistryName().getPath()).save(iFinishedRecipeConsumer);
    }
}
