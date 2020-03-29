package com.loncha.gothiccharacters;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitScheduler;

import net.md_5.bungee.api.ChatColor;

public class CargarCajas implements Listener {
	Main m;
	HashMap<Player, Boolean> cooldown = new HashMap<Player, Boolean>();
	
	public CargarCajas(Main m) {
		this.m = m;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		cooldown.put(p, true);
	}
	
	@EventHandler
	public void onPickupEvent(EntityPickupItemEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			ItemStack item = e.getItem().getItemStack();
			
			ItemStack[] items = p.getInventory().getContents();
			
			Boolean ocupados = true;
			for (int i = 0; i < 9; i++) {
				if (items[i] == null) {
					ocupados = false;
				}
			}
			
			if (ocupados) {
				if (item.hasItemMeta()) {
					if (!item.getItemMeta().getDisplayName().contains("Mochila")) {
						e.setCancelled(true);
					}
				} else {
					e.setCancelled(true);
				}
				
			} else {
				if (item.getType().toString().contains("SHULKER")) {
					items = p.getInventory().getContents();
					
					Boolean conCaja = false;
					for (ItemStack i : items) {
						if (i != null) {
							if (i.getType().toString().contains("SHULKER")) {
								conCaja = true;
							}
						}
					}
					
					if (conCaja) {
						e.setCancelled(true);
						
						if (cooldown.get(p)) {
							p.sendMessage(ChatColor.RED+"Solo puedes cargar una caja a la vez");
							
							cooldown.put(p, false);
							
				            BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
				            scheduler.scheduleSyncDelayedTask(m, new Runnable() {
				                @Override
				                public void run() {
				                	cooldown.put(p, true);
				                }
				            }, 100);
				            
						}
					} else {
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999999, 3));
						items = p.getInventory().getContents();
	
						items = p.getInventory().getContents();
						
						for (int i = 0; i < 9; i++) {
							if (items [i] == null) {
								p.getInventory().setHeldItemSlot(i);
								break;
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		Block b = e.getBlock();
		
		if (b.getType().toString().contains("SHULKER")) {
			for (PotionEffect pEf : p.getActivePotionEffects()) {
				if (pEf.getType().toString().contains("SLOW")) {
					p.removePotionEffect(pEf.getType());
				}
			}
		}
	}
	
	@EventHandler 
	public void onPlayerChange(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		ItemStack item = p.getInventory().getItem(e.getPreviousSlot());
		
		if (item != null) {
			if (item.getType().toString().contains("SHULKER")) {
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onPlayerDrop(PlayerDropItemEvent e) {
		ItemStack item = e.getItemDrop().getItemStack();
		Player p = e.getPlayer();
		
		if (item.getType().toString().contains("SHULKER")) {
			for (PotionEffect pEf : p.getActivePotionEffects()) {
				if (pEf.getType().toString().contains("SLOW")) {
					p.removePotionEffect(pEf.getType());
				}
			}
		}
	}

}
