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
	HashMap<Player, Boolean> cooldown = new HashMap<Player, Boolean>(); //Checker para un cooldown almacenado en un hashmap para cada jugador.
	
	public CargarCajas(Main m) {
		this.m = m;
	}
	
	//Cuando entra un jugador al servidor se le establece el cooldown en true (listo para usarse)
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		cooldown.put(p, true);
	}
	
	//Detecta cuando un jugador recoge un item del suelo
	@EventHandler
	public void onPickupEvent(EntityPickupItemEvent e) {
		//Se comprueba si el item ha sido recogido por un Player
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			ItemStack item = e.getItem().getItemStack();
			
			ItemStack[] items = p.getInventory().getContents();
			
			//Loop que comprueba si tienes hueco en el inventario (debido a que tenemos un inventario personalizado hay que comprobar manualmente si hay espacio
			//en el inventario), por defecto el inventario está ocupado, pero si encuentra un hueco vacío ocupado pasa a false (no comprueba el hueco de mochila)
			Boolean ocupados = true;
			for (int i = 0; i < 9; i++) {
				if (items[i] == null) {
					ocupados = false;
				}
			}
			
			//Si todos los huecos están ocupados excepto el de mochila
			if (ocupados) {
				//Si estás recogiendo un item custom pero no es una mochila cancela el evento de recogida
				if (item.hasItemMeta()) {
					if (!item.getItemMeta().getDisplayName().contains("Mochila")) {
						e.setCancelled(true);
					}
					
				//Si no estás recogiendo un item custom cancela el evento de recogida
				} else {
					e.setCancelled(true);
				}
				
			} else { //Si el inventario no está ocupado y estás recogiendo una caja ("SHULKER")
				if (item.getType().toString().contains("SHULKER")) {
					items = p.getInventory().getContents();
					
					//Loop que comprueba si ya tienes otra caja en el inventario (solo puedes tener una)
					Boolean conCaja = false;
					for (ItemStack i : items) {
						if (i != null) {
							if (i.getType().toString().contains("SHULKER")) {
								conCaja = true;
							}
						}
					}
					
					//Si ya tienes una caja cancela el evento de recogida, te muestra un mensaje y activa el cooldown y una tarea programada a 100 ticks para desactivar
					//el cooldown
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
					//Si no tienes una caja encima te aplica un efecto de SLOW (lentitud) infinito
					} else {
						p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 99999999, 3));
						items = p.getInventory().getContents();
						
						//Te cambia el hueco seleccionado al hueco en el que has guardado la caja
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
	
	//Detecta si colocas un bloque, si colocas la caja en el suelo automaticamente te quita el efecto de lentitud
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
	
	//Comprueba si estás cambiando el item que tienes en la mano, si tienes una caja en el inventario no puedes cambiar el item, solo puedes llevar la caja
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
	
	//Comprueba si sueltas un item, si ese item es una caja te quita el efecto de lentitud
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
