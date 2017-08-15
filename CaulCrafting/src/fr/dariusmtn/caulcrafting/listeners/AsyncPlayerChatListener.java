package fr.dariusmtn.caulcrafting.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import fr.dariusmtn.caulcrafting.CaulCrafting;
import fr.dariusmtn.caulcrafting.CraftArray;
import mkremins.fanciful.FancyMessage;

public class AsyncPlayerChatListener implements Listener {

	private CaulCrafting plugin;
	public AsyncPlayerChatListener(CaulCrafting plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e){
		Player player = e.getPlayer();
		String msg = e.getMessage();
		//S'il est dans l'éditeur
		if(plugin.editor.containsKey(player)){
			e.setCancelled(true);
			int editorstep = plugin.editor.get(player);
			if(editorstep < 3){
				//Getting craft
				CraftArray globalcraft = plugin.craft.get(player);
				//Setting mode
				String mode = "craft";
				if(editorstep == 2)
					mode = "result";
				//Leave editor
				if(msg.equalsIgnoreCase("exit")){
					plugin.editor.remove(player);
					player.sendMessage("§c" + plugin.lang.getTranslation("craftmaking_editor_left"));
				}
				//Adding command
				else if(msg.startsWith("cmd ")) {
					if(mode == "result") {
						String cmd = msg.replace("cmd ", "");
						if(!cmd.startsWith("/"))
							cmd = "/" + cmd;
						new FancyMessage("§e" + plugin.lang.getTranslation("craftmaking_cmd_whosend") + " ").then("[" + plugin.lang.getTranslation("craftmaking_cmd_console") + "]")
						.color(ChatColor.GOLD).tooltip("§b" + plugin.lang.getTranslation("general_click_here")).command("/ccc addconsolecmd " + cmd).then(" ")
						.then("[" + plugin.lang.getTranslation("craftmaking_cmd_player") + "]")
						.color(ChatColor.GOLD).tooltip("§b" + plugin.lang.getTranslation("general_click_here")).command("/ccc addplayercmd " + cmd).send(player);
						player.sendMessage("§7§l§m-----");
					}
				}
				//Adding to craft
				else if(msg.equalsIgnoreCase("add")){
					ItemStack item = player.getInventory().getItemInMainHand();
					if(item != null){
						//Add item
						plugin.editorUtils.addItem(player, item, mode);
					}
				}
				//passer à l'étape suivante
				else if(msg.equalsIgnoreCase("next")){
					if(mode == "craft"){ //Passage à l'éditeur de résultat
						if(!globalcraft.getCraft().isEmpty()){
							player.sendMessage(" ");
							player.sendMessage("§d§l➤ " + plugin.lang.getTranslation("craftmaking_step_2"));
							player.sendMessage("§e" + plugin.lang.getTranslation("craftmaking_step_2_explain"));
							player.sendMessage("§f§l§m-----");
							player.sendMessage("§b" + plugin.lang.getTranslation("craftmaking_editor_cmd_cmd"));
							player.sendMessage("§7" + plugin.lang.getTranslation("craftmaking_editor_cmd_exit"));
							player.sendMessage("§e" + plugin.lang.getTranslation("craftmaking_editor_cmd_next"));
							player.sendMessage("§d§l§m-----");
							plugin.editor.put(player, 2);
						} else {
							player.sendMessage("§c" + plugin.lang.getTranslation("craftmaking_step_1_add_items"));
						}
					} else if(mode == "result"){ //Enregistrement
						if(!globalcraft.getResult().isEmpty() || !globalcraft.getCmds().isEmpty()){
							player.sendMessage(" ");
							player.sendMessage("§a§l➤ " + plugin.lang.getTranslation("craftmaking_step_final"));
							plugin.craftFormat.getCraftRecap(globalcraft, "§e" + plugin.lang.getTranslation("craftmaking_craft_created"), false).send(player);
							plugin.craftStorage.addCraft(globalcraft);
							plugin.craft.remove(player);
							plugin.editor.remove(player);
							plugin.reloadConfig();
						} else {
							player.sendMessage("§c" + plugin.lang.getTranslation("craftmaking_step_2_add_items"));
						}
					}
				} else {
					player.sendMessage("§7§l§m-----");
					player.sendMessage("§b" + plugin.lang.getTranslation("craftmaking_editor_cmd_add"));
					if(mode == "result")
						player.sendMessage("§b" + plugin.lang.getTranslation("craftmaking_editor_cmd_cmd"));
					player.sendMessage("§7" + plugin.lang.getTranslation("craftmaking_editor_cmd_exit"));
					player.sendMessage("§e" + plugin.lang.getTranslation("craftmaking_editor_cmd_next"));
					player.sendMessage("§7§l§m-----");
				}
			} else {
				if(editorstep == 3){
					//Craft entré via commande, demande de confirmation
					if(msg.equalsIgnoreCase("yes")){
						player.sendMessage("§a§l➤ " + plugin.lang.getTranslation("craftmaking_step_final"));
						plugin.craftFormat.getCraftRecap(plugin.craft.get(player), "§e" + plugin.lang.getTranslation("craftmaking_craft_created"), false).send(player);
						plugin.craftStorage.addCraft(plugin.craft.get(player));
						plugin.craft.remove(player);
						plugin.editor.remove(player);
						plugin.reloadConfig();
					} else if(msg.equalsIgnoreCase("no")){
						plugin.craft.remove(player);
						plugin.editor.remove(player);
						player.sendMessage("§c" + plugin.lang.getTranslation("craftmaking_canceled"));
					}
				}
			}
		}
		
	}
	
	
}
