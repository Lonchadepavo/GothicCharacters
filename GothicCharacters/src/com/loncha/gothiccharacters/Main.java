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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.kazzababe.bukkit.NoNameTags;
import net.minecraft.server.v1_12_R1.EntityArmorStand;
import net.minecraft.server.v1_12_R1.PacketPlayOutEntityDestroy;
import net.minecraft.server.v1_12_R1.PacketPlayOutSpawnEntityLiving;

public class Main extends JavaPlugin implements Listener{
	//Declarar las clases del plugin
	InventarioCustom iCustom;
	CargarCajas cCajas;
	RopaAleatoria rAleatoria;
	
	public void onEnable() {
		//Inicializar las clases que utilizará el plugin (Algunos se inicializan con "this" porque requieren de la clase principal)
		iCustom = new InventarioCustom(this);
		cCajas = new CargarCajas(this);
		rAleatoria = new RopaAleatoria();
		
		//Pene
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
			getServer().dispatchCommand(getServer().getConsoleSender(), "upc addGroup" + p.getName() + " Usuario");
		}
		
	}
	
	//Detecta si rompes un bloque de tipo "BLACK_SHULKER_BOX" y cancela el evento (es una mochila que no pueden romper los usuarios)
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getBlock().getType() == Material.BLACK_SHULKER_BOX) {
			e.setCancelled(true);
		}
	}

}
