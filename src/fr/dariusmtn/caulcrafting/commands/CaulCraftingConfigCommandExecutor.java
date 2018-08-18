package fr.dariusmtn.caulcrafting.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.dariusmtn.caulcrafting.CaulCrafting;
import fr.dariusmtn.caulcrafting.CraftArray;
import fr.dariusmtn.caulcrafting.CraftFormatting;
import fr.dariusmtn.caulcrafting.Language;
import fr.dariusmtn.caulcrafting.CrossVersionSounds;
import fr.dariusmtn.editor.PlayerEditor;
import mkremins.fanciful.FancyMessage;

public class CaulCraftingConfigCommandExecutor implements CommandExecutor{
	
	private CaulCrafting plugin;
	public CaulCraftingConfigCommandExecutor(CaulCrafting plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player;
		
		if (!(sender instanceof Player))
			return false;
		player = (Player)sender;
		//Config command (internal)
		if(cmd.getName().equalsIgnoreCase("caulcraftingconfig")) {
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("setlang")) {
					if(args.length == 2 && plugin.languagesAvailable.containsKey(args[1])) {
						String oldlang = Language.getExactLanguage();
						Language.setLanguage(args[1]);
						if(oldlang.equalsIgnoreCase("default")) {
							player.playSound(player.getLocation(), CrossVersionSounds.NOTE_PLING.bukkitSound(), 1, 0);
							player.sendMessage("§d§l➤ " + Language.getTranslation("welcome_lets_start_something"));
							new FancyMessage(" §e•§2§l " + Language.getTranslation("welcome_create_craft")).tooltip("§b" + Language.getTranslation("general_click_here")).command("/caulcrafting create").send(player);
							new FancyMessage(" §e•§2 " + Language.getTranslation("welcome_main_command")).tooltip("§b" + Language.getTranslation("general_click_here")).command("/caulcrafting").send(player);
						}
					}
					return false;
				}else if(args[0].equalsIgnoreCase("setdropchance")) {
					if(args.length == 3 && PlayerEditor.isInEditor(player)) {
						if(Integer.valueOf(args[1]) != null) {
							int itemnb = Integer.valueOf(args[1]);
							CraftArray editcraft = CaulCrafting.editors.get(player.getUniqueId()).getCraft();
							if(editcraft.getResultItems().get(itemnb) != null) {
								if(Double.valueOf(args[2]) != null) {
									double prob = Double.valueOf(args[2]);
									editcraft.addResultItem(editcraft.getResultItems().get(itemnb),prob);
									CraftFormatting.getCraftRecap(editcraft, "§e" + Language.getTranslation("craftmaking_craft_contents"), true).send(sender);
									sender.sendMessage("§7§l§m-----");
								}
							}
						}
					}
					return false;
				//Editor : delete item
				}else if(args[0].equalsIgnoreCase("delitem")) {
					if(args.length == 3) {
						if(PlayerEditor.isInEditor(player)) {
							if(Integer.valueOf(args[1]) != null) {
								int itemnb = Integer.valueOf(args[1]);
								CraftArray editcraft = PlayerEditor.getEditorCraft(player);
								if(args[2].equalsIgnoreCase("craft")) {
									ItemStack itemtodel = editcraft.getCraft().get(itemnb);
									editcraft.removeCraftItem(itemtodel);
								} else if(args[2].equalsIgnoreCase("result")) {
									ItemStack itemtodel = editcraft.getResultItems().get(itemnb);
									editcraft.removeResultItem(itemtodel);
								}
								CraftFormatting.getCraftRecap(editcraft, "§e" + Language.getTranslation("craftmaking_craft_contents"), true).send(sender);
								sender.sendMessage("§7§l§m-----");
							}
						}
					}
				//Editor : add player cmd
				}else if(args[0].equalsIgnoreCase("addplayercmd") || args[0].equalsIgnoreCase("addconsolecmd")) {
					if(PlayerEditor.isInEditor(player)) {
						String mode = (args[0].equalsIgnoreCase("addplayercmd") ? "plcmd" : "opcmd");
						CraftArray editcraft = PlayerEditor.getEditorCraft(player);
						//Getting cmd by args
						String cmdsen = "";
						for(String arg : args) {
							cmdsen += arg + " ";
						}
						cmdsen = cmdsen.replace(args[0] + " ", "");
						if(!editcraft.getCmds().contains("opcmd" + cmdsen) && !editcraft.getCmds().contains("plcmd" + cmdsen)) {
							//Adding command
							sender.sendMessage("§7" + Language.getTranslation("craftmaking_cmd_added") + "§a " + cmdsen);
							editcraft.addCmd(mode + cmdsen);
							//Delete option
							new FancyMessage("§3" + Language.getTranslation("craftmaking_craft_options") + " ").then("[" + Language.getTranslation("craftmaking_cmd_delete") + "]")
							.color(ChatColor.RED).tooltip("§b" + Language.getTranslation("general_click_here")).suggest("/ccc " + (mode == "plcmd" ? "delplayercmd" : "delconsolecmd") + " " + cmdsen).send(sender);
							//Craft recap
							CraftFormatting.getCraftRecap(editcraft, "§e" + Language.getTranslation("craftmaking_craft_contents"), true).send(sender);
							sender.sendMessage("§7§l§m-----");
						}
					}
					return false;
				//Editor : delete player cmd
				}else if(args[0].equalsIgnoreCase("delplayercmd") || args[0].equalsIgnoreCase("delconsolecmd")) {
					if(PlayerEditor.isInEditor(player)) {
						String mode = (args[0].equalsIgnoreCase("delplayercmd") ? "plcmd" : "opcmd");
						CraftArray editcraft = PlayerEditor.getEditorCraft(player);
						//Getting cmd by args
						String cmdsen = "";
						for(String arg : args) {
							cmdsen += arg + " ";
						}
						cmdsen = cmdsen.replace(args[0] + " ", "");
						if(editcraft.getCmds().contains(mode + cmdsen)) {
							//Removing command
							sender.sendMessage("§7" + Language.getTranslation("craftmaking_cmd_deleted") + "§c§m " + cmdsen);
							editcraft.removeCmd(mode + cmdsen);
							//Craft recap
							CraftFormatting.getCraftRecap(editcraft, "§e" + Language.getTranslation("craftmaking_craft_contents"), true).send(sender);
							sender.sendMessage("§7§l§m-----");
						}
					}
					return false;
				}
				return false;
			}
			if(sender instanceof Player) {
				((Player)sender).performCommand("caulcrafting");
			}
			return false;
		}
		return false;
	}

}
