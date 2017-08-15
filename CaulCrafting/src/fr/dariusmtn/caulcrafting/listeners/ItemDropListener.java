package fr.dariusmtn.caulcrafting.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import fr.dariusmtn.caulcrafting.CaulCrafting;
import fr.dariusmtn.caulcrafting.CraftArray;
import fr.dariusmtn.caulcrafting.events.CaulCraftDroppingEvent;
import fr.dariusmtn.caulcrafting.events.CaulCraftFailEvent;
import fr.dariusmtn.caulcrafting.events.CaulCraftSuccessEvent;

public class ItemDropListener implements Listener {
	
	private CaulCrafting plugin;
	public ItemDropListener(CaulCrafting plugin) {
		this.plugin = plugin;
	}
	
	Random random = new Random();
	
	Entity[] getNearbyEntities(Location l, int radius){
		int chunkRadius = radius < 16 ? 1 : (radius - (radius % 16))/16;
		HashSet<Entity> radiusEntities = new HashSet<Entity>();
      	for (int chX = 0 -chunkRadius; chX <= chunkRadius; chX ++){
      		for (int chZ = 0 -chunkRadius; chZ <= chunkRadius; chZ++){
      			int x=(int) l.getX(),y=(int) l.getY(),z=(int) l.getZ();
      			for (Entity e : new Location(l.getWorld(),x+(chX*16),y,z+(chZ*16)).getChunk().getEntities()){
      				if (e.getLocation().distance(l) <= radius && e.getLocation().getBlock() != l.getBlock()) radiusEntities.add(e);
      			}
      		}
      	}
	     return radiusEntities.toArray(new Entity[radiusEntities.size()]);
	}
	
	@EventHandler
	public void onItemDrop(PlayerDropItemEvent e){
		final Player player = e.getPlayer();
		final Item item = e.getItemDrop();
		//EDITOR BY DROPPING
		if(plugin.editor.containsKey(player)){
			e.setCancelled(true);
			int editorstep = plugin.editor.get(player);
			if(editorstep < 3){
				String mode = "craft";
				if(editorstep == 2)
					mode = "result";
				if(item != null){
					ItemStack finalitem = item.getItemStack().clone();
					//Amount
					ItemStack itemhand = new ItemStack(player.getInventory().getItemInMainHand());
					itemhand.setAmount(1);
					if(itemhand.isSimilar(finalitem))
						finalitem.setAmount(player.getInventory().getItemInMainHand().getAmount()+1);
					//Adding to the editor
					plugin.editorUtils.addItem(player, finalitem, mode);
					return;
				}
				
			}
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			@SuppressWarnings("deprecation")
			public void run(){
				Location itemLoc = item.getLocation();
				//Si l'item est sur le sol
				if(item.isOnGround()){
					//Si l'item est dans un chadron
					if(itemLoc.getBlock().getType() == Material.CAULDRON){
						Block caul = itemLoc.getBlock();
						//Event: CaulCraftDroppingEvent
						plugin.sendDebug(player,"API : CaulCraftDroppingEvent");
						CaulCraftDroppingEvent ccEvent = new CaulCraftDroppingEvent(player, caul, Particle.SPELL_MOB, Sound.BLOCK_BREWING_STAND_BREW);
						plugin.getServer().getPluginManager().callEvent(ccEvent);
						
						//contain water
						if(caul.getData() > 0 && !ccEvent.isCancelled() && player.hasPermission("caulcrafting.use")){
							if(!plugin.caulLoc.containsKey(player.getUniqueId()))
								plugin.caulLoc.put(player.getUniqueId(), caul.getLocation());
							
							plugin.sendDebug(player,"STEP1 a/b - detecting dropping into cauldron " + item.getItemStack().getType());
							//Particule dans le chadron
							plugin.sendDebug(player,"STEP1 b/b - sending particle and sound");
							if(ccEvent.getParticle() != null)
								itemLoc.getWorld().spawnParticle(ccEvent.getParticle(), itemLoc, 100);
							//Son
							if(ccEvent.getSound() != null)
								player.getWorld().playSound(player.getLocation(),ccEvent.getSound(),1, 0);
							//Pickup delay
							if(!ccEvent.canItemPickup())
								item.setPickupDelay(100);
						}
					}
				}
			}
		},20);
		if(!plugin.craftProc.contains(player.getUniqueId())){
			plugin.craftProc.add(player.getUniqueId());
			plugin.sendDebug(player,"STEP1* a/b - starting scheduler...");
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
				@SuppressWarnings({ "deprecation" })
				public void run(){
					//Suppression des valeurs.
					clearVar(player);
					if(!plugin.caulLoc.containsKey(player.getUniqueId()))
						return;
					plugin.sendDebug(player,"STEP1* b/b - scheduler launched!");
					int count = 0;
					plugin.sendDebug(player,"STEP2 a/c - verifying cauldron content...");
					for(Entity entIn : plugin.caulLoc.get(player.getUniqueId()).getChunk().getEntities()){
						if(entIn.getType() == EntityType.DROPPED_ITEM){
							ItemStack itms = ((Item)entIn).getItemStack();
							if(entIn.getLocation().getBlock().getType() == Material.CAULDRON){
								count++;
								//Ajout dans la liste du chaudron
								ArrayList<ItemStack> itemToAdd = new ArrayList<ItemStack>();
								if(plugin.inCaulFin.containsKey(player.getUniqueId()))
									itemToAdd = plugin.inCaulFin.get(player.getUniqueId());
								itemToAdd.add(itms);
								plugin.inCaulFin.put(player.getUniqueId(), itemToAdd);
								plugin.sendDebug(player,"STEP2 b/c - items in cauldron +" + itms.getType().toString());
							}
						}
					}
					//On éxécute le craft
					plugin.sendDebug(player,"STEP2 c/c - cauldron content ok (" + count + " items)");
					//On récup tous les crafts
					ArrayList<CraftArray> allcrafts = plugin.craftStorage.getCrafts();
					//Un par un on voit s'ils correspondent
					CraftArray actualcraft = new CraftArray();
					boolean stop = false;
					plugin.sendDebug(player,"STEP3 a/a - checking the craft");
					for(CraftArray ecraft : allcrafts){
						if(stop == false){
							ArrayList<ItemStack> need = ecraft.getCraft();
							ArrayList<ItemStack> droped = plugin.inCaulFin.get(player.getUniqueId());
							actualcraft = ecraft;
							//S'ils sont similaires on arrête la boucle
							if(!droped.isEmpty() && droped.containsAll(need)){
								stop = true;
								plugin.sendDebug(player,"STEP4a a/d - craft detected : " + ecraft.toString());
							}
						}
					}
					if(stop == true){
						//CRAFT SUCCESS !
						
						//Event: CaulCraftSuccessEvent
						plugin.sendDebug(player,"API : CaulCraftSuccessEvent");
						CaulCraftSuccessEvent ccEvent = new CaulCraftSuccessEvent(actualcraft, player, plugin.caulLoc.get(player.getUniqueId()).getBlock(), Particle.FIREWORKS_SPARK, Sound.ENTITY_PLAYER_LEVELUP);
						plugin.getServer().getPluginManager().callEvent(ccEvent);
						
						if(ccEvent.isCancelled()) {
							plugin.sendDebug(player,"API : CaulCraftSuccessEvent cancelled");
							return;
						}
						
						//Cauldron center
						Location cauldronlocation = ccEvent.getCauldron().getLocation().add(0.5, 0, 0.5);
						
						//removing cauldron items
						plugin.sendDebug(player,"STEP4a b/d - removing cauldrons items (into)");
						for(Entity ent : getNearbyEntities(cauldronlocation,1)){
							if(ent instanceof Item && ccEvent.isItemDeleting()){
								Item itm = (Item)ent;
								if(ccEvent.getCraft().getCraft().contains(itm.getItemStack())){
									ent.remove();
								}
							}
						}
						
						//reward items
						plugin.sendDebug(player,"STEP4a c/d - sending rewards");
						if(ccEvent.isSendingRewards()) {
							HashMap<ItemStack, Integer> craftrewards = ccEvent.getCraft().getResult();
							for(ItemStack itemresult : craftrewards.keySet()) {
								int itemrd = craftrewards.get(itemresult);
								if(itemrd < 1000) {
									//drop chance
									int rd = random.nextInt(1001); //0-1000
									if(rd <= itemrd)
										cauldronlocation.getWorld().dropItemNaturally(cauldronlocation.clone().add(0, 1, 0), itemresult);
								} else {
									cauldronlocation.getWorld().dropItemNaturally(cauldronlocation.clone().add(0, 1, 0), itemresult);
								}
							}
							//reward commands
							for(String cmd : ccEvent.getCraft().getCmds()) {
								//by player
								if(cmd.startsWith("plcmd")) {
									cmd = cmd.replace("plcmd/", "");
									player.performCommand(cmd);
								} else if(cmd.startsWith("opcmd")) {
									cmd = cmd.replace("opcmd/", "");
									plugin.getServer().dispatchCommand(Bukkit.getConsoleSender(), cmd.replaceAll("<player>", player.getName()));
								}
								
							}
						}
						
						//Particles
						plugin.sendDebug(player,"STEP4a d/d - sending particles");
						if(ccEvent.getParticle() != null) {
							cauldronlocation.getWorld().spawnParticle(ccEvent.getParticle(), cauldronlocation, 40);
							player.getWorld().playSound(player.getLocation(),ccEvent.getSound(),1, 0);
						}
						//Water layer
						if(!player.hasPermission("caulcrafting.nowaterconsume") && ccEvent.isEditingWaterLayer()) {
							plugin.sendDebug(player,"STEP4a* a/a - modifying water layers");
							Block caul = plugin.caulLoc.get(player.getUniqueId()).getBlock();
							byte caulData = caul.getData();
							caul.setData((byte) (caulData-1));
						}
					} else {
						ArrayList<ItemStack> droped = plugin.inCaulFin.get(player.getUniqueId());
						//Event: CaulCraftFailEvent
						plugin.sendDebug(player,"API : CaulCraftFailEvent");
						CaulCraftFailEvent ccEvent = new CaulCraftFailEvent(droped, player, plugin.caulLoc.get(player.getUniqueId()).getBlock(), Particle.SPELL_WITCH, Sound.ITEM_BOTTLE_FILL_DRAGONBREATH);
						plugin.getServer().getPluginManager().callEvent(ccEvent);
						
						//Craft invalide
						plugin.sendDebug(player,"STEP4b a/b - detecting wrong process");
						if(ccEvent.getSound() != null)
							player.getWorld().playSound(player.getLocation(),ccEvent.getSound(),1, 0);
						if(ccEvent.getParticle() != null)
							plugin.caulLoc.get(player.getUniqueId()).getWorld().spawnParticle(ccEvent.getParticle(), ccEvent.getCauldron().getLocation().add(0.5, 0, 0.5), 100);
						plugin.sendDebug(player,"STEP4b b/b - wrong process succeed");
						
					}
				}
			},100);
		}	 
	}
	
	void clearVar(final Player player){
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
			public void run(){
				plugin.sendDebug(player,"STEP5 a/b - starting removing vars");
				plugin.craftProc.remove(player.getUniqueId());
				plugin.caulLoc.remove(player.getUniqueId());
				plugin.inCaulFin.remove(player.getUniqueId());
				plugin.sendDebug(player,"STEP5 b/b - removing vars succeed");
			}
		},4);
	}
	

}
