package fr.dariusmtn.editor;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.dariusmtn.caulcrafting.CraftArray;
import fr.dariusmtn.caulcrafting.CraftFormatting;
import fr.dariusmtn.caulcrafting.Language;
import mkremins.fanciful.FancyMessage;

public class Editor{
	
	private CraftArray craft;
	private EditorMode mode;
	private int data;
	private int step;
	
	public Editor() {
		this.setCraft(new CraftArray());
		this.setStep(1);
		this.mode = EditorMode.NORMAL;
		this.data = 1;
	}
	
	public void addItem(Player player, ItemStack item) {
		String itemName = CraftFormatting.getName(item);
		Integer itemIndex = 0;
		
		player.sendMessage("§7" + Language.getTranslation("craftmaking_item_added") + "§a " + itemName);
		FancyMessage options = new FancyMessage("§3" + Language.getTranslation("craftmaking_craft_options") + " ");
		if (this.step == 1) {
			//Adding to recipe
			this.craft.addCraftItem(item);
		} else if (this.step == 2) {
			//Adding to craft result
			this.craft.addResultItem(item);
			itemIndex = this.craft.getResultItems().indexOf(item);
			//Drop chance button
			options.then("[" + Language.getTranslation("craftmaking_craft_options_dropchance") + "]")
			.color(ChatColor.GOLD).tooltip("§b" + Language.getTranslation("general_click_here")).suggest("/ccc setdropchance " + itemIndex + " <0.01-100>").then(" ");
			
		}
		//Next step button
		options.then("[" + Language.getTranslation("craftmaking_next_step") + "]").color(ChatColor.GREEN).style(ChatColor.BOLD).tooltip("§b" + Language.getTranslation("general_click_here")).command("next");
		//Craft recap
		CraftFormatting.getCraftRecap(this.craft, "§e" + Language.getTranslation("craftmaking_craft_contents"), true).send(player);
		//Sending option buttons
		options.then("[" + Language.getTranslation("craftmaking_next_step") + "]").color(ChatColor.GREEN).style(ChatColor.BOLD).tooltip("§b" + Language.getTranslation("general_click_here")).command("next");
		options.send(player);
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
//		player.sendMessage("§7" + Language.getTranslation("craftmaking_item_added") + "§a " + name);
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
//		plugin.craftFormat.getCraftRecap(globalcraft, "§e" + Language.getTranslation("craftmaking_craft_contents"), true).send(player);
//		//Options
//		FancyMessage options = new FancyMessage("§3" + Language.getTranslation("craftmaking_craft_options") + " ");
//		//Probability
//		if(nb > -1) {
//			options.then("[" + Language.getTranslation("craftmaking_craft_options_dropchance") + "]")
//			.color(ChatColor.GOLD).tooltip("§b" + Language.getTranslation("general_click_here")).suggest("/ccc setdropchance " + nb + " <0.01-100>").then(" ");
//		}
//		//Next Step
//		options.then("[" + Language.getTranslation("craftmaking_next_step") + "]").color(ChatColor.GREEN).style(ChatColor.BOLD).tooltip("§b" + Language.getTranslation("general_click_here")).command("next");
//		options.send(player);
//		//Rappel commandes
//		player.sendMessage("§7§l§m-----");
//    }

}
