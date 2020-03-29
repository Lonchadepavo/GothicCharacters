package com.loncha.gothiccharacters;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagFloat;
import net.minecraft.server.v1_12_R1.NBTTagInt;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;

public class RopaAleatoria implements Listener{
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		
		if (!p.hasPlayedBefore()) {			
			ArrayList<String> listaItems = getGear();
			ArrayList<String> listaTipos = new ArrayList<String>(Arrays.asList("CHAINMAIL_HELMET","CHAINMAIL_CHESTPLATE","CHAINMAIL_LEGGINGS","CHAINMAIL_BOOTS"));
			ArrayList<String> listaTipos2 = new ArrayList<String>(Arrays.asList("CHAINMAIL_CHESTPLATE","CHAINMAIL_LEGGINGS","CHAINMAIL_BOOTS"));
	
			for (int i = 0; i < listaItems.size(); i++) {
				ItemStack item;
				ArrayList<String> listaFinal = new ArrayList<String>(Arrays.asList("CHAINMAIL_HELMET","CHAINMAIL_CHESTPLATE","CHAINMAIL_LEGGINGS","CHAINMAIL_BOOTS"));;
				
				if (listaItems.size() == 4) {
					listaFinal = listaTipos;
				} else if (listaItems.size() == 3) {
					listaFinal = listaTipos2;
				}
				
				item = new ItemStack(Material.getMaterial(listaFinal.get(i)));
				
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(listaItems.get(i));
				item.setItemMeta(meta);
				
				net.minecraft.server.v1_12_R1.ItemStack nmsStack = CraftItemStack.asNMSCopy(item);
				NBTTagCompound compound = (nmsStack.hasTag()) ? nmsStack.getTag() : new NBTTagCompound();
				NBTTagList modifiers = new NBTTagList();
				
				NBTTagCompound attribute = new NBTTagCompound();
				
				attribute.set("AttributeName", new NBTTagString("generic.armor"));
				attribute.set("Name", new NBTTagString("generic.armor"));
				attribute.set("Amount", new NBTTagFloat(0));
				attribute.set("Operation", new NBTTagInt(0));
				attribute.set("UUIDLeast", new NBTTagInt(894654));
				attribute.set("UUIDMost", new NBTTagInt(2872));
				
				modifiers.add(attribute);
				
				compound.set("AttributeModifiers", modifiers);
				
				nmsStack.setTag(compound);
				
				item = CraftItemStack.asBukkitCopy(nmsStack);
				p.getInventory().addItem(item);
			}
		}
		
	}

	public ArrayList<String> getGear() {
		
		int needsArmor = (int) (Math.random()*100)+0, needsHelmet = (int) (Math.random()*100)+0;

		ArrayList<String> helmets = new ArrayList<String>(Arrays.asList("§fCapucha de cuero", "§fCapucha de cuero oscuro"));
		ArrayList<String> armors = new ArrayList<String>(Arrays.asList("§fAbrigo de cuero", "§fAbrigo de cuero oscuro"));
		ArrayList<String> pants = new ArrayList<String>(Arrays.asList("§fPantalones de trabajo", "§fPantalones de cuero", "§fPantalones negros", "§fPantalones marrones", "§fPantalones rojos", "§fPantalones amarillos", "§fTunica roja"));
		ArrayList<String> shoes = new ArrayList<String>(Arrays.asList("§fZapatos de cuero", "§fBotas de cuero oscuro", "§fBotas de cuero negro", "§fBotas altas de cuero"));
		
		ArrayList<String> equipement = new ArrayList<String>();
		
		int szHelmets = helmets.size(), szArmors = armors.size(), szPants = pants.size(), szShoes = shoes.size(), temp;
		
		if(needsArmor > 15) {
			if(needsHelmet > 70) {
				for(int i=1;i<=4;i++) {
					switch(i) {
						case 1: //Añade casco
							temp = (int) (Math.random()*szHelmets)+1;
							equipement.add(i-1, helmets.get(temp-1));
							break;
							
						case 2: //Añade peto
							temp = (int) (Math.random()*szArmors)+1;
							equipement.add(i-1, armors.get(temp-1));
							break;
							
						case 3: //Añade pantalones
							temp = (int) (Math.random()*szPants)+1;
							equipement.add(i-1, pants.get(temp-1));
							break;
							
						case 4: //Añade zapatillas
							temp = (int) (Math.random()*szShoes)+1;
							equipement.add(i-1, shoes.get(temp-1));
							break;
					}
				}
				
				return equipement;
			} else {
				for(int i=1;i<=3;i++) {
					switch(i) {
						case 1: //Añade peto
							temp = (int) (Math.random()*szArmors)+1;
							equipement.add(i-1, armors.get(temp-1));
							break;
							
						case 2: //Añade pantalones
							temp = (int) (Math.random()*szPants)+1;
							equipement.add(i-1, pants.get(temp-1));
							break;
							
						case 3: //Añade zapatillas
							temp = (int) (Math.random()*szShoes)+1;
							equipement.add(i-1, shoes.get(temp-1));
							break;
					}
				}
				
				return equipement;
			}
		} else {
			return equipement;
		}
	}

}