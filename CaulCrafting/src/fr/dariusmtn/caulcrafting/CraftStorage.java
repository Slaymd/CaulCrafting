package fr.dariusmtn.caulcrafting;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class CraftStorage implements Listener {
    
	private CaulCrafting plugin;
    public CraftStorage(CaulCrafting instance) {
          this.plugin = instance; 
    }
    
    @SuppressWarnings("unchecked")
	public ArrayList<CraftArray> getCrafts(){
    	ArrayList<CraftArray> craftlist = new ArrayList<CraftArray>();
    	try {
	    	File craftfile = new File(plugin.getDataFolder(), "crafts.yml");
			craftfile.createNewFile();
			FileConfiguration craftconfig = YamlConfiguration.loadConfiguration(craftfile);
			if(craftconfig.isSet("Crafts")) {
				for(String craftpath : craftconfig.getConfigurationSection("Crafts").getKeys(false)) {
					craftpath = "Crafts." + craftpath;
					ArrayList<ItemStack> config_craft = (ArrayList<ItemStack>) craftconfig.getList(craftpath + ".craft");
					ArrayList<ItemStack> config_resultitems = (ArrayList<ItemStack>) craftconfig.getList(craftpath + ".result.items");
					ArrayList<Integer> config_resultprobs = (ArrayList<Integer>) craftconfig.getIntegerList(craftpath + ".result.probs");
					HashMap<ItemStack,Integer> config_result = new HashMap<ItemStack, Integer>();
					for(ItemStack resultitem : config_resultitems) {
						config_result.put(resultitem, config_resultprobs.get(config_resultitems.indexOf(resultitem)));
					}
					ArrayList<String> config_cmds = (ArrayList<String>) craftconfig.getStringList(craftpath + ".cmds");
					boolean config_redstonepower = craftconfig.getBoolean(craftpath + ".redstonepower");
					int config_experience = craftconfig.getInt(craftpath + ".experience");
					CraftArray specraft = new CraftArray(config_craft, config_result, config_cmds, config_redstonepower, config_experience);
					craftlist.add(specraft);
				}
				return craftlist;
			} else {
				return craftlist;
			}
    	} catch (Exception e) {
    		e.printStackTrace();
    		return craftlist;
    	}
    }
    
    
    
    public void removeCraft(int nb) {
		try {
			//replacing in the file
	    	File craftfile = new File(plugin.getDataFolder(), "crafts.yml");
			craftfile.createNewFile();
			FileConfiguration craftconfig = YamlConfiguration.loadConfiguration(craftfile);
			int count = 0;
			for(String craftuuid : craftconfig.getConfigurationSection("Crafts").getKeys(false)) {
				if(nb == count) {
					craftconfig.set(craftuuid, null);
				}
				count++;
			}
			craftconfig.save(craftfile);
    	} catch (Exception e) {
    		//
    	}
    }
    
	public void addCraft(CraftArray globalcraft){
		try {
	    	File craftfile = new File(plugin.getDataFolder(), "crafts.yml");
			craftfile.createNewFile();
			FileConfiguration craftconfig = YamlConfiguration.loadConfiguration(craftfile);
			craftconfig.set("Crafts." + UUID.randomUUID(), globalcraft.serialize());
			craftconfig.save(craftfile);
    	} catch (Exception e) {
    		//
    	}
	}

}
