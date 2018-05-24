package fr.dariusmtn.caulcrafting.commands;

import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.dariusmtn.caulcrafting.CaulCrafting;
import fr.dariusmtn.caulcrafting.CraftArray;
import fr.dariusmtn.editor.Editor;

public class CreateCraftCommand {
	
	private CaulCrafting plugin;
    public CreateCraftCommand(CaulCrafting instance) {
          this.plugin = instance; 
    }
    
    private void dispEditorMessage(Player player, int nbTitleStrings, String... strings) {
    	boolean title = false;
 
    	for (String str : strings) {
    		if (nbTitleStrings > 0 && !title) {
        		//Title
    			player.sendMessage(" ");
    			player.sendMessage("§d§l➤ " + str);
    			title = true;
    		} else if (nbTitleStrings > 0)
    			player.sendMessage("§e" + str);
    		else
    			player.sendMessage(str.equals("---") ? "§f§l§m-----" : "§7" + str);
    		nbTitleStrings--;
    	}
    }
    
    private void initCreateEditor(Player player) {
    	//Listes éditeur
    	plugin.editors.put(player.getUniqueId(), new Editor());
		//Explications
		this.dispEditorMessage(player, 2, plugin.lang.getTranslation("craftmaking_step_1"),
				plugin.lang.getTranslation("craftmaking_step_1_explain"),
				"---", plugin.lang.getTranslation("craftmaking_editor_cmd_exit"),
				plugin.lang.getTranslation("craftmaking_editor_cmd_next"),
				"---");
    }
    
    @SuppressWarnings("deprecation")
	public boolean createCommand(Player player, String[] args) {
    	if (player == null)
    		return false;
    	if (!player.hasPermission("caulcrafting.admin.create")) {
    		player.sendMessage("§c" + plugin.lang.getTranslation("general_do_not_permission"));
    		return false;
    	}
    	if (plugin.editors.containsKey(player.getUniqueId())) {
    		//already in editor
	    	player.sendMessage("§c" + plugin.lang.getTranslation("craftmaking_already_in"));
			return false;
    	}
    	if (args.length == 3) {
    		//Creating craft from command arguments
    		CraftArray craftcmd = new CraftArray();
			//On converti en ItemStack
			boolean error = false;
			for(int i = 1; i <= 2; i++){
				ArrayList<String> part = new ArrayList<String>();
				if(i == 1){
					//Craft
					part = new ArrayList<String>(Arrays.asList(args[1].split(",")));
				} else if(i == 2){
					//Result
					part = new ArrayList<String>(Arrays.asList(args[2].split(",")));
				}
				for(String eitm : part){
					if(error == false){
						ItemStack itms = null;
						//Détection du nombre
						int amount = 1;
						if(eitm.contains("*")){
							try{
								amount = Integer.parseInt(eitm.substring(eitm.indexOf("*")+1, eitm.length()));
							} catch (Exception e){
								//...
							}
						}
						eitm = eitm.replace("*" + amount, "");
						//ID avec Data
						if(eitm.contains(":")){
							ArrayList<String> idastr = new ArrayList<String>(Arrays.asList(eitm.split(":")));
							try{
								itms = new ItemStack(Material.getMaterial(Integer.parseInt(idastr.get(0))),amount);
								itms.setDurability(Short.parseShort(idastr.get(1)));
							} catch (Exception e){
								try{
									itms = new ItemStack(Material.getMaterial(idastr.get(0).toUpperCase()),amount);
									itms.setDurability(Short.parseShort(idastr.get(1)));
								} catch (Exception ee){
									//...
								}
							}
						} else {
							try{
								if(Integer.parseInt(eitm) >= 0){
									itms = new ItemStack(Material.getMaterial(Integer.parseInt(eitm)),amount);
								}
							} catch (Exception e){
								try{
									itms = new ItemStack(Material.getMaterial(eitm.toUpperCase()),amount);
								} catch (Exception ee){
									player.sendMessage("§c" + plugin.lang.getTranslation("craftmaking_wrong") + eitm);
								}
							}
							
						}
						if(itms != null){
							if(i == 1){
								craftcmd.addCraftItem(itms);
							} else if(i == 2){
								craftcmd.addResultItem(itms);
							}
						} else {
							error = true;
						}
					}
				}
			}
			
			if(!craftcmd.isEmpty()){
				if(error == false){
					player.sendMessage("§d§l➤ " + plugin.lang.getTranslation("craftmaking_thats_right"));
					plugin.craftFormat.getCraftRecap(craftcmd, "§b" + plugin.lang.getTranslation("craftmaking_craft_typed"), false).send(player);
					player.sendMessage("§e" + plugin.lang.getTranslation("craftmaking_craft_confirm_cmd"));
					plugin.editors.put(player.getUniqueId(), new Editor());
					plugin.editors.get(player.getUniqueId()).setCraft(craftcmd);
					plugin.editors.get(player.getUniqueId()).setStep(3);
					return true;
				}
			}
			return false;
    	} else {
    		//Init editor
			this.initCreateEditor(player);
			return true;
    	}
    }
}
