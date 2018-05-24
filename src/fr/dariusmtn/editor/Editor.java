package fr.dariusmtn.editor;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.dariusmtn.caulcrafting.CraftArray;
import fr.dariusmtn.caulcrafting.CraftFormatting;

public class Editor{
	
	private CraftArray craft;
	private EditorMode mode;
	private int data;
	private int step;
	
	public Editor() {
		this.setCraft(new CraftArray());
		this.setStep(0);
		this.mode = EditorMode.NORMAL;
		this.data = 1;
	}
	
	public void addItem(Player player, ItemStack item) {
		
	}
	
    public void addItem(Player player, ItemStack item, String mode) {
		//Affichage du nom
    	String name = CraftFormatting.getName(item);
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

	/**
	 * @return the craft
	 */
	public CraftArray getCraft() {
		return craft;
	}

	/**
	 * @param craft the craft to set
	 */
	public void setCraft(CraftArray craft) {
		this.craft = craft;
	}

	/**
	 * @return the mode
	 */
	public EditorMode getMode() {
		return mode;
	}

	/**
	 * @param mode the mode to set
	 */
	public void setMode(EditorMode mode) {
		this.mode = mode;
	}

	/**
	 * @return the step
	 */
	public int getStep() {
		return step;
	}

	/**
	 * @param step the step to set
	 */
	public void setStep(int step) {
		this.step = step;
	}

	/**
	 * @return the data
	 */
	public int getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(int data) {
		this.data = data;
	}
    
//    public void addItem(Player player, ItemStack item, String mode) {
//		//Affichage du nom
//		String name = plugin.craftFormat.getName(item);
//		player.sendMessage("§7" + plugin.lang.getTranslation("craftmaking_item_added") + "§a " + name);
//		//Ajout définitif
//		CraftArray globalcraft = plugin.craft.get(player);
//		int nb = -1;
//		if(mode == "craft") {
//			globalcraft.addCraftItem(item);
//		} else {
//			globalcraft.addResultItem(item);
//			nb = globalcraft.getResultItems().indexOf(item);
//		}
//		plugin.craft.put(player, globalcraft);
//		//Recap
//		plugin.craftFormat.getCraftRecap(globalcraft, "§e" + plugin.lang.getTranslation("craftmaking_craft_contents"), true).send(player);
//		//Options
//		FancyMessage options = new FancyMessage("§3" + plugin.lang.getTranslation("craftmaking_craft_options") + " ");
//		//Probability
//		if(nb > -1) {
//			options.then("[" + plugin.lang.getTranslation("craftmaking_craft_options_dropchance") + "]")
//			.color(ChatColor.GOLD).tooltip("§b" + plugin.lang.getTranslation("general_click_here")).suggest("/ccc setdropchance " + nb + " <0.01-100>").then(" ");
//		}
//		//Next Step
//		options.then("[" + plugin.lang.getTranslation("craftmaking_next_step") + "]").color(ChatColor.GREEN).style(ChatColor.BOLD).tooltip("§b" + plugin.lang.getTranslation("general_click_here")).command("next");
//		options.send(player);
//		//Rappel commandes
//		player.sendMessage("§7§l§m-----");
//    }

}
