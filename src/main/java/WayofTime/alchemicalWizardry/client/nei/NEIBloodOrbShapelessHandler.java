package WayofTime.alchemicalWizardry.client.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.StatCollector;
import WayofTime.alchemicalWizardry.api.items.ShapelessBloodOrbRecipe;
import WayofTime.alchemicalWizardry.api.items.interfaces.IBloodOrb;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ShapelessRecipeHandler;

import javax.annotation.Nonnull;

/**
 * NEI Blood Orb Shapeless Recipe Handler by joshie *
 */
public class NEIBloodOrbShapelessHandler extends ShapelessRecipeHandler {
    public class CachedBloodOrbRecipe extends CachedShapelessRecipe {
        public CachedBloodOrbRecipe(List<Object> items, ItemStack recipeOutput) {
            super(items, recipeOutput);
        }

        @Override
        public void setIngredients(List<?> items) {
            ingredients.clear();
            for (int i = 0; i < items.size(); i++) {
                final Object ingredient = prepareIngredient(items.get(i));
                final PositionedStack stack = createPositionedStack(ingredient, i);
                ingredients.add(stack);
            }
        }

        @Nonnull
        private Object prepareIngredient(Object item) {
            if (item instanceof Integer) {
                return getValidOrbs((Integer) item);
            } else if (item instanceof ItemStack || item instanceof List) {
                return item;
            }
            throw new IllegalArgumentException("Invalid ingredient type: " + item);
        }

        @Nonnull
        private ArrayList<ItemStack> getValidOrbs(Integer minimumLevel) {
            ArrayList<ItemStack> orbs = new ArrayList<>();
            for (Item item : NEIConfig.bloodOrbs) {
                if (((IBloodOrb) item).getOrbLevel() >= minimumLevel) {
                    orbs.add(new ItemStack(item));
                }
            }
            return orbs;
        }

        private PositionedStack createPositionedStack(Object ingredient, int order) {
            final int relX = 25 + stackorder[order][0] * 18;
            final int relY = 6 + stackorder[order][1] * 18;
            PositionedStack stack = new PositionedStack(ingredient, relX, relY);
            stack.setMaxSize(1);
            return stack;
        }
    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("crafting") && getClass() == NEIBloodOrbShapelessHandler.class) {
            List<IRecipe> allrecipes = CraftingManager.getInstance().getRecipeList();
            for (IRecipe irecipe : allrecipes) {
                CachedBloodOrbRecipe recipe = null;
                if (irecipe instanceof ShapelessBloodOrbRecipe)
                    recipe = forgeShapelessRecipe((ShapelessBloodOrbRecipe) irecipe);

                if (recipe == null)
                    continue;

                arecipes.add(recipe);
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        List<IRecipe> allrecipes = CraftingManager.getInstance().getRecipeList();
        for (IRecipe irecipe : allrecipes) {
            if (NEIServerUtils.areStacksSameTypeCrafting(irecipe.getRecipeOutput(), result)) {
                CachedBloodOrbRecipe recipe = null;
                if (irecipe instanceof ShapelessBloodOrbRecipe)
                    recipe = forgeShapelessRecipe((ShapelessBloodOrbRecipe) irecipe);

                if (recipe == null)
                    continue;

                arecipes.add(recipe);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        List<IRecipe> allrecipes = CraftingManager.getInstance().getRecipeList();
        for (IRecipe irecipe : allrecipes) {
            CachedBloodOrbRecipe recipe = null;
            if (irecipe instanceof ShapelessBloodOrbRecipe)
                recipe = forgeShapelessRecipe((ShapelessBloodOrbRecipe) irecipe);

            if (recipe == null)
                continue;

            if (recipe.contains(recipe.ingredients, ingredient)) {
                recipe.setIngredientPermutation(recipe.ingredients, ingredient);
                arecipes.add(recipe);
            }
        }
    }

    public CachedBloodOrbRecipe forgeShapelessRecipe(ShapelessBloodOrbRecipe recipe) {
        ArrayList<Object> items = recipe.getInput();

        for (Object item : items)
            if (item instanceof List && ((List<?>) item).isEmpty())//ore handler, no ores
                return null;

        return new CachedBloodOrbRecipe(items, recipe.getRecipeOutput());
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(84, 23, 24, 18), "crafting"));
    }

    @Override
    public String getOverlayIdentifier() {
        return "crafting";
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("bm.string.crafting.orb.shapeless");
    }
}
