package fr.dariusmtn.caulcrafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.Callable;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import fr.dariusmtn.caulcrafting.commands.CaulCraftingCommandExecutor;
import fr.dariusmtn.caulcrafting.commands.CaulCraftingConfigCommandExecutor;
import fr.dariusmtn.caulcrafting.itemsname.Itemsname;
import fr.dariusmtn.caulcrafting.itemsname.Itemsname_1_10_R1;
import fr.dariusmtn.caulcrafting.itemsname.Itemsname_1_11_R1;
import fr.dariusmtn.caulcrafting.itemsname.Itemsname_1_12_R1;
import fr.dariusmtn.caulcrafting.itemsname.Itemsname_1_9_R1;
import fr.dariusmtn.caulcrafting.itemsname.Itemsname_1_9_R2;
import fr.dariusmtn.caulcrafting.listeners.AsyncPlayerChatListener;
import fr.dariusmtn.caulcrafting.listeners.ItemDropListener;

public class CaulCrafting extends JavaPlugin implements Listener {
	
	public ConfigUpdate configUpdate = new ConfigUpdate(this);
	public Config configUtils = new Config(this);
	public Language lang = new Language(this);
	public CraftStorage craftStorage = new CraftStorage(this);
	public CraftFormatting craftFormat = new CraftFormatting(this);
	public Editor editorUtils = new Editor(this);
	
	//Languages availables list 
	public HashMap<String, String> languagesAvailable = new HashMap<String,String>();
	
	@Override
	public void onEnable(){
		ConfigurationSerialization.registerClass(CraftArray.class);
		//Commands executors
		this.getCommand("caulcrafting").setExecutor(new CaulCraftingCommandExecutor(this));
		this.getCommand("caulcraftingconfig").setExecutor(new CaulCraftingConfigCommandExecutor(this));
		//LISTENERS - EVENTS register
		PluginManager plugman = getServer().getPluginManager();
		plugman.registerEvents(new AsyncPlayerChatListener(this), this);
		plugman.registerEvents(new ItemDropListener(this), this);
		//Setup languages
		languagesAvailable.put("en", "English");
		languagesAvailable.put("fr", "Français");
		languagesAvailable.put("ru", "Русский");
		languagesAvailable.put("nl", "Dutch");
		languagesAvailable.put("de", "Deutsch");
		languagesAvailable.put("ja", "日本語");
		languagesAvailable.put("pl", "Polski");
		languagesAvailable.put("vi", "Tiếng Việt");
		languagesAvailable.put("es", "Español");
		languagesAvailable.put("pt", "Português");
		//Defaults configs files (locales..)
		configUtils.setupDefaults();
		//Load defaults configs if empty
		saveDefaultConfig();
		//Updating config
		configUpdate.update();
		//nms class for items name utils
		if(setupItemsname()){
			nmsItemsName = true;
		} else {
			getLogger().severe(lang.getTranslation("updater_warn_1"));
			getLogger().severe(lang.getTranslation("updater_warn_2"));
			nmsItemsName = false;
		}
		//Stats (bstats) https://bstats.org/plugin/bukkit/CaulCrafting
		Metrics metrics = new Metrics(this);
		metrics.addCustomChart(new Metrics.SimplePie("used_languages", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	            return lang.getLanguage();
	        }
	    }));
	} 
	
	Itemsname itemsname = null;
	boolean nmsItemsName = false;
	
	public Itemsname getItemsname(){
		return itemsname;
	}
	
	private boolean setupItemsname(){
		String version;
		try{
			version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
		} catch (Exception e){
			return false;
		}
		//Gestion des versions
		if(version.equalsIgnoreCase("v1_12_R1")){
			itemsname = new Itemsname_1_12_R1();
		} else if(version.equalsIgnoreCase("v1_11_R1")){
			itemsname = new Itemsname_1_11_R1();
		} else if(version.equalsIgnoreCase("v1_10_R1")){
			itemsname = new Itemsname_1_10_R1();
		} else if(version.equalsIgnoreCase("v1_9_R2")){
			itemsname = new Itemsname_1_9_R2();
		} else if(version.equalsIgnoreCase("v1_9_R1")){
			itemsname = new Itemsname_1_9_R1();
		}
		return itemsname != null;
	}
	
	public HashMap<Player,Integer> editor = new HashMap<Player,Integer>();
	public HashMap<Player,CraftArray> craft = new HashMap<Player,CraftArray>();
	
	public ArrayList<UUID> craftProc = new ArrayList<UUID>();
	public HashMap<UUID,Location> caulLoc = new HashMap<UUID,Location>();
	public HashMap<UUID,ArrayList<ItemStack>> inCaulFin = new HashMap<UUID,ArrayList<ItemStack>>();
	
	public void sendDebug(Player player, String msg) {
		if(getConfig().getBoolean("debug_message") == true) {
			getLogger().info("CaulCrafting DEBUG " + player.getName() + ": " + msg);
		}
	}
}
