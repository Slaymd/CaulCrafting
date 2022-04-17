package fr.dariusmtn.caulcrafting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;

public class CraftArray implements ConfigurationSerializable{
	
	private ArrayList<ItemStack> craft = new ArrayList<ItemStack>();
	private HashMap<ItemStack,Integer> result = new HashMap<ItemStack,Integer>();
	private ArrayList<String> cmds = new ArrayList<String>();
	private boolean redstonepower = false;
	private int experience = 0;
	
	public CraftArray(ArrayList<ItemStack> craft, HashMap<ItemStack,Integer> result, ArrayList<String> cmds, boolean redstonepower, int experience) {
		this.setCraft(craft);
		this.setResult(result);
		this.setCmds(cmds);
		this.setRedstonepower(redstonepower);
	}
	
	public CraftArray(ArrayList<ItemStack> craft, ArrayList<ItemStack> result) {
		this.setCraft(craft);
		HashMap<ItemStack,Integer> newresult = new HashMap<ItemStack,Integer>();
		for(ItemStack ritem : result) {
			newresult.put(ritem, 1000);
		}
		this.setResult(newresult);
		
	}
	
	@SuppressWarnings("unchecked")
	public CraftArray(Map<String, Object> map) {
		this.craft = (ArrayList<ItemStack>) map.get("craft");
		this.result = (HashMap<ItemStack, Integer>) map.get("result");
		this.cmds = (ArrayList<String>) map.get("cmds");
		this.redstonepower = (boolean) map.get("redstonepower");
		this.experience = (int) map.get("experience");
	}

	public CraftArray() {
		//default
	}

	/**
	 * 
	 * @return the craft recipe
	 */
	public ArrayList<ItemStack> getCraft() {
		return craft;
	}

	/**
	 * 
	 * @param craft the craft recipe
	 */
	public void setCraft(ArrayList<ItemStack> craft) {
		this.craft = craft;
	}
	
	/**
	 * 
	 * @param item the recipe item
	 */
	public void addCraftItem(ItemStack item) {
		this.craft.add(item);
	}
	
	/**
	 * 
	 * @param item the recipe item
	 */
	public void removeCraftItem(ItemStack item) {
		this.craft.remove(item);
	}

	/**
	 * 
	 * @return the craft rewards
	 */
	public HashMap<ItemStack,Integer> getResult() {
		return result;
	}
	
	/**
	 * 
	 * @return the craft reward items
	 */
	public ArrayList<ItemStack> getResultItems() {
		ArrayList<ItemStack> resultitems = new ArrayList<ItemStack>();
		resultitems.addAll(result.keySet());
		return resultitems;
		
	}

	/**
	 * 
	 * @param result the craft rewards
	 */
	public void setResult(HashMap<ItemStack,Integer> result) {
		this.result = result;
	}
	
	/**
	 * 
	 * @param item the reward item
	 * @param luck chance of drop
	 */
	public void addResultItem(ItemStack item, double luck) {
		int luckInt = (int) (luck*10);
		if(luckInt > 1000)
			luckInt = 1000;
		if(luckInt < 1)
			luckInt = 1;
		if(result.containsKey(item))
			result.remove(item);
		this.result.put(item, luckInt);
	}
	
	/**
	 * 
	 * @param item the reward item
	 */
	public void removeResultItem(ItemStack item) {
		this.result.remove(item);
	}
	
	/**
	 * 
	 * @param item the reward item
	 */
	public void addResultItem(ItemStack item) {
		this.addResultItem(item, 100);
	}

	/**
	 * 
	 * @return reward commands
	 */
	public ArrayList<String> getCmds() {
		return cmds;
	}

	/**
	 * 
	 * @param cmds the reward commands
	 */
	public void setCmds(ArrayList<String> cmds) {
		this.cmds = cmds;
	}
	
	/**
	 * 
	 * @param cmd the reward command
	 */
	public void addCmd(String cmd) {
		if(cmd.startsWith("/"))
			cmd.substring(1);
		this.cmds.add(cmd);
	}
	
	/**
	 * 
	 * @param cmd the reward command
	 */
	public void removeCmd(String cmd) {
		this.cmds.remove(cmd);
	}

	/**
	 * 
	 * @return if craft make cauldron redstone powered
	 */
	public boolean isRedstonepower() {
		return redstonepower;
	}

	/**
	 * 
	 * @param redstonepower if true, craft make cauldron redstone powered
	 */
	public void setRedstonepower(boolean redstonepower) {
		this.redstonepower = redstonepower;
	}

	/**
	 * @return the experience
	 */
	public int getExperience() {
		return experience;
	}

	/**
	 * @param experience the experience to set
	 */
	public void setExperience(int experience) {
		this.experience = experience;
	}

	/**
	 * 
	 * @return if craft is empty or not
	 */
	public boolean isEmpty() {
		boolean empty = false;
		if(this.getCraft().isEmpty())
			empty = true;
		if(this.getResult().isEmpty())
			empty = true;
		if(this.getCmds().isEmpty())
			empty = true;
		if(this.isRedstonepower())
			empty = true;
		return empty;
	}

	@Override
	public Map<String, Object> serialize() {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("craft", this.craft);
        HashMap<ItemStack, Integer> ofresult = this.result;
        ArrayList<ItemStack> seresult = new ArrayList<ItemStack>();
        seresult.addAll(ofresult.keySet());
        map.put("result.items", seresult);
        ArrayList<Integer> seresultprobs = new ArrayList<Integer>();
        seresultprobs.addAll(ofresult.values());
        map.put("result.probs", seresultprobs);
        map.put("cmds", this.cmds);
        map.put("redstonepower", this.redstonepower);
        map.put("experience", this.experience);
        return map;
	}

}
