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
	public Map<ArrayList<UUID>,EntityArmorStand> hiddenmap = new HashMap<ArrayList<UUID>,EntityArmorStand>();
    //First Player in the List is the hidden one
	
	InventarioCustom iCustom;
	CargarCajas cCajas;
	RopaAleatoria rAleatoria;
	
	public void onEnable() {
		Bukkit.clearRecipes();
		iCustom = new InventarioCustom(this);
		cCajas = new CargarCajas(this);
		rAleatoria = new RopaAleatoria();
		
		getServer().getPluginManager().registerEvents(this, this);
		getServer().getPluginManager().registerEvents(this.iCustom, this);
		getServer().getPluginManager().registerEvents(this.cCajas, this);
		getServer().getPluginManager().registerEvents(this.rAleatoria, this);
		
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		if (!p.hasPlayedBefore()) {
			getServer().dispatchCommand(getServer().getConsoleSender(), "upc addGroup" + p.getName() + " Usuario");
		}
		
	}
	
	//Bloquear la destrucción de las mochilas
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (e.getBlock().getType() == Material.BLACK_SHULKER_BOX) {
			e.setCancelled(true);
		}
	}

}
