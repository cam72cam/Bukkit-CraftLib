package me.cmesh.CraftLib;

import java.util.*;

import org.apache.commons.lang.StringUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class LoreUtil {
	private LoreUtil() {};
	
	private static String seperator = ": ";
	
	public static void setValue(ItemStack item, String str, int value) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null) {
			lore = new ArrayList<String>();
			lore.add(str + seperator + value);
		} else {
			int count = 0;
			boolean found = false;
			for(String s : lore) {
				if (s.contains(str)) {
					found = true;
					lore.set(count, str + seperator + value);
					break;
				}
				count ++;
			}
			if (!found) {
				lore.add(str + seperator + value);
			}
		}
		meta.setLore(lore);
		item.setItemMeta(meta);
	}
	
	public static Integer getValue(ItemStack item, String str) {
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

	public static int getLevel(ItemStack item, String str) {
		Integer level = LoreUtil.getValue(item, str);
		return level == null ? 0 : level;
	}
	
	public static void addLevel(ItemStack item, String str, int add) {
		int level = getLevel(item, str);
		level += add;
		setLevel(item, str, level);
	}
	
	public static void setLevel(ItemStack item, String str, int level) {
		LoreUtil.setValue(item, str, level);
	}
}
