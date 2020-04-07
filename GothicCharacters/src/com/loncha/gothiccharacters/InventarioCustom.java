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
	//Declarar la clase principal
	Main m;
	
	//Cargar la clase principal en el constructor
	public InventarioCustom(Main m) {
		this.m = m;
	}

	//Crea el inventario personalizado para el jugador, todos los huecos están bloqueados excepto la barra de acceso rápido, el slot 35 (para guardar una mochila)
	//Y los slots de armadura y mano secundaria.
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		//Crea el itemstack de panel de cristal
		ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE);
		ItemMeta imeta = item.getItemMeta();
		imeta.setDisplayName("."); //Le establece de nombre un "."
		item.setItemMeta(imeta); //Le establece el meta al itemstack
		
		//Guarda el inventario del jugador y recoge todos los objetos que tiene en un array de ItemStack.
		Inventory inv = p.getInventory();
		ItemStack[] items = inv.getContents();
		
		//Llena el array del inventario con los item creados antes (paneles de cristal), donde haya un panel de cristal se considera un slot bloqueado (no se puede usar)
		for (int i = 9; i < 35; i++) {
			items[i] = item;
		}
		
		//Le establece el nuevo inventario
		inv.setContents(items);
	}
	
	//Código para que cuando el jugador muera se cree una mochila en el suelo con todos sus items.
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e) {
		//Hace que el jugador no pierda el inventario al morir
		e.setKeepInventory(true);
		Player p = e.getEntity().getPlayer();
		
		Inventory inv = p.getInventory(); //Guarda el inventario del player
		ItemStack[] items = inv.getContents(); //Guarda los items del inventario
		
		ArrayList<ItemStack> itemsMochila = new ArrayList<ItemStack>(); //Crea un arraylist con los items que se guardarán en la mochila
		
		//Recorre el inventario del player en posiciones específicas, los quita del inventario y los añade al arraylist de la mochila
		//Elimina todos los items del inventario excepto los paneles de cristal (no se pierden, ya que no se consideran objetos, están ahí para bloquear el inventario)
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
		
		inv.setContents(items); //Se establece el nuevo inventario
		
		deathBag(itemsMochila, p); //Se llama al método que crea la mochila
		
	}
	
	//Detecta el click en el inventario
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		
		if (e.getClickedInventory() != null) { //Si has hecho click DENTRO de un inventario
			if (e.getClickedInventory().getType() == InventoryType.PLAYER ) { //Si has hecho click dentro del inventario del jugador.
				if (!p.isOp()) { //Si el jugador no es op (administrador)
					if (e.getSlot() > 8 && e.getSlot() != 35) { //Si ha hecho click en un slot bloqueado
						if (e.getSlotType() != SlotType.ARMOR) { //Si el slot no es de tipo ARMADURA
							e.setCancelled(true); //Cancela el evento (no puedes interactuar con un slot bloqueado)
							
							ItemStack item = e.getCursor(); //Guarda el item que tienes seleccionado con el cursor
							e.getInventory().remove(e.getSlot());
							
							//Si el item no es null y no es de tipo aire, lo suelta en el suelto y te lo quita del cursor
							if (item != null && item.getType() != Material.AIR) {
								p.getWorld().dropItem(p.getLocation(), item);
							}
							
							e.setCursor(new ItemStack(Material.AIR));
						}
						
					} else if (e.getSlot() == 35) { //Si has hecho click en el slot de la mochila y no intentas guardar una mochila, no te deja hacerlo
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
	
	//Método que crea la mochila en el suelo al morir el jugador
	public void deathBag(ArrayList<ItemStack> items, Player p) {
		Block b = p.getLocation().getBlock(); //Guarda el bloque que se localiza donde ha muerto el jugador
		
		//Comprueba si el bloque es de tipo SLAB o STEP, si lo es mueve el bloque mochila 1 hacia arriba (para no reemplazar la SLAB/STEP)
		if (b.getType().toString().contains("SLAB") || b.getType().toString().contains("STEP") ) {
			Location l = new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY()+1,p.getLocation().getZ());		
			b = l.getBlock();
		}
		
		//Crea la mochila y guarda dentro todos los items del jugador
		b.setType(Material.BLACK_SHULKER_BOX);	
		ShulkerBox s = (ShulkerBox) b.getState();	
		Inventory inv = s.getInventory();	
		ItemStack[] itemsFinal = new ItemStack[items.size()];
		
		for (int i = 0; i < items.size(); i++) {
			itemsFinal[i] = items.get(i);
		}
		
		inv.setContents(itemsFinal);
		
		//Se crea una tarea que se ejectuará dentro de 300 ticks (15 segundos) para hacer que la mochila desaparezca
		BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(m, new EliminarMochila(b), 300);
	}
}
