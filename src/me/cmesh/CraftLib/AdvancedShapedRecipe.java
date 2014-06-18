package me.cmesh.CraftLib;

import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedShapedRecipe extends ShapedRecipe {
private final AdvancedRecipe parent;
	
	public AdvancedShapedRecipe(ItemStack result, JavaPlugin base) {
		super(result);
		parent = new AdvancedRecipe(this, base);
	}
	
	public AdvancedShapedRecipe(ItemStack result) throws Exception {
		super(result);
		throw new Exception("Must not use the default constructor");
	}
	public void requireLore(Material m, List<String> lore) {
		parent.requireLore(m, lore);
	}
	
	public void requireEnchant(Material m, Map<Enchantment, Integer> enchantments){
		parent.requireEnchant(m, enchantments);
	}
	
	public void infiniteItem(ItemStack item) {
		parent.infiniteItem(item);
	}
	
	public void requireNone(Material m) {
		parent.requireNone(m);
	}
	
	//TODO override getIngredientMap
}
