package me.cmesh.CraftLib;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LoreUtil {
	private LoreUtil() {};
	
	private static String seperator = ": ";
	
	public void setValue(ItemStack item, String str, int value) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		int index = 0;
		if (lore == null) {
			lore = new ArrayList<String>();
		}
		int count = 0;
		for(String s : lore) {
			if (s.contains(str)) {
				index = count;
			}
			count ++;
		}
		
		lore.set(index, str + seperator + value);
		meta.setLore(lore);
		item.setItemMeta(meta);
	}
	
	public Integer getValue(ItemStack item, String str) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		
		if (lore == null) {
			return null;
		}
		
		for (String s : lore) {
			if (s.contains(str + seperator)) {
				s = StringUtils.remove(s, str + seperator);
				if (StringUtils.isNumeric(s) ){
					return Integer.parseInt(s);
				}
			}
		}
		
		return null;
	}
}
