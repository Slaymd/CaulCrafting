package fr.dariusmtn.caulcrafting;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import mkremins.fanciful.FancyMessage;


public class CraftFormatting implements Listener {
    
    public static FancyMessage getCraftRecap(CraftArray globalcraft, String headtext, boolean editor){
		ArrayList<ItemStack> need = globalcraft.getCraft();
		HashMap<ItemStack,Integer> result = globalcraft.getResult();
		ArrayList<String> cmds = globalcraft.getCmds();
		//Elements requis
		FancyMessage formcraft = new FancyMessage(headtext + "§r" + (headtext == "" ? "" : " "));
		boolean first = true;
		int count = 0;
		if(!need.isEmpty()){
			for(ItemStack item : need){
				String name = getName(item);
				formcraft.then((first ? "" : ", ") + name);
				if(item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
					formcraft.then(" ").then("ℇ").color(ChatColor.LIGHT_PURPLE).style(ChatColor.BOLD).tooltip("§b" + Language.getTranslation("general_enchanted"));
				}
				if(editor == true)
					formcraft.then(" ").then("✕").color(ChatColor.RED).tooltip("§b" + Language.getTranslation("craftmaking_delete_item")).command("/caulcraftingconfig delitem " + count + " craft");
				formcraft.then("§r");
				first = false;
				count++;
			}
		} else {
			formcraft.then(Language.getTranslation("general_undefined")).color(ChatColor.GRAY).style(ChatColor.ITALIC);
		}
		first = true;
		count = 0;
		formcraft.then(" ").then("--").color(ChatColor.YELLOW).style(ChatColor.BOLD).style(ChatColor.STRIKETHROUGH).then(">").color(ChatColor.YELLOW).style(ChatColor.BOLD).then(" §r");
		//Elements donnés
		if(!result.isEmpty()){
			for(ItemStack item : result.keySet()){
				String name = getName(item);
				formcraft.then((first ? "" : ", ") + name);
				if(item.hasItemMeta() && item.getItemMeta().hasEnchants()) {
					formcraft.then(" ").then("ℇ").color(ChatColor.LIGHT_PURPLE).style(ChatColor.BOLD).tooltip("§b" + Language.getTranslation("general_enchanted"));
				}
				double luck = result.get(item)/10;
				if(luck < 100) {
					String luckStr = (String.valueOf(luck) + " ").replace(".0 ", "").replace(" ", "");
					formcraft.then(" ").then("☘").color(ChatColor.YELLOW).tooltip("§e" + Language.getTranslation("general_drop_chance").replace("%chance%", "§6§l" + luckStr));
				}
				if(editor == true)
					formcraft.then(" ").then("✕").color(ChatColor.RED).tooltip("§b" + Language.getTranslation("craftmaking_delete_item")).command("/caulcraftingconfig delitem " + count + " result");
				formcraft.then("§r");
				first = false;
				count++;
			}
		} else {
			first = true;
			if(cmds.isEmpty())
				formcraft.then(Language.getTranslation("general_undefined")).color(ChatColor.GRAY).style(ChatColor.ITALIC);
		}
		count = 0;
		if(!cmds.isEmpty()) {
			for(String cmd : cmds){
				String[] cmdsplit = cmd.split(" ");
				formcraft.then((first ? "" : ", ") + cmdsplit[0].replace("plcmd", "").replace("opcmd", ""));
			}
		}
		//Recap
		return formcraft;
	}
	
	public static String getName(ItemStack stack) {
		if(CaulCrafting.nmsItemsName == true){
			return CaulCrafting.getItemsname().getItemStackName(stack);
		} else {
			String name = "";
			name = stack.getType().toString();
			int amt = stack.getAmount();
			if(amt > 1){
				name += " §3x " + amt;
			}
			return name;
		}
	}

}
