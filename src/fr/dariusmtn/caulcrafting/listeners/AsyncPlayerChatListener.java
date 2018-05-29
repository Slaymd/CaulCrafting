package fr.dariusmtn.caulcrafting.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import fr.dariusmtn.caulcrafting.CaulCrafting;
import fr.dariusmtn.caulcrafting.CraftArray;
import fr.dariusmtn.caulcrafting.CraftFormatting;
import fr.dariusmtn.caulcrafting.Language;
import fr.dariusmtn.editor.Editor;
import fr.dariusmtn.editor.PlayerEditor;
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
		Editor editor;
		
		//S'il est dans l'éditeur
		if (PlayerEditor.isInEditor(player)) {
			editor = PlayerEditor.getEditor(player);
			e.setCancelled(true);
			int editorstep = editor.getStep();
			if(editorstep < 3){
				//Getting craft
				CraftArray globalcraft = editor.getCraft();
				//Setting mode
				String mode = "craft";
				if(editorstep == 2)
					mode = "result";
				//Leave editor
				if(msg.equalsIgnoreCase("exit")){
					PlayerEditor.exitEditor(player);
					player.sendMessage("§c" + Language.getTranslation("craftmaking_editor_left"));
				}
				//Adding command
				else if(msg.startsWith("cmd ")) {
					if(mode == "result") {
						String cmd = msg.replace("cmd ", "");
						if(!cmd.startsWith("/"))
							cmd = "/" + cmd;
						new FancyMessage("§e" + Language.getTranslation("craftmaking_cmd_whosend") + " ").then("[" + Language.getTranslation("craftmaking_cmd_console") + "]")
						.color(ChatColor.GOLD).tooltip("§b" + Language.getTranslation("general_click_here")).command("/ccc addconsolecmd " + cmd).then(" ")
						.then("[" + Language.getTranslation("craftmaking_cmd_player") + "]")
						.color(ChatColor.GOLD).tooltip("§b" + Language.getTranslation("general_click_here")).command("/ccc addplayercmd " + cmd).send(player);
						player.sendMessage("§7§l§m-----");
					}
				}
				//Adding to craft
				else if(msg.equalsIgnoreCase("add")){
					ItemStack item = player.getInventory().getItemInMainHand();
					if(item != null){
						//Add item
						editor.addItem(player, item);
					}
				}
				//passer à l'étape suivante
				else if(msg.equalsIgnoreCase("next")){
					if(mode == "craft"){ //Passage à l'éditeur de résultat
						if(!globalcraft.getCraft().isEmpty()){
							player.sendMessage(" ");
							player.sendMessage("§d§l➤ " + Language.getTranslation("craftmaking_step_2"));
							player.sendMessage("§e" + Language.getTranslation("craftmaking_step_2_explain"));
							player.sendMessage("§f§l§m-----");
							player.sendMessage("§b" + Language.getTranslation("craftmaking_editor_cmd_cmd"));
							player.sendMessage("§7" + Language.getTranslation("craftmaking_editor_cmd_exit"));
							player.sendMessage("§e" + Language.getTranslation("craftmaking_editor_cmd_next"));
							player.sendMessage("§d§l§m-----");
							editor.setStep(2);
						} else {
							player.sendMessage("§c" + Language.getTranslation("craftmaking_step_1_add_items"));
						}
					} else if(mode == "result"){ //Enregistrement
						if(!globalcraft.getResult().isEmpty() || !globalcraft.getCmds().isEmpty()){
							player.sendMessage(" ");
							player.sendMessage("§a§l➤ " + Language.getTranslation("craftmaking_step_final"));
							CraftFormatting.getCraftRecap(globalcraft, "§e" + Language.getTranslation("craftmaking_craft_created"), false).send(player);
							plugin.craftStorage.addCraft(globalcraft);
							PlayerEditor.exitEditor(player);
							plugin.reloadConfig();
						} else {
							player.sendMessage("§c" + Language.getTranslation("craftmaking_step_2_add_items"));
						}
					}
				} else {
					player.sendMessage("§7§l§m-----");
					player.sendMessage("§b" + Language.getTranslation("craftmaking_editor_cmd_add"));
					if(mode == "result")
						player.sendMessage("§b" + Language.getTranslation("craftmaking_editor_cmd_cmd"));
					player.sendMessage("§7" + Language.getTranslation("craftmaking_editor_cmd_exit"));
					player.sendMessage("§e" + Language.getTranslation("craftmaking_editor_cmd_next"));
					player.sendMessage("§7§l§m-----");
				}
			} else {
				if(editorstep == 3){
					//Craft entré via commande, demande de confirmation
					if(msg.equalsIgnoreCase("yes")){
						player.sendMessage("§a§l➤ " + Language.getTranslation("craftmaking_step_final"));
						CraftFormatting.getCraftRecap(editor.getCraft(), "§e" + Language.getTranslation("craftmaking_craft_created"), false).send(player);
						plugin.craftStorage.addCraft(editor.getCraft());
						PlayerEditor.exitEditor(player);
						plugin.reloadConfig();
					} else if(msg.equalsIgnoreCase("no")){
						PlayerEditor.exitEditor(player);
						player.sendMessage("§c" + Language.getTranslation("craftmaking_canceled"));
					}
				}
			}
		}
		
	}
	
	
}
