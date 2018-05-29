package fr.dariusmtn.caulcrafting;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Properties;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;

public class Language implements Listener {
	
	public static String getLanguage() {
		if (CaulCrafting.dataFolder == null)
			return null;
		//Get exact lang 
		String loc = getExactLanguage();
		File file = new File(CaulCrafting.dataFolder, "/lang/" + loc + ".properties");
		if(file.exists()) {
			return loc;
		} else {
			return "en";
		}
	}
	
	public static void setLanguage(String loc) {
		if (CaulCrafting.dataFolder == null)
			return;
		try {
	    	File locfile = new File(CaulCrafting.dataFolder, "config_locale.yml");
			locfile.createNewFile();
			FileConfiguration locconfig = YamlConfiguration.loadConfiguration(locfile);
			locconfig.set("lang", loc);
			locconfig.save(locfile);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
	}
	
	public static String getExactLanguage() {
		if (CaulCrafting.dataFolder == null)
			return null;
		File locfile = new File(CaulCrafting.dataFolder, "config_locale.yml");
		FileConfiguration locconfig = YamlConfiguration.loadConfiguration(locfile);
		String loc = locconfig.getString("lang");
		return loc;
	}

	public static String getTranslation(String code) {
		return getTranslation(code, getLanguage());
	}
	
	public static String getTranslation(String code, String locale) {
		if (CaulCrafting.dataFolder == null)
			return null;
		String transl = code;
		try {
			File locfile = new File(CaulCrafting.dataFolder + "/lang/" + locale + ".properties");
			FileInputStream locStream = new FileInputStream(locfile);
			Properties locprop = new Properties();
			locprop.load(new InputStreamReader(locStream, Charset.forName("UTF-8")));
			transl = locprop.getProperty(code.toLowerCase());
			locStream.close();
			return transl.replaceAll("&", "§");
		} catch (Exception e) {
			try {
				File locfile = new File(CaulCrafting.dataFolder + "/lang/en.properties");
				FileInputStream locStream = new FileInputStream(locfile);
				Properties locprop = new Properties();
				locprop.load(new InputStreamReader(locStream, Charset.forName("UTF-8")));
				transl = locprop.getProperty(code.toLowerCase());
				locStream.close();
				return transl.replaceAll("&", "§");
			} catch (Exception ee) {
				return code;
			}
		}
	}
	
}
