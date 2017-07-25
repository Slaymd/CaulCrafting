package fr.dariusmtn.caulcrafting;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

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
	public ArrayList<HashMap<String,ArrayList<ItemStack>>> getCrafts(){
    	ArrayList<HashMap<String,ArrayList<ItemStack>>> craftlist = new ArrayList<HashMap<String,ArrayList<ItemStack>>>();
    	try {
	    	File craftfile = new File(plugin.getDataFolder(), "crafts.yml");
			craftfile.createNewFile();
			FileConfiguration craftconfig = YamlConfiguration.loadConfiguration(craftfile);
			if(craftconfig.isSet("Crafts")) {
				craftlist = (ArrayList<HashMap<String, ArrayList<ItemStack>>>) craftconfig.get("Crafts");
				return craftlist;
			} else {
				return new ArrayList<HashMap<String,ArrayList<ItemStack>>>();
			}
    	} catch (Exception e) {
    		return new ArrayList<HashMap<String,ArrayList<ItemStack>>>();
    	}
    }
    
    public void removeCraft(int nb) {
    	//List of all crafts
    	ArrayList<HashMap<String,ArrayList<ItemStack>>> craftlist = getCrafts();
		try {
			//removing
			craftlist.remove(nb);
			//replacing in the file
	    	File craftfile = new File(plugin.getDataFolder(), "crafts.yml");
			craftfile.createNewFile();
			FileConfiguration craftconfig = YamlConfiguration.loadConfiguration(craftfile);
			craftconfig.set("Crafts", craftlist);
			craftconfig.save(craftfile);
    	} catch (Exception e) {
    		//
    	}
    }
    
	public void addCraft(HashMap<String,ArrayList<ItemStack>> globalcraft){
		//Récupère la liste totale des crafts
    	ArrayList<HashMap<String,ArrayList<ItemStack>>> craftlist = getCrafts();
		//Ajoute à la liste
		craftlist.add(globalcraft);
		try {
	    	File craftfile = new File(plugin.getDataFolder(), "crafts.yml");
			craftfile.createNewFile();
			FileConfiguration craftconfig = YamlConfiguration.loadConfiguration(craftfile);
			craftconfig.set("Crafts", craftlist);
			craftconfig.save(craftfile);
    	} catch (Exception e) {
    		//
    	}
	}

}
