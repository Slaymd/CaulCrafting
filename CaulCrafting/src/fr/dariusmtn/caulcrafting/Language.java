package fr.dariusmtn.caulcrafting;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

public class Language implements Listener {
    
	private CaulCrafting plugin;
    public Language(CaulCrafting instance) {
          this.plugin = instance; 
    }
    
	
	public String getLanguage() {
		//Get exact lang
		String loc = getExactLanguage();
		File file = new File(plugin.getDataFolder(), "/lang/" + loc + ".yml");
		if(file.exists()) {
			return loc;
		} else {
			return "en";
		}
	}
	
	public void setLanguage(String loc) {
		try {
	    	File locfile = new File(plugin.getDataFolder(), "config_locale.yml");
			locfile.createNewFile();
			FileConfiguration locconfig = YamlConfiguration.loadConfiguration(locfile);
			locconfig.set("lang", loc);
			locconfig.save(locfile);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	
	public String getExactLanguage() {
		File locfile = new File(plugin.getDataFolder(), "config_locale.yml");
		FileConfiguration locconfig = YamlConfiguration.loadConfiguration(locfile);
		String loc = locconfig.getString("lang");
		return loc;
	}

	public String getTranslation(String code) {
		return getTranslation(code, getLanguage());
	}
	
	public String getTranslation(String code, String locale) {
		String transl = code;
		try {
			File locfile = new File(plugin.getDataFolder() + "/lang/" + locale + ".yml");
			FileConfiguration locconfig = YamlConfiguration.loadConfiguration(locfile);
			transl = locconfig.getString(code.toLowerCase());
			return transl.replaceAll("&", "§");
		} catch (Exception e) {
			try {
				File locfile = new File(plugin.getDataFolder() + "/lang/en.yml");
				FileConfiguration locconfig = YamlConfiguration.loadConfiguration(locfile);
				transl = locconfig.getString(code.toLowerCase());
				return transl.replaceAll("&", "§");
			} catch (Exception ee) {
				return code;
			}
		}
	}
	
	
	
}
