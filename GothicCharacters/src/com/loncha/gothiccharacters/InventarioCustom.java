package com.loncha.gothiccharacters;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitScheduler;

public class InventarioCustom implements Listener{
	//Declarar la clase principal
	Main m;
	
	HashMap<Player, ItemStack[]> baseInv= new HashMap<Player, ItemStack[]>();
	
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
		
		//Botones
		ItemStack botonProfesiones = new ItemStack(Material.IRON_PICKAXE,1);
		ItemMeta profesionesMeta = botonProfesiones.getItemMeta();
		profesionesMeta.setDisplayName("§fProfesiones");
		profesionesMeta.setLore(new ArrayList<String>(Arrays.asList("Abrir la interfaz de profesiones.")));
		profesionesMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		botonProfesiones.setItemMeta(profesionesMeta);
		
		ItemStack botonFichas = new ItemStack(Material.PAPER,1);
		ItemMeta fichasMeta = botonFichas.getItemMeta();
		fichasMeta.setDisplayName("§fFichas de personaje");
		fichasMeta.setLore(new ArrayList<String>(Arrays.asList("Abrir la interfaz de fichas de personaje.")));
		fichasMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		botonFichas.setItemMeta(fichasMeta);
		
		ItemStack botonAcciones = new ItemStack(Material.WOOD_SWORD,1);
		ItemMeta accionesMeta = botonAcciones.getItemMeta();
		accionesMeta.setDisplayName("§fLista de acciones");
		accionesMeta.setLore(new ArrayList<String>(Arrays.asList("Abrir la interfaz de acciones.")));
		accionesMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		botonAcciones.setItemMeta(accionesMeta);
		
		ItemStack botonGuia = new ItemStack(Material.COMPASS,1);
		ItemMeta guiaMeta = botonGuia.getItemMeta();
		guiaMeta.setDisplayName("§fGuía Ingame");
		guiaMeta.setLore(new ArrayList<String>(Arrays.asList("Abrir la interfaz de la guía ingame.")));
		guiaMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
		botonGuia.setItemMeta(guiaMeta);
		
		//Guarda el inventario del jugador y recoge todos los objetos que tiene en un array de ItemStack.
		Inventory inv = p.getInventory();
		ItemStack[] items = inv.getContents();
		
		//Llena el array del inventario con los item creados antes (paneles de cristal), donde haya un panel de cristal se considera un slot bloqueado (no se puede usar)
		for (int i = 9; i < 35; i++) {
			items[i] = item;
		}
		
		//Le establece el nuevo inventario
		inv.setContents(items);
		
		inv.setItem(19, botonProfesiones);
		inv.setItem(21, botonFichas);
		inv.setItem(23, botonAcciones);
		inv.setItem(25, botonGuia);
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
				if (p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.ADVENTURE && p.getGameMode() != GameMode.SPECTATOR) { //Si el jugador no es op (administrador)
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
							
							if (e.getCurrentItem().hasItemMeta()) {
								switch(e.getCurrentItem().getItemMeta().getDisplayName()) {
									case "§fProfesiones":
										p.performCommand("profesiones");
									break;
									
									case "§fFichas de personaje": {
										baseInv.put(p, p.getInventory().getContents());
										Inventory replaceInv = p.getInventory();
										ItemStack[] items = replaceInv.getContents();
										
										//ITEM DE FONDO
										ItemStack itemBackground = new ItemStack(Material.STAINED_GLASS_PANE);
										ItemMeta imeta = itemBackground.getItemMeta();
										imeta.setDisplayName("."); //Le establece de nombre un "."
										itemBackground.setItemMeta(imeta); //Le establece el meta al itemstack
										
										//Llena el array del inventario con los item creados antes (paneles de cristal), donde haya un panel de cristal se considera un slot bloqueado (no se puede usar)
										for (int i = 9; i < 35; i++) {
											items[i] = itemBackground;
										}
										
										ArrayList<ItemStack> playerItems = new ArrayList<ItemStack>();
										
										for (Player player : Bukkit.getOnlinePlayers()) {
											ItemStack playerItem = new ItemStack(Material.PAPER, 1);
											ItemMeta playerMeta = playerItem.getItemMeta();
											playerMeta.setDisplayName(player.getName());
											playerItem.setItemMeta(playerMeta);
											
											
											playerItems.add(playerItem);
										}
										
										for (int i = 0; i < playerItems.size(); i++) {
											items[i+9] = playerItems.get(i);
										}
										
										ItemStack botonVolver = new ItemStack(Material.REDSTONE,1);
										ItemMeta volverMeta = botonVolver.getItemMeta();
										volverMeta.setDisplayName("§fVolver atrás");
										volverMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
										botonVolver.setItemMeta(volverMeta);
										
										replaceInv.setContents(items);
										
										replaceInv.setItem(34, botonVolver);
									}
									break;
									
									case "§fLista de acciones":
										baseInv.put(p, p.getInventory().getContents());
										Inventory replaceInv = p.getInventory();
										ItemStack[] items = replaceInv.getContents();
										
										//ITEM DE FONDO
										ItemStack itemBackground = new ItemStack(Material.STAINED_GLASS_PANE);
										ItemMeta imeta = itemBackground.getItemMeta();
										imeta.setDisplayName("."); //Le establece de nombre un "."
										itemBackground.setItemMeta(imeta); //Le establece el meta al itemstack
										
										//Llena el array del inventario con los item creados antes (paneles de cristal), donde haya un panel de cristal se considera un slot bloqueado (no se puede usar)
										for (int i = 9; i < 35; i++) {
											items[i] = itemBackground;
										}
										
										//Colocar las opciones en los slots 12,13,14,21,22,23,30,31,32
										ItemStack botonSentarse = new ItemStack(Material.WOOD_STAIRS,1);
										ItemMeta sentarseMeta = botonSentarse.getItemMeta();
										sentarseMeta.setDisplayName("§fSentarse");
										sentarseMeta.setLore(new ArrayList<String>(Arrays.asList("Acceso rápido a la acción de sentarse.")));
										sentarseMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
										botonSentarse.setItemMeta(sentarseMeta);
										
										ItemStack botonTumbarse = new ItemStack(Material.BED,1);
										ItemMeta tumbarseMeta = botonTumbarse.getItemMeta();
										tumbarseMeta.setDisplayName("§fTumbarse");
										tumbarseMeta.setLore(new ArrayList<String>(Arrays.asList("Acceso rápido a la acción de tumbarse.")));
										tumbarseMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
										botonTumbarse.setItemMeta(tumbarseMeta);
										
										ItemStack botonCulo = new ItemStack(Material.GHAST_TEAR,1);
										ItemMeta culoMeta = botonCulo.getItemMeta();
										culoMeta.setDisplayName("§fEsconder objeto");
										culoMeta.setLore(new ArrayList<String>(Arrays.asList("Acceso rápido a la acción de esconder objeto.")));
										culoMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
										botonCulo.setItemMeta(culoMeta);
										
										ItemStack botonCaca = new ItemStack(Material.DIRT,1);
										ItemMeta cacaMeta = botonCaca.getItemMeta();
										cacaMeta.setDisplayName("§fDefecar");
										cacaMeta.setLore(new ArrayList<String>(Arrays.asList("Acceso rápido a la acción de defecar.")));
										cacaMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
										botonCaca.setItemMeta(cacaMeta);
										
										ItemStack botonInspect = new ItemStack(Material.STICK,1);
										ItemMeta inspectMeta = botonInspect.getItemMeta();
										inspectMeta.setDisplayName("§fInspeccionar");
										inspectMeta.setLore(new ArrayList<String>(Arrays.asList("Acceso rápido a la acción de inspeccionar personas.")));
										inspectMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
										botonInspect.setItemMeta(inspectMeta);
										
										ItemStack botonCasas = new ItemStack(Material.EMERALD_BLOCK,1);
										ItemMeta casasMeta = botonCasas.getItemMeta();
										casasMeta.setDisplayName("§fMis casas");
										casasMeta.setLore(new ArrayList<String>(Arrays.asList("Acceso rápido a la lista de tus casas.")));
										casasMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
										botonCasas.setItemMeta(casasMeta);
										
										ItemStack botonVolver = new ItemStack(Material.REDSTONE,1);
										ItemMeta volverMeta = botonVolver.getItemMeta();
										volverMeta.setDisplayName("§fVolver atrás");
										volverMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
										botonVolver.setItemMeta(volverMeta);
										
										replaceInv.setContents(items);
										
										replaceInv.setItem(12, botonSentarse);
										replaceInv.setItem(13, botonTumbarse);
										replaceInv.setItem(14, botonCulo);
										replaceInv.setItem(21, botonCaca);
										replaceInv.setItem(22, botonInspect);
										replaceInv.setItem(23, botonCasas);
										replaceInv.setItem(34, botonVolver);
										
										break;
										
									case "§fGuía Ingame":
										p.performCommand("guia");
										break;
									
									case "§fVolver atrás":
										ItemStack[] itemsReplace = baseInv.get(p);
										ItemStack[] playerItems = p.getInventory().getContents();
										
										for (int i = 9; i < 35; i++) {
											playerItems[i] = itemsReplace[i];
										}
										
										p.getInventory().setContents(playerItems);
										
										break;
									
									case "§fSentarse":
										p.performCommand("sit");
										break;
									
									case "§fTumbarse":
										p.performCommand("lay");
										break;
									
									case "§fEsconder objeto":
										p.performCommand("culo");
										break;
									
									case "§fDefecar":
										p.performCommand("caca");
										break;
										
									case "§fInspeccionar":
										p.performCommand("inspeccionar");
										p.closeInventory();
										break;
										
									case "§fMis casas":
										p.closeInventory();
										p.performCommand("miscasas");
										break;
									
									case "§fCaminar/Andar rápido":
										
										break;
									
									
									default:
										if (e.getCurrentItem().hasItemMeta()) {
											if (Bukkit.getPlayer(e.getCurrentItem().getItemMeta().getDisplayName()) != null) {
												p.performCommand("ficha ver " + e.getCurrentItem().getItemMeta().getDisplayName());
											}
										}
										break;
								}
							}
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
	
	@EventHandler
	public void invclose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		
		if (e.getInventory().getType() == InventoryType.PLAYER) {
			p.getInventory().setContents(baseInv.get(p));
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
