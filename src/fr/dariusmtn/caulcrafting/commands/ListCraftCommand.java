package fr.dariusmtn.caulcrafting.commands;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import fr.dariusmtn.caulcrafting.CraftArray;
import fr.dariusmtn.caulcrafting.CraftFormatting;
import fr.dariusmtn.caulcrafting.Language;
import mkremins.fanciful.FancyMessage;

public class ListCraftCommand {
	
	public static boolean craftListCommand(Player player, String[] args, ArrayList<CraftArray> craftlist) {
		if (!player.hasPermission("caulcrafting.admin.list")) {
			player.sendMessage("§c" + Language.getTranslation("general_do_not_permission"));
			return false;
		}
		//Intervalle d'action (pagination)
		int page = 0;
		if(args.length > 1){
			String p = args[1];
			try{
				page = Integer.parseInt(p);
				if(page < 0)
					page = 0;
			}catch (Exception e){
				player.sendMessage("§c" + Language.getTranslation("craftlist_no_display"));
				return true;
			}
		}
		int mincraft = 10*page;
		int maxcraft = mincraft + 10;
		//On affiche la liste
		int count = 0;
		if(craftlist.size()-1 >= mincraft){
			player.sendMessage("§d§l§m-----§b(" + Language.getTranslation("craftlist_page_nb").replace("%number%", "" + page) + ")");
			for(CraftArray crafts : craftlist){
				if(count >= mincraft && count <= maxcraft){
					FancyMessage craftrecap = CraftFormatting.getCraftRecap(crafts, "§6§l•", false);
					//Craft removing cross
					if(player.hasPermission("caulcrafting.admin.remove")) {
						craftrecap.then(" ").then("[✕]").color(ChatColor.RED).tooltip("§c" + Language.getTranslation("craftlist_delete_craft")).command("/caulcrafting remove " + count);
					}
					craftrecap.send(player);
				}
				count++;
			}
			if(maxcraft < craftlist.size()-1){
				player.sendMessage("§d§l§m-----§b(" + Language.getTranslation("craftlist_next_page") + " §3/... list " + (page+1) + "§b)");
			} else {
				player.sendMessage("§d§l§m-----§b(" + Language.getTranslation("craftlist_list_finished") + ")");
			}
		} else {
			player.sendMessage("§c" + Language.getTranslation("craftlist_no_display"));
		}
		return true;
	}
	
}
