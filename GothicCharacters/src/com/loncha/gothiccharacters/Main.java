package com.loncha.gothiccharacters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.kazzababe.bukkit.NoNameTags;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving;
import com.loncha.gothicchat.*;

public class Main extends JavaPlugin implements Listener{
	//Declarar las clases del plugin
	InventarioCustom iCustom;
	CargarCajas cCajas;
	RopaAleatoria rAleatoria;
	
	String[] listaCascos = {"Capucha completa", "Casco de capitan de la guardia", "Casco de caballero", "Casco de cruzado", "Casco de paladin pesado"};
	
	public void onEnable() {
		Bukkit.clearRecipes();
		//Inicializar las clases que utilizará el plugin (Algunos se inicializan con "this" porque requieren de la clase principal)
		iCustom = new InventarioCustom(this);
		cCajas = new CargarCajas(this);
		rAleatoria = new RopaAleatoria();
		
		//Tercera prueba
		//Registrar los listeners de cada una de las clases
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(this.iCustom, this);
		getServer().getPluginManager().registerEvents(this.cCajas, this);
		getServer().getPluginManager().registerEvents(this.rAleatoria, this);
		
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		//Cuando un jugador entra por primera vez al servidor le añade el grupo de permisos "Usuario"
		if (!p.hasPlayedBefore()) {
			getServer().dispatchCommand(getServer().getConsoleSender(), "upc addGroup " + p.getName() + " Usuario");
		}
		
		checkCapucha(p);
		
	}
	
	@EventHandler
	public void onHandChange(PlayerItemHeldEvent e) {
		Player p = e.getPlayer();
		checkCapucha(p);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		 Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
             public void run() {
         			checkCapucha(p);
                 }
             }, 1);
	}
	
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		checkCapucha(p);
	}
	
	//Detecta si rompes un bloque de tipo "BLACK_SHULKER_BOX" y cancela el evento (es una mochila que no pueden romper los usuarios)
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getBlock().getType() == Material.BLACK_SHULKER_BOX) {
			e.setCancelled(true);
		}
	}
	
	public void checkCapucha(Player p) {
		Inventory pInv = p.getInventory();
		ItemStack[] invContent = pInv.getContents();
		
		if (invContent[39] != null) {
			ItemStack casco = invContent[39];
			
			if (casco.hasItemMeta()) {
				for (String s : listaCascos) {
					if (casco.getItemMeta().getDisplayName().contains(s)) {
						p.setDisplayName("???");
						break;
					} else {
						p.setDisplayName(com.loncha.gothicchat.Main.datosFicha.get(p)[0]);
					}
				}
			}
		} else {
			if (com.loncha.gothicchat.Main.datosFicha.get(p) != null) {
				p.setDisplayName(com.loncha.gothicchat.Main.datosFicha.get(p)[0]);
			}
		}
	}

}
