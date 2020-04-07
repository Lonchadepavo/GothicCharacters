package com.loncha.gothiccharacters;

import org.bukkit.Material;
import org.bukkit.block.Block;

public class EliminarMochila implements Runnable{
	Block b;
	
	public EliminarMochila(Block b) {
		this.b = b;
	}
	
	//Cambia el bloque a tipo aire (hace desaparecer la mochila)
	@Override
	public void run() {
		b.setType(Material.AIR);	
	}

}
