package mrp_v2.morewires.datagen;

import mrp_v2.morewires.item.AdjustedRedstoneItem;
import mrp_v2.morewires.util.ObjectHolder;
import mrp_v2.morewires.util.Util;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.data.RecipeProvider;
import net.minecraft.data.ShapelessRecipeBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.ITag;
import net.minecraft.util.IItemProvider;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.RegistryObject;

import java.util.function.Consumer;

public class RecipeGenerator extends mrp_v2.mrplibrary.datagen.RecipeProvider
{
    public static final String DYEING_ID = "dyeing";

    public RecipeGenerator(DataGenerator generatorIn, String modId)
    {
        super(generatorIn, modId);
    }

    @Override protected void registerRecipes(Consumer<IFinishedRecipe> iFinishedRecipeConsumer)
    {
        makeWireRecipes(iFinishedRecipeConsumer);
        makeInfiniwireRecipes(iFinishedRecipeConsumer);
    }

    private void makeWireRecipes(Consumer<IFinishedRecipe> iFinishedRecipeConsumer)
    {
        for (RegistryObject<AdjustedRedstoneItem> item : ObjectHolder.WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE.values())
        {
            makeDyeingWireRecipe(iFinishedRecipeConsumer, item.get(), item.get().getDyeTag());
        }
    }

    private void makeDyeingWireRecipe(Consumer<IFinishedRecipe> iFinishedRecipeConsumer, IItemProvider result,
            ITag<Item> dyeTag)
    {
        ShapelessRecipeBuilder.shapelessRecipe(result, 8)
                .addIngredient(Ingredient.fromTag(ObjectHolder.WIRES_TAG), 8)
                .addIngredient(dyeTag)
                .addCriterion("has_wire", RecipeProvider.hasItem(ObjectHolder.WIRES_TAG))
                .setGroup(result.asItem().getRegistryName().getPath())
                .build(iFinishedRecipeConsumer, Util.makeResourceLocation(DYEING_ID, getID(result)));
    }

    private void makeInfiniwireRecipes(Consumer<IFinishedRecipe> iFinishedRecipeConsumer)
    {
        for (String color : ObjectHolder.COLORS.keySet())
        {
            AdjustedRedstoneItem item = ObjectHolder.INFINIWIRE_BLOCK_ITEMS.get(color).get();
            if (item.getRegistryName().equals(Items.REDSTONE.getRegistryName()))
            {
                makeDyedInfiniwireRecipe(iFinishedRecipeConsumer, item, Tags.Items.DUSTS_REDSTONE);
            } else
            {
                makeDyedInfiniwireRecipe(iFinishedRecipeConsumer, item, ObjectHolder.WIRE_BLOCK_ITEMS.get(color).get());
            }
            makeDyeingInfiniwireRecipe(iFinishedRecipeConsumer, item, item.getDyeTag());
        }
    }

    private void makeDyeingInfiniwireRecipe(Consumer<IFinishedRecipe> iFinishedRecipeConsumer, IItemProvider result,
            ITag<Item> dyeTag)
    {
        ShapelessRecipeBuilder.shapelessRecipe(result, 8)
                .addIngredient(Ingredient.fromTag(ObjectHolder.INFINIWIRES_TAG), 8)
                .addIngredient(dyeTag)
                .addCriterion("has_infiniwire", RecipeProvider.hasItem(ObjectHolder.INFINIWIRES_TAG))
                .setGroup(result.asItem().getRegistryName().getPath())
                .build(iFinishedRecipeConsumer, Util.makeResourceLocation(DYEING_ID, getID(result)));
    }

    private void makeDyedInfiniwireRecipe(Consumer<IFinishedRecipe> iFinishedRecipeConsumer, IItemProvider result,
            ITag<Item> ingredient)
    {
        ShapelessRecipeBuilder.shapelessRecipe(result, 8)
                .addIngredient(Ingredient.fromTag(ingredient), 8)
                .addIngredient(Tags.Items.INGOTS_IRON)
                .addCriterion("has_wire", RecipeProvider.hasItem(ingredient))
                .setGroup(result.asItem().getRegistryName().getPath())
                .build(iFinishedRecipeConsumer);
    }

    private void makeDyedInfiniwireRecipe(Consumer<IFinishedRecipe> iFinishedRecipeConsumer, IItemProvider result,
            IItemProvider ingredient)
    {
        ShapelessRecipeBuilder.shapelessRecipe(result, 8)
                .addIngredient(ingredient, 8)
                .addIngredient(Tags.Items.INGOTS_IRON)
                .addCriterion("has_wire", RecipeProvider.hasItem(ingredient))
                .setGroup(result.asItem().getRegistryName().getPath())
                .build(iFinishedRecipeConsumer);
    }
}
