package fr.dariusmtn.caulcrafting;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import mkremins.fanciful.FancyMessage;

public class Editor implements Listener{
	
	private CaulCrafting plugin;
    public Editor(CaulCrafting instance) {
          this.plugin = instance; 
    }
    
    public void addItem(Player player, ItemStack item, String mode) {
		//Affichage du nom
		String name = plugin.craftFormat.getName(item);
		player.sendMessage("§7" + plugin.lang.getTranslation("craftmaking_item_added") + "§a " + name);
		//Ajout définitif
		CraftArray globalcraft = plugin.craft.get(player);
		int nb = -1;
		if(mode == "craft") {
			globalcraft.addCraftItem(item);
		} else {
			globalcraft.addResultItem(item);
			nb = globalcraft.getResultItems().indexOf(item);
		}
		plugin.craft.put(player, globalcraft);
		//Recap
		plugin.craftFormat.getCraftRecap(globalcraft, "§e" + plugin.lang.getTranslation("craftmaking_craft_contents"), true).send(player);
		//Options
		FancyMessage options = new FancyMessage("§3" + plugin.lang.getTranslation("craftmaking_craft_options") + " ");
		//Probability
		if(nb > -1) {
			options.then("[" + plugin.lang.getTranslation("craftmaking_craft_options_dropchance") + "]")
			.color(ChatColor.GOLD).tooltip("§b" + plugin.lang.getTranslation("general_click_here")).suggest("/ccc setdropchance " + nb + " <0.01-100>").then(" ");
		}
		//Next Step
		options.then("[" + plugin.lang.getTranslation("craftmaking_next_step") + "]").color(ChatColor.GREEN).style(ChatColor.BOLD).tooltip("§b" + plugin.lang.getTranslation("general_click_here")).command("next");
		options.send(player);
		//Rappel commandes
		player.sendMessage("§7§l§m-----");
    }

}
