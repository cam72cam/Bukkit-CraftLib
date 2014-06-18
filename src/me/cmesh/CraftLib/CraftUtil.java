package me.cmesh.CraftLib;

import org.bukkit.Material;
import org.bukkit.inventory.ShapedRecipe;

public class CraftUtil {
	private CraftUtil(){}
	
	public static void SetupShapedRecipe3x3(ShapedRecipe r, Material ... ingredients) {
		if (ingredients.length != 9) {
			throw new IllegalArgumentException("3x3 recipe requires 9 items");
		}
		
		r.shape("abc", "def", "ghi");
		r.setIngredient('a', ingredients[0]);
		r.setIngredient('b', ingredients[1]);
		r.setIngredient('c', ingredients[2]);
		
		r.setIngredient('d', ingredients[3]);
		r.setIngredient('e', ingredients[4]);
		r.setIngredient('f', ingredients[5]);
		
		r.setIngredient('g', ingredients[6]);
		r.setIngredient('h', ingredients[7]);
		r.setIngredient('i', ingredients[8]);
	}
	
	public static void SetupShapedRecipe2x2(ShapedRecipe r, Material ... ingredients) {
		if (ingredients.length != 4) {
			throw new IllegalArgumentException("2x2 recipe requires 4 items");
		}
		
		r.shape("ab", "cd");
		r.setIngredient('a', ingredients[0]);
		r.setIngredient('b', ingredients[1]);
		
		r.setIngredient('c', ingredients[2]);
		r.setIngredient('d', ingredients[3]);
	}
}
