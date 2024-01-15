package com.morewires.datagen;

import com.morewires.item.AdjustedRedstoneItem;
import com.morewires.util.ObjectHolder;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeJsonProvider;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.function.Consumer;

public class RecipeGenerator extends FabricRecipeProvider {
    public static final String DYEING_ID = "dyeing";

    public RecipeGenerator(FabricDataOutput dataOutput){
        super(dataOutput);
    }

    @Override
    public void generate(Consumer<RecipeJsonProvider> exporter) {
        makeWireRecipes(exporter);
        makeInfiniwireRecipes(exporter);
    }

    private void makeWireRecipes(Consumer<RecipeJsonProvider> iFinishedRecipeConsumer)
    {
        for (AdjustedRedstoneItem item : ObjectHolder.WIRE_BLOCK_ITEMS_EXCLUDING_REDSTONE.values())
        {
            makeDyeingWireRecipe(iFinishedRecipeConsumer, item, item.getDye());
        }
    }

    private void makeDyeingWireRecipe(Consumer<RecipeJsonProvider> iFinishedRecipeConsumer, Item result, Item dye)
    {
        Identifier resultLoc = Registries.ITEM.getId(result);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, result, 8).input(Ingredient.fromTag(ObjectHolder.WIRES_TAG_KEY), 8)
                .input(dye).criterion("has_wire", FabricRecipeProvider.conditionsFromTag(ObjectHolder.WIRES_TAG_KEY))
                .group(resultLoc.getPath())
                .offerTo(iFinishedRecipeConsumer, new Identifier(DYEING_ID + "/" + resultLoc.getPath()));
    }

    private void makeInfiniwireRecipes(Consumer<RecipeJsonProvider> iFinishedRecipeConsumer)
    {
        for (String color : ObjectHolder.COLORS.keySet())
        {
            AdjustedRedstoneItem item = ObjectHolder.INFINIWIRE_BLOCK_ITEMS.get(color);
            makeDyedInfiniwireRecipe(iFinishedRecipeConsumer, item, ObjectHolder.WIRE_BLOCK_ITEMS.get(color));
            makeDyeingInfiniwireRecipe(iFinishedRecipeConsumer, item, item.getDye());
        }
    }

    private void makeDyeingInfiniwireRecipe(Consumer<RecipeJsonProvider> iFinishedRecipeConsumer, Item result, Item dye)
    {
        Identifier resultLoc = Registries.ITEM.getId(result);
        ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, result, 8)
                .input(Ingredient.fromTag(ObjectHolder.INFINIWIRES_TAG_KEY), 8).input(dye)
                .criterion("has_infiniwire", FabricRecipeProvider.conditionsFromTag(ObjectHolder.INFINIWIRES_TAG_KEY))
                .group(resultLoc.getPath())
                .offerTo(iFinishedRecipeConsumer, new Identifier(DYEING_ID + "/" + resultLoc.getPath()));
    }

    private void makeDyedInfiniwireRecipe(Consumer<RecipeJsonProvider> iFinishedRecipeConsumer, Item result, Item ingredient)
    {
        ShapelessRecipeJsonBuilder.create(RecipeCategory.REDSTONE, result, 8).input(ingredient, 8)
                .input(Items.IRON_INGOT).criterion("has_wire", FabricRecipeProvider.conditionsFromItem(ingredient))
                .group(Registries.ITEM.getId(result).getPath()).offerTo(iFinishedRecipeConsumer);
    }

}
