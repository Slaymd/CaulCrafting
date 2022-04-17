package fr.dariusmtn.caulcrafting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.event.Listener;

public class Config implements Listener {
    
	private CaulCrafting plugin;
    public Config(CaulCrafting instance) {
          this.plugin = instance; 
    }
	
	private void addDefault(String mainpath, String name, boolean replace, String extension) {
		//update file
		plugin.saveResource(name + extension, replace);
		//File move (source mkyong.com) 
		InputStream inStream = null;
		OutputStream outStream = null;
		try {
			File dfile = new File(plugin.getDataFolder(),name + extension);
			new File(plugin.getDataFolder(),mainpath).mkdir();
			File ffile = new File(plugin.getDataFolder(),mainpath + name + extension);
			inStream = new FileInputStream(dfile);
    	    outStream = new FileOutputStream(ffile);
    	    byte[] buffer = new byte[1024];
    	    int length;
    	    //copy the file content in bytes
    	    while ((length = inStream.read(buffer)) > 0){
    	    	outStream.write(buffer, 0, length);
    	    }
    	    inStream.close();
    	    outStream.close();
    	    //delete the original file
    	    dfile.delete();
    	}catch(IOException e){ 
    	    e.printStackTrace();
    	}
	    
	}

	public void setupDefaults() {
		//languages
		for(String loc : plugin.languagesAvailable.keySet()) {
			this.addDefault("/lang/", loc, true, ".properties");
			
		}
		//Locale config
		File dfile = new File(plugin.getDataFolder(),"config_locale.yml");
		if(!dfile.exists())
			plugin.saveResource("config_locale.yml", false);
		
	}
	
}
