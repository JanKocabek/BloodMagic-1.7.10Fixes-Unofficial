package WayofTime.alchemicalWizardry.client.nei;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.StatCollector;
import WayofTime.alchemicalWizardry.api.items.ShapedBloodOrbRecipe;
import WayofTime.alchemicalWizardry.api.items.interfaces.IBloodOrb;
import codechicken.nei.NEIServerUtils;
import codechicken.nei.PositionedStack;
import codechicken.nei.recipe.ShapedRecipeHandler;

/**
 * NEI Blood Orb Shaped Recipe Handler by joshie *
 */
public class NEIBloodOrbShapedHandler extends ShapedRecipeHandler {
    public class CachedBloodOrbRecipe extends CachedShapedRecipe {
        public CachedBloodOrbRecipe(int width, int height, Object[] items, ItemStack out) {
            super(width, height, items, out);
        }

        @Override

        public void setIngredients(int width, int height, Object[] items) {
            for (int i = 0; i < Math.min(width * height, items.length); i++) {
                Object ingredient = prepareIngredient(items[i]);
                if (ingredient != null) {
                    final int x = i % width;
                    final int y = i / width;
                    ingredients.add(createPositionedStack(ingredient, x, y));
                }
            }
        }

        private Object prepareIngredient(Object item) {
            Object ingredient = null;
            if (item instanceof Integer) {
                ingredient = getValidBloodOrbs((Integer) item);
            } else if (item instanceof ItemStack || item instanceof List) {
                ingredient = item;
            }
            return ingredient;

        }

        private PositionedStack createPositionedStack(Object ingredient, int x, int y) {
            final int relX = 25 + x * 18;
            final int relY = 6 + y * 18;
            PositionedStack positionedStack = new PositionedStack(ingredient, relX, relY, false);
            positionedStack.setMaxSize(1);
            return positionedStack;

        }


        private ArrayList<ItemStack> getValidBloodOrbs(int minLevel) {
            ArrayList<ItemStack> orbs = new ArrayList<>();
            for (Item item : NEIConfig.bloodOrbs) {
                if (((IBloodOrb) item).getOrbLevel() >= minLevel) {
                    orbs.add(new ItemStack(item));
                }
            }
            return orbs.isEmpty() ? null : orbs;
        }

    }

    @Override
    public void loadCraftingRecipes(String outputId, Object... results) {
        if (outputId.equals("crafting") && getClass() == NEIBloodOrbShapedHandler.class) {
            for (IRecipe irecipe : (List<IRecipe>) CraftingManager.getInstance().getRecipeList()) {
                if (irecipe instanceof ShapedBloodOrbRecipe) {
                    CachedBloodOrbRecipe recipe = forgeShapedRecipe((ShapedBloodOrbRecipe) irecipe);
                    if (recipe == null)
                        continue;

                    recipe.computeVisuals();
                    arecipes.add(recipe);
                }
            }
        } else {
            super.loadCraftingRecipes(outputId, results);
        }
    }

    @Override
    public void loadCraftingRecipes(ItemStack result) {
        for (IRecipe irecipe : (List<IRecipe>) CraftingManager.getInstance().getRecipeList()) {
            if (irecipe instanceof ShapedBloodOrbRecipe) {
                CachedBloodOrbRecipe recipe = forgeShapedRecipe((ShapedBloodOrbRecipe) irecipe);
                if (recipe == null || !NEIServerUtils.areStacksSameTypeCrafting(recipe.result.item, result))
                    continue;

                recipe.computeVisuals();
                arecipes.add(recipe);
            }
        }
    }

    @Override
    public void loadUsageRecipes(ItemStack ingredient) {
        for (IRecipe irecipe : (List<IRecipe>) CraftingManager.getInstance().getRecipeList()) {
            CachedShapedRecipe recipe = null;
            if (irecipe instanceof ShapedBloodOrbRecipe)
                recipe = forgeShapedRecipe((ShapedBloodOrbRecipe) irecipe);

            if (recipe == null || !recipe.contains(recipe.ingredients, ingredient.getItem()))
                continue;

            recipe.computeVisuals();
            if (recipe.contains(recipe.ingredients, ingredient)) {
                recipe.setIngredientPermutation(recipe.ingredients, ingredient);
                arecipes.add(recipe);
            }
        }
    }

    private CachedBloodOrbRecipe forgeShapedRecipe(ShapedBloodOrbRecipe recipe) {
        int width;
        int height;
        try {
            width = recipe.width;
            height = recipe.height;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        Object[] items = recipe.getInput();
        for (Object item : items)
            if (item instanceof List && ((List<?>) item).isEmpty())// ore
                // handler,
                // no ores
                return null;

        return new CachedBloodOrbRecipe(width, height, items, recipe.getRecipeOutput());
    }

    @Override
    public void loadTransferRects() {
        transferRects.add(new RecipeTransferRect(new Rectangle(84, 23, 24, 18), "crafting"));
    }

    @Override
    public String getRecipeName() {
        return StatCollector.translateToLocal("bm.string.crafting.orb.shaped");
    }
}
