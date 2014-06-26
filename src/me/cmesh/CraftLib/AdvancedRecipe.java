package me.cmesh.CraftLib;

import java.util.*;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

public class AdvancedRecipe implements Recipe, Listener {
	private Recipe base;
	
	private Map<Material, List<String>> lore = new HashMap<Material, List<String>>();
	private Map<Material, Map<Enchantment, Integer>> enchantments = new HashMap<Material, Map<Enchantment, Integer>>();
	private List<ItemStack> infinite = new ArrayList<ItemStack>();
	private JavaPlugin plugin;
	
	public AdvancedRecipe(Recipe base, JavaPlugin plugin) {
		this.base = base;
		this.plugin = plugin;
		
		Bukkit.getServer().getPluginManager().registerEvents(this, this.plugin);
	}
	
	public void requireLore(Material m, List<String> lore) {
		this.lore.put(m, lore);
	}
	
	public void requireEnchant(Material m, Map<Enchantment, Integer> enchantments){
		this.enchantments.put(m, enchantments);
	}

	public void requireNone(Material m) {
		this.lore.put(m, null);
		this.enchantments.put(m, null);
	}
	
	public void infiniteItem(ItemStack item) {
		infinite.add(item);
	}
	
	private boolean isInfinite(ItemStack item) {
		for (ItemStack curr : infinite) {
			if (item != null &&
				curr != null &&
				curr.getEnchantments().equals(item.getEnchantments()) &&
				curr.getItemMeta().getLore().equals(item.getItemMeta().getLore()) &&
				curr.getItemMeta().getDisplayName().equals(item.getItemMeta().getDisplayName()) &&
				curr.getDurability() == item.getDurability() &&
				curr.getData().equals(item.getData())) {
				return true;
			}
		}
		return false;
	}
	
	@EventHandler()
	public void onOpenCraft(InventoryOpenEvent ev) {
		final Inventory topInv = ev.getView().getTopInventory();
		final Player p = (Player)ev.getPlayer();
		if (topInv.getType() == InventoryType.WORKBENCH) {
			final JavaPlugin pl = this.plugin; 
			Bukkit.getScheduler().runTaskLater(this.plugin, 
					new Runnable() {
						@Override
						@SuppressWarnings("deprecation")
						public void run() {
							CraftingInventory inv = (CraftingInventory)topInv;
							//If we are here, the crafting grid is open, yet the recipe exists
							//No player can put the items into the crafting table within 2 ticks
							//hopefully
							if (isUs(inv.getRecipe())) {
								//p.sendMessage("HERE2!");
								for(ItemStack i : inv.getMatrix()) {
									if (i != null) {
										setAttributes(i);
									}
								}
								Bukkit.getScheduler().runTaskLater(pl, new Runnable() {
									@Override
									public void run() {
										p.updateInventory();
									}
									
								}, 1l);
							}
						}
					}, 1l
				);
		}
	}

	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSetupCraft(InventoryClickEvent ev) {
		final Inventory topInv = ev.getView().getTopInventory();
		final Player p = (Player)ev.getWhoClicked();
		if(topInv.getType() == InventoryType.WORKBENCH) {
			//Item will not be in the inventory until after this event concludes 
			Bukkit.getScheduler().runTaskLater(this.plugin, 
				new Runnable() {
					@Override
					@SuppressWarnings("deprecation")
					public void run() {
						CraftingInventory inv = (CraftingInventory)topInv;
						//p.sendMessage(inv.getRecipe() + "foobar");
						if (isUs(inv.getRecipe())) {
							//p.sendMessage("HERE3!");
							if (!checkInv(inv) && inv.getResult() != null) {
								inv.setResult(null);
								p.updateInventory();
							}
						}
					}
				}, 1l
			);
		}
	}
	
	
	//We want to be the last one that runs
	@EventHandler(priority = EventPriority.LOW)
	public void onCraft(CraftItemEvent ev) {
		final Player p = (Player)ev.getWhoClicked();
		final CraftingInventory inv = ev.getInventory();
		Recipe r = ev.getRecipe();
		if(!isUs(r)) {
			return;
		}
		
		int amount = 1;
		//Shift click or Right click
		if (ev.getAction() == InventoryAction.MOVE_TO_OTHER_INVENTORY || ev.getAction() == InventoryAction.PICKUP_HALF) {
			int max = 64;
			for (ItemStack item : inv.getMatrix()) {
				if (	item != null && //item exists
						item.getAmount() != 0 && //Item is really there
						!isInfinite(item) && //We are not a many use item
						max > item.getAmount()) {
					
					max = item.getAmount();
				}
			}
			amount = max;
		}
		
		if (!checkInv(inv)) {
			ev.setCancelled(true);
			return;
		}
		//If we are here, we are good to continue crafting

		//Take care of infinite items
		//After this we are modifying stack size so 
		//DO NOT CANCEL AFTER THIS LINE
		for (ItemStack item : inv.getMatrix()) {
			if (isInfinite(item)) {
				item.setAmount(item.getAmount() + amount);
			}
		}
		
		//Craft must finish before we can "update" the inventory
		//This should not be necessary, but stuff breaks without it 
		Bukkit.getScheduler().runTaskLater(this.plugin, 
			new Runnable() {
				@SuppressWarnings("deprecation")
				@Override
				public void run() {
					p.updateInventory(); //This sucks but is necessary
				}
			}, 1l
		);
	}
	
	private boolean checkInv(CraftingInventory inv) {
		//Check Lore and Enchantments
		for (final ItemStack item : inv.getMatrix()) {
			if (item != null) {
				ItemMeta meta = item.getItemMeta();
				Material itemtype = item.getType();
				//Check Lore
				if (this.lore.containsKey(itemtype)) {
					List<String> lore = meta.getLore();
					
					//Check for none
					if(this.lore.get(itemtype) == null) {
						if (lore != null && !lore.isEmpty()) {
							return false;
						}
					} else {
						if (lore == null || !lore.equals(this.lore.get(itemtype))) {
							return false;
						}
					}
				}
				//Check Enchantments
				if (this.enchantments.containsKey(itemtype)) {
					Map<Enchantment, Integer> enchantments = meta.getEnchants();
					
					//Check for none
					if(this.enchantments.get(itemtype) == null) {
						if (enchantments != null && !enchantments.isEmpty()) {
							return false;
						}
					} else {
						for(Entry<Enchantment, Integer> set : this.enchantments.get(itemtype).entrySet()) {
							if (set.getValue() == -1) { //Check for existence
								if (!enchantments.containsKey(set.getKey())) {
									return false;
								}
							} else { // check existence and value
								if (!	enchantments.containsKey(set.getKey()) || 
										enchantments.get(set.getKey()) != set.getValue()) {
									return false;
								}
							}
						}
					}
				}
			}
		}
		return true;
	}
	
	private boolean isUs(Recipe r) {
		if (r instanceof ShapelessRecipe && base instanceof ShapelessRecipe) {
			ShapelessRecipe sr = (ShapelessRecipe)r;
			ShapelessRecipe srbase = (ShapelessRecipe)base;
			if (! sr.getIngredientList().equals(srbase.getIngredientList())) {
				return false;
			}
		} else if (r instanceof ShapedRecipe && base instanceof ShapedRecipe) {
			ShapedRecipe sr = (ShapedRecipe)r;
			ShapedRecipe srbase = (ShapedRecipe)base;
			if (! sr.getIngredientMap().equals(srbase.getIngredientMap())) {
				//not us
				return false;
			}
		} else {
			//not us
			return false;
		}
		return true;
	}

	@Override
	public ItemStack getResult() {
		return base.getResult();
	}

	public ItemStack setAttributes(ItemStack item) {
		ItemMeta meta = item.getItemMeta();
		Material itemtype = item.getType();
		//Check Lore
		if (this.lore.containsKey(itemtype) && this.lore.get(itemtype) != null) {
			meta.setLore(this.lore.get(itemtype));
			item.setItemMeta(meta);
		}
		
		//Check Enchantments
		if (this.enchantments.containsKey(itemtype) && this.enchantments.get(itemtype) != null) {
			item.addUnsafeEnchantments(this.enchantments.get(itemtype));
		}
		return item;
	}
}
