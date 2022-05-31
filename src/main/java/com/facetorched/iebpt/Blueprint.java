package com.facetorched.iebpt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import blusunrize.immersiveengineering.api.crafting.BlueprintCraftingRecipe;
import minetweaker.IUndoableAction;
import minetweaker.MineTweakerAPI;
import minetweaker.api.item.IIngredient;
import minetweaker.api.item.IItemStack;
import minetweaker.api.minecraft.MineTweakerMC;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import stanhebben.zenscript.annotations.ZenClass;
import stanhebben.zenscript.annotations.ZenMethod;

@ZenClass("mods.immersiveengineering.Blueprint")
public class Blueprint
{
	@ZenMethod
	public static void addRecipe(String category, IItemStack output, IIngredient[] inputs)
	{
		Object[] oInputs = new Object[inputs.length];
			for(int i = 0; i < inputs.length; i++)
				oInputs[i] = MineTweakerMC.getItemStack(inputs[i]);
		BlueprintCraftingRecipe r = new BlueprintCraftingRecipe(MineTweakerMC.getItemStack(output), oInputs);
		MineTweakerAPI.apply(new Add(category, r));
	}

	private static class Add implements IUndoableAction
	{
		private final BlueprintCraftingRecipe recipe;
		private final String category;

		public Add(String category, BlueprintCraftingRecipe recipe)
		{
			this.recipe = recipe;
			this.category = category;
			
		}

		@Override
		public void apply()
		{
			System.out.println("NOTIFICATION_IE: ADDING RECIPE FOR"+recipe.output);
			if(!BlueprintCraftingRecipe.blueprintCategories.contains(category))
				BlueprintCraftingRecipe.blueprintCategories.add(category);
			BlueprintCraftingRecipe.recipeList.put(category, recipe);
			//MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipe);
			//IECompatModule.jeiAddFunc.accept(recipe);
		}

		@Override
		public boolean canUndo()
		{
			return true;
		}

		@Override
		public void undo()
		{
			BlueprintCraftingRecipe.recipeList.remove(category, recipe);
			if(BlueprintCraftingRecipe.recipeList.get(category).isEmpty())
				BlueprintCraftingRecipe.blueprintCategories.remove(category);
			//MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(recipe);
			//IECompatModule.jeiRemoveFunc.accept(recipe);
		}

		@Override
		public String describe()
		{
			return "Adding Blueprint Recipe for " + recipe.output.getDisplayName();
		}

		@Override
		public String describeUndo()
		{
			return "Removing Blueprint Recipe for " + recipe.output.getDisplayName();
		}

		@Override
		public Object getOverrideKey()
		{
			return null;
		}
	}

	@ZenMethod
	public static void removeRecipe(IItemStack output)
	{
		MineTweakerAPI.apply(new Remove(MineTweakerMC.getItemStack(output)));
	}

	private static class Remove implements IUndoableAction
	{
		private final ItemStack output;
		List<BlueprintCraftingRecipe> removedRecipes;
		List<String> removedCategories;

		public Remove(ItemStack output)
		{
			this.output = output;
		}

		@Override
		public void apply()
		{
			removedRecipes = new ArrayList<BlueprintCraftingRecipe>();
			removedCategories = new ArrayList<String>();
			Iterator<String> itCat = BlueprintCraftingRecipe.blueprintCategories.iterator();
			while(itCat.hasNext())
			{
				String category = itCat.next();
				Iterator<BlueprintCraftingRecipe> it = BlueprintCraftingRecipe.recipeList.get(category).iterator();
				while(it.hasNext())
				{
					BlueprintCraftingRecipe ir = it.next();
					if(OreDictionary.itemMatches(ir.output, output, true))
					{
						removedRecipes.add(ir);
						removedCategories.add(category);
						//MineTweakerAPI.getIjeiRecipeRegistry().removeRecipe(ir);
						//IECompatModule.jeiRemoveFunc.accept(ir);
						it.remove();
					}
				}
				if(BlueprintCraftingRecipe.recipeList.get(category).isEmpty())
					itCat.remove();
			}
		}

		@Override
		public void undo()
		{
			if(removedRecipes != null) {
				for(int i = 0; i < removedRecipes.size(); i++) {
					BlueprintCraftingRecipe recipe = removedRecipes.get(i);
					String category = removedCategories.get(i);
					if(recipe != null)
					{
						if(!BlueprintCraftingRecipe.blueprintCategories.contains(category))
							BlueprintCraftingRecipe.blueprintCategories.add(category);
						BlueprintCraftingRecipe.recipeList.put(category, recipe);
						//MineTweakerAPI.getIjeiRecipeRegistry().addRecipe(recipe);
						//IECompatModule.jeiAddFunc.accept(recipe);
					}
				}
			}
		}

		@Override
		public String describe()
		{
			return "Removing Blueprint Recipe for " + output.getDisplayName();
		}

		@Override
		public String describeUndo()
		{
			return "Re-Adding Blueprint Recipe for " + output.getDisplayName();
		}

		@Override
		public Object getOverrideKey()
		{
			return null;
		}

		@Override
		public boolean canUndo()
		{
			return true;
		}
	}
}
