package fr.dariusmtn.caulcrafting;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class CraftFormatting implements Listener {
    
	private CaulCrafting plugin;
    public CraftFormatting(CaulCrafting instance) {
          this.plugin = instance; 
    }
    
    public String getCraftRecap(HashMap<String,ArrayList<ItemStack>> globalcraft){
		ArrayList<ItemStack> need = globalcraft.get("craft");
		ArrayList<ItemStack> result = globalcraft.get("result");
		String name = "";
		//Elements requis
		String reNeed = "";
		if(!need.isEmpty()){
			for(ItemStack item : need){
				name = getName(item);
				reNeed += (reNeed == "" ? "" : ", ") + name + "§r";
			}
		} else {
			reNeed = "§7§o" + plugin.lang.getTranslation("general_undefined");
		}
		//Elements donnés 
		String reResult = "";
		if(!result.isEmpty()){
			for(ItemStack item : result){
				name = getName(item);
				reResult += (reResult == "" ? "" : ", ") + name + "§r";
			}
		} else {
			reResult = "§7§o" + plugin.lang.getTranslation("general_undefined");
		}
		//Recap
		String recap = reNeed + " §e§l§m--§e§l>§r " + reResult;
		return recap;
	}
	
	public String getName(ItemStack stack) {
		if(plugin.nmsItemsName == true){
			return plugin.getItemsname().getItemStackName(stack);
		} else {
			String name = "";
			name = stack.getType().toString();
			int amt = stack.getAmount();
			if(amt > 1){
				name += " §3x " + amt;
			}
			return name;
		}
	}

}
