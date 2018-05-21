package fr.dariusmtn.caulcrafting.commands;

import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.dariusmtn.caulcrafting.CaulCrafting;
import fr.dariusmtn.caulcrafting.CraftArray;
import mkremins.fanciful.FancyMessage;

public class CaulCraftingConfigCommandExecutor implements CommandExecutor{
	
	private CaulCrafting plugin;
	public CaulCraftingConfigCommandExecutor(CaulCrafting plugin) {
		this.plugin = plugin;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Config command (internal)
		if(cmd.getName().equalsIgnoreCase("caulcraftingconfig")) {
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("setlang")) {
					if(args.length == 2 && plugin.languagesAvailable.containsKey(args[1])) {
						String oldlang = plugin.lang.getExactLanguage();
						plugin.lang.setLanguage(args[1]);
						if(sender instanceof Player && oldlang.equalsIgnoreCase("default")) {
							Player player = (Player)sender;
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 0);
							player.sendMessage("§d§l➤ " + plugin.lang.getTranslation("welcome_lets_start_something"));
							new FancyMessage(" §e•§2§l " + plugin.lang.getTranslation("welcome_create_craft")).tooltip("§b" + plugin.lang.getTranslation("general_click_here")).command("/caulcrafting create").send(player);
							new FancyMessage(" §e•§2 " + plugin.lang.getTranslation("welcome_main_command")).tooltip("§b" + plugin.lang.getTranslation("general_click_here")).command("/caulcrafting").send(player);
						}
					}
					return false;
				}else if(args[0].equalsIgnoreCase("setdropchance")) {
					if(args.length == 3 && plugin.editor.containsKey(sender)) {
						if(Integer.valueOf(args[1]) != null) {
							int itemnb = Integer.valueOf(args[1]);
							CraftArray editcraft = plugin.craft.get(sender);
							if(editcraft.getResultItems().get(itemnb) != null) {
								if(Double.valueOf(args[2]) != null) {
									double prob = Double.valueOf(args[2]);
									editcraft.addResultItem(plugin.craft.get(sender).getResultItems().get(itemnb),prob);
									plugin.craftFormat.getCraftRecap(editcraft, "§e" + plugin.lang.getTranslation("craftmaking_craft_contents"), true).send(sender);
									sender.sendMessage("§7§l§m-----");
								}
							}
						}
					}
					return false;
				//Editor : delete item
				}else if(args[0].equalsIgnoreCase("delitem")) {
					if(args.length == 3) {
						if(plugin.editor.containsKey(sender)) {
							if(Integer.valueOf(args[1]) != null) {
								int itemnb = Integer.valueOf(args[1]);
								CraftArray editcraft = plugin.craft.get(sender);
								if(args[2].equalsIgnoreCase("craft")) {
									ItemStack itemtodel = editcraft.getCraft().get(itemnb);
									editcraft.removeCraftItem(itemtodel);
								} else if(args[2].equalsIgnoreCase("result")) {
									ItemStack itemtodel = editcraft.getResultItems().get(itemnb);
									editcraft.removeResultItem(itemtodel);
								}
								plugin.craftFormat.getCraftRecap(editcraft, "§e" + plugin.lang.getTranslation("craftmaking_craft_contents"), true).send(sender);
								sender.sendMessage("§7§l§m-----");
							}
						}
					}
				//Editor : add player cmd
				}else if(args[0].equalsIgnoreCase("addplayercmd") || args[0].equalsIgnoreCase("addconsolecmd")) {
					if(plugin.editor.containsKey(sender)) {
						String mode = (args[0].equalsIgnoreCase("addplayercmd") ? "plcmd" : "opcmd");
						//Getting cmd by args
						String cmdsen = "";
						for(String arg : args) {
							cmdsen += arg + " ";
						}
						cmdsen = cmdsen.replace(args[0] + " ", "");
						if(!plugin.craft.get(sender).getCmds().contains("opcmd" + cmdsen) && !plugin.craft.get(sender).getCmds().contains("plcmd" + cmdsen)) {
							//Adding command
							sender.sendMessage("§7" + plugin.lang.getTranslation("craftmaking_cmd_added") + "§a " + cmdsen);
							plugin.craft.get(sender).addCmd(mode + cmdsen);
							//Delete option
							new FancyMessage("§3" + plugin.lang.getTranslation("craftmaking_craft_options") + " ").then("[" + plugin.lang.getTranslation("craftmaking_cmd_delete") + "]")
							.color(ChatColor.RED).tooltip("§b" + plugin.lang.getTranslation("general_click_here")).suggest("/ccc " + (mode == "plcmd" ? "delplayercmd" : "delconsolecmd") + " " + cmdsen).send(sender);
							//Craft recap
							plugin.craftFormat.getCraftRecap(plugin.craft.get(sender), "§e" + plugin.lang.getTranslation("craftmaking_craft_contents"), true).send(sender);
							sender.sendMessage("§7§l§m-----");
						}
					}
					return false;
				//Editor : delete player cmd
				}else if(args[0].equalsIgnoreCase("delplayercmd") || args[0].equalsIgnoreCase("delconsolecmd")) {
					if(plugin.editor.containsKey(sender)) {
						String mode = (args[0].equalsIgnoreCase("delplayercmd") ? "plcmd" : "opcmd");
						//Getting cmd by args
						String cmdsen = "";
						for(String arg : args) {
							cmdsen += arg + " ";
						}
						cmdsen = cmdsen.replace(args[0] + " ", "");
						if(plugin.craft.get(sender).getCmds().contains(mode + cmdsen)) {
							//Removing command
							sender.sendMessage("§7" + plugin.lang.getTranslation("craftmaking_cmd_deleted") + "§c§m " + cmdsen);
							plugin.craft.get(sender).removeCmd(mode + cmdsen);
							//Craft recap
							plugin.craftFormat.getCraftRecap(plugin.craft.get(sender), "§e" + plugin.lang.getTranslation("craftmaking_craft_contents"), true).send(sender);
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
