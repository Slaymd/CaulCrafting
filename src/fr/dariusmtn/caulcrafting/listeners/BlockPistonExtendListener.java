package fr.dariusmtn.caulcrafting.listeners;

import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;

import fr.dariusmtn.caulcrafting.CaulCrafting;

public class BlockPistonExtendListener implements Listener {
	
	private CaulCrafting plugin;
	public BlockPistonExtendListener(CaulCrafting plugin) {
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void onBlockPistonExtend(BlockPistonExtendEvent e) {
		//Check if there are cauldrons on activity on pushed blocks 
		for(Block block : e.getBlocks()) {
			Location loc = block.getLocation();
			for(UUID uuid : plugin.caulLoc.keySet()) {
				Location caul = plugin.caulLoc.get(uuid);
				if(caul.getBlock().getLocation().distance(loc) == 0) {
					e.setCancelled(true);
					return;
				}
			}
		}
	}
	
	
}
