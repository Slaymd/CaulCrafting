package fr.dariusmtn.caulcrafting;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ConfigUpdate implements Listener {
    
	private CaulCrafting plugin;
    public ConfigUpdate(CaulCrafting instance) {
          this.plugin = instance; 
    }
	
	@SuppressWarnings("unchecked")
	public void update() {
		//Màj config V1
		if(!plugin.getConfig().isSet("config_version")) {
			plugin.getLogger().info("CaulCrafting config has changed : trying to convert...");
			//Getting whole crafts 
			ArrayList<HashMap<String,ArrayList<ItemStack>>> craftlist = new ArrayList<HashMap<String,ArrayList<ItemStack>>>();
			if(plugin.getConfig().isSet("Crafts"))
				craftlist = (ArrayList<HashMap<String, ArrayList<ItemStack>>>) plugin.getConfig().get("Crafts");
			//New file
			try {
				File craftfile = new File(plugin.getDataFolder(), "crafts.yml");
				craftfile.createNewFile();
				FileConfiguration craftconfig = YamlConfiguration.loadConfiguration(craftfile);
				craftconfig.set("Crafts", craftlist);
				craftconfig.save(craftfile);
				plugin.getLogger().info("CaulCrafting : Conversion succeed");
			} catch (IOException e) {
				plugin.getLogger().warning("CaulCrafting : Conversion failed");
				Bukkit.getPluginManager().disablePlugin(plugin);
			}
			//reset with the new config
			File configfile = new File(plugin.getDataFolder() + "/config.yml");
			configfile.delete();
			plugin.saveDefaultConfig();
			plugin.reloadConfig();
		}
		//Màj config V2 : craft storage
		if(plugin.getConfig().getInt("config_version") == 2) {
			plugin.getLogger().info("CaulCrafting config has changed : trying to convert...");
			try {
				//Getting craft.yml file
				File craftfile = new File(plugin.getDataFolder(), "crafts.yml");
				craftfile.createNewFile();
				FileConfiguration craftconfig = YamlConfiguration.loadConfiguration(craftfile);
				//Getting whole crafts 
				ArrayList<HashMap<String,ArrayList<ItemStack>>> craftlist = new ArrayList<HashMap<String,ArrayList<ItemStack>>>();
				if(craftconfig.isSet("Crafts"))
					craftlist = (ArrayList<HashMap<String, ArrayList<ItemStack>>>) craftconfig.get("Crafts");
				//remove old crafts
				craftfile.delete();
				//converting all craft
				for(HashMap<String, ArrayList<ItemStack>> craft : craftlist) {
					//Getting main array
					ArrayList<ItemStack> recipe = craft.get("craft");
					ArrayList<ItemStack> reward = craft.get("result");
					//Creating new Craft Object
					CraftArray convertedcraft = new CraftArray(recipe,reward);
					//saving the converted craft
					plugin.craftStorage.addCraft(convertedcraft);
				}
				plugin.getLogger().info("CaulCrafting : Conversion succeed");
				plugin.saveResource("config.yml", true);
			} catch (IOException e) {
				plugin.getLogger().warning("CaulCrafting : Conversion failed");
				Bukkit.getPluginManager().disablePlugin(plugin);
			}
			
		}
		//Màj config V3 : add fire option
		if(plugin.getConfig().getInt("config_version") == 3) {
			//reset config.yml
			plugin.saveResource("config.yml", true);
		}
		
	}

}
