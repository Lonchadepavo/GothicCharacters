package com.loncha.gothiccharacters;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

public class InventarioCustom implements Listener{
	Main m;
	
	public InventarioCustom(Main m) {
		this.m = m;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE);
		ItemMeta imeta = item.getItemMeta();
		imeta.setDisplayName(".");
		item.setItemMeta(imeta);
		
		Inventory inv = p.getInventory();
		ItemStack[] items = inv.getContents();
		
		for (int i = 9; i < 35; i++) {
			items[i] = item;
		}
		
		inv.setContents(items);
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		e.setKeepInventory(true);
		Player p = e.getEntity().getPlayer();
		Inventory inv = p.getInventory();
		ItemStack[] items = inv.getContents();
		
		ArrayList<ItemStack> itemsMochila = new ArrayList<ItemStack>();
		
		
		for (int i = 0; i < 9; i++) {
			if (items[i] != null) {
				itemsMochila.add(items[i]);
			}
			items[i] = null;
		}
		
		for (int i = 35; i < 41; i++) {
			if (items[i] != null) {
				itemsMochila.add(items[i]);
			}
			items[i] = null;
		}
		
		inv.setContents(items);
		
		deathBag(itemsMochila, p);
		
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		if (e.getClickedInventory() != null) {
			if (e.getClickedInventory().getType() == InventoryType.PLAYER ) {
				if (!p.isOp()) {
					if (e.getSlot() > 8 && e.getSlot() != 35) {
						if (e.getSlotType() != SlotType.ARMOR) {
							e.setCancelled(true);
							
							ItemStack item = e.getCursor();
							e.getInventory().remove(e.getSlot());
							if (item != null && item.getType() != Material.AIR) {
								p.getWorld().dropItem(p.getLocation(), item);
							}
							
							e.setCursor(new ItemStack(Material.AIR));
						}
					} else if (e.getSlot() == 35) {
						if (p.getItemOnCursor() != null && p.getItemOnCursor().getType() != Material.AIR) {
							if (p.getItemOnCursor().hasItemMeta()) {
								if (!p.getItemOnCursor().getItemMeta().getDisplayName().contains("Mochila")) {
									e.setCancelled(true);
								}
							} else {
								e.setCancelled(true);
							}
						}
					}
				}
			}
		}
	}
	
	public void deathBag(ArrayList<ItemStack> items, Player p) {
		Block b = p.getLocation().getBlock();

		if (b.getType().toString().contains("SLAB") || b.getType().toString().contains("STEP") ) {
			Location l = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY()+1,p.getLocation().getZ());		
			b = l.getBlock();
		}
		
		b.setType(Material.BLACK_SHULKER_BOX);	
		ShulkerBox s = (ShulkerBox) b.getState();	
		Inventory inv = s.getInventory();	
		ItemStack[] itemsFinal = new ItemStack[items.size()];
		
		for (int i = 0; i < items.size(); i++) {
			itemsFinal[i] = items.get(i);
		}
		
		inv.setContents(itemsFinal);
		
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(m, new EliminarMochila(b), 300);
	}
}
