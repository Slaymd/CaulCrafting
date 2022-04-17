package fr.dariusmtn.caulcrafting.itemsname;

import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;

public class Itemsname_1_9_R2 implements Itemsname{
	 
	@Override
	public String getItemStackName(ItemStack stack){
		String name = "";
		try{
			name =  CraftItemStack.asNMSCopy(stack).getName();
		} catch (Exception e){
			name = stack.getType().toString();
		}
		int amt = stack.getAmount();
		if(amt > 1){
			name += " §3x " + amt;
		}
		return name;
	}
	
}
