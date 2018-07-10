package org.ctp.enchantmentsolution.listeners.abilities;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ctp.enchantmentsolution.enchantments.DefaultEnchantments;
import org.ctp.enchantmentsolution.enchantments.Enchantments;

import org.bukkit.ChatColor;

@SuppressWarnings("deprecation")
public class FishingListener implements Listener{
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onPlayerFish(PlayerFishEvent event) {
		if(event.getState().equals(PlayerFishEvent.State.CAUGHT_FISH)) {
			Item item = (Item)event.getCaught();
			ItemStack caught = item.getItemStack();
			Player player = event.getPlayer();
			ItemStack rod = player.getInventory().getItemInMainHand();
			if(Enchantments.hasEnchantment(rod, DefaultEnchantments.FRIED)) {
				if(DefaultEnchantments.isEnabled(DefaultEnchantments.FRIED)) {
					if(Bukkit.getPluginManager().isPluginEnabled("mcMMO")) {
						if(caught.getType().equals(Material.COD) || caught.getType().equals(Material.SALMON)) {
							ItemMeta caughtMeta = caught.getItemMeta();
							caughtMeta.setDisplayName(ChatColor.RED + "CookedFish");
							caught.setItemMeta(caughtMeta);
						}
					} else {
						if(caught.getType().equals(Material.COD)) {
							caught.setType(Material.COOKED_COD);
						}else if(caught.getType().equals(Material.SALMON)) {
							caught.setType(Material.COOKED_SALMON);
						}
					}
				}
			}
			if(Enchantments.hasEnchantment(rod, DefaultEnchantments.ANGLER)) {
				if(DefaultEnchantments.isEnabled(DefaultEnchantments.ANGLER)) {
					List<Material> fish = Arrays.asList(Material.COD, Material.COOKED_COD, Material.SALMON, Material.COOKED_SALMON, Material.TROPICAL_FISH, Material.PUFFERFISH);
					if(fish.contains(caught.getType())) {
						caught.setAmount(1 + Enchantments.getLevel(rod, DefaultEnchantments.ANGLER));
					}
				}
			}
			((Item) event.getCaught()).setItemStack(caught);
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if(!DefaultEnchantments.isEnabled(DefaultEnchantments.FRIED)) return;
		Item item = event.getItem();
		ItemStack items = item.getItemStack();
		ItemMeta meta = items.getItemMeta();
		if((items.getType().equals(Material.COD) || items.getType().equals(Material.SALMON)) && meta.getDisplayName() != null && meta.getDisplayName().equals(ChatColor.RED + "CookedFish")) {
			meta.setDisplayName("");
			items.setItemMeta(meta);
			if (items.getType().equals(Material.COD)) {
				items.setType(Material.COOKED_COD);
			} else if (items.getType().equals(Material.SALMON)) {
				items.setType(Material.COOKED_SALMON);
			}
		}
	}
}
