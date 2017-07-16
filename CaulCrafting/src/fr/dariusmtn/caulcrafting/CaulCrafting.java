package fr.dariusmtn.caulcrafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import fr.dariusmtn.caulcrafting.itemsname.Itemsname;
import fr.dariusmtn.caulcrafting.itemsname.Itemsname_1_10_R1;
import fr.dariusmtn.caulcrafting.itemsname.Itemsname_1_11_R1;
import fr.dariusmtn.caulcrafting.itemsname.Itemsname_1_12_R1;

public class CaulCrafting extends JavaPlugin implements Listener {
	
	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(this, this);
		saveDefaultConfig();
		if(setupItemsname()){
			nmsItemsName = true;
		} else {
			getLogger().severe("CaulCrafting is not fully compatible with your server version.");
			getLogger().severe("Check CaulCrafting updates !");
			nmsItemsName = false;
		}
	}
	
	static Itemsname itemsname = null;
	static boolean nmsItemsName = false;
	
	public static Itemsname getItemsname(){
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
		}
		return itemsname != null;
	}
	
	HashMap<Player,Integer> editor = new HashMap<Player,Integer>();
	HashMap<Player,HashMap<String,ArrayList<ItemStack>>> craft = new HashMap<Player,HashMap<String,ArrayList<ItemStack>>>();
	
	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(cmd.getName().equalsIgnoreCase("caulcrafting")){
			//Joueur OP
			if(sender.isOp()){
				//Joueur réel
				if(sender instanceof Player){
					Player player = (Player)sender;
					//Sous commandes
					if(args.length > 0){
						if(args[0].equalsIgnoreCase("create")){
							//CRÉATION DE CRAFT
							if(args.length > 1){
								if(args.length == 3){
									if(!editor.containsKey(player)){
										HashMap<String,ArrayList<ItemStack>> craftcmd = new HashMap<String,ArrayList<ItemStack>>();
										//On converti en ItemStack
										boolean error = false;
										for(int i = 1; i <= 2; i++){
											ArrayList<String> part = new ArrayList<String>();
											if(i == 1){
												//Craft
												part = new ArrayList<String>(Arrays.asList(args[1].split(",")));
											} else if(i == 2){
												//Result
												part = new ArrayList<String>(Arrays.asList(args[2].split(",")));
											}
											for(String eitm : part){
												if(error == false){
													ItemStack itms = null;
													//Détection du nombre
													int amount = 1;
													if(eitm.contains("*")){
														try{
															amount = Integer.parseInt(eitm.substring(eitm.indexOf("*")+1, eitm.length()));
														} catch (Exception e){
															//...
														}
													}
													eitm = eitm.replace("*" + amount, "");
													//ID avec Data
													if(eitm.contains(":")){
														ArrayList<String> idastr = new ArrayList<String>(Arrays.asList(eitm.split(":")));
														try{
															itms = new ItemStack(Material.getMaterial(Integer.parseInt(idastr.get(0))),amount);
															itms.setDurability(Short.parseShort(idastr.get(1)));
														} catch (Exception e){
															try{
																itms = new ItemStack(Material.getMaterial(idastr.get(0).toUpperCase()),amount);
																itms.setDurability(Short.parseShort(idastr.get(1)));
															} catch (Exception ee){
																//...
															}
														}
													} else {
														try{
															if(Integer.parseInt(eitm) >= 0){
																itms = new ItemStack(Material.getMaterial(Integer.parseInt(eitm)),amount);
															}
														} catch (Exception e){
															try{
																itms = new ItemStack(Material.getMaterial(eitm.toUpperCase()),amount);
															} catch (Exception ee){
																player.sendMessage("§cWrong: " + eitm);
															}
														}
														
													}
													if(itms != null){
														if(i == 1){
															ArrayList<ItemStack> ccraft = new ArrayList<ItemStack>();
															if(craftcmd.containsKey("craft"))
																ccraft = craftcmd.get("craft");
															ccraft.add(itms);
															craftcmd.put("craft", ccraft);
														} else if(i == 2){
															ArrayList<ItemStack> ccraft = new ArrayList<ItemStack>();
															if(craftcmd.containsKey("result"))
																ccraft = craftcmd.get("result");
															ccraft.add(itms);
															craftcmd.put("result", ccraft);
														}
													} else {
														error = true;
													}
												}
											}
										}
										
										if(!craftcmd.isEmpty()){
											if(error == false){
												player.sendMessage("§d§l➤ That's right ?");
												player.sendMessage("§bCraft typed :§r " + getCraftRecap(craftcmd));
												player.sendMessage("§eWrite §2yes§e to confirm and save it or §cno§e to cancel.");
												editor.put(player, 3);
												craft.put(player, craftcmd);
												return true;
											}
										}
									} else {
										player.sendMessage("§cYou need to confirm the last craft !");
										player.sendMessage("§7Write §2yes§7 to confirm and save it or §cno§7 to cancel.");
										return false;
									}
								}
								player.sendMessage("§e/caulcrafting §2create §b<craft>§l*§b <rewards>§l*");
								player.sendMessage("§b§l* Example: §f264*9,stick §7(9 x DIAMOND + 1 x STICK)");
								return false;
							} else {
								if(!editor.containsKey(player)){
									//Listes éditeur
									editor.put(player, 1);
									HashMap<String,ArrayList<ItemStack>> init = new HashMap<String,ArrayList<ItemStack>>();
									init.put("craft", new ArrayList<ItemStack>());
									init.put("result", new ArrayList<ItemStack>());
									craft.put(player, init);
									//Explications
									player.sendMessage("§d§l➤ Making a new craft");
									player.sendMessage("§eSelect items in your §d§omain§d hand§e "
											+ "and write §2§ladd§e on chat.");
									player.sendMessage("§f§l§m-----");
									player.sendMessage("§7Write §oexit§7 to leave this editor.");
									player.sendMessage("§7Write §cremovelast§7 to remove the last item you added.");
									player.sendMessage("§eWrite §2next§e to go to the next step.");
									player.sendMessage("§d§l§m-----");
									return true;
								}
								player.sendMessage("§cYou're already in the editor! ;)");
								return false;
							}
						}
						else if(args[0].equalsIgnoreCase("list")){
							//Intervalle d'action (pagination)
							int page = 0;
							if(args.length > 1){
								String p = args[1];
								try{
									page = Integer.parseInt(p);
									if(page < 0)
										page = 0;
								}catch (Exception e){
									player.sendMessage("§cNo craft to display on this page.");
									return true;
								}
							}
							int mincraft = 10*page;
							int maxcraft = mincraft + 10;
							//On récup les crafts
							ArrayList<HashMap<String,ArrayList<ItemStack>>> craftlist = new ArrayList<HashMap<String,ArrayList<ItemStack>>>();
							if(getConfig().isSet("Crafts")){
								craftlist = (ArrayList<HashMap<String, ArrayList<ItemStack>>>) getConfig().get("Crafts");
							}
							//On affiche la liste
							int count = 0;
							if(craftlist.size()-1 >= mincraft){
								player.sendMessage("§d§l§m-----§b(Page " + page + ")");
								for(HashMap<String,ArrayList<ItemStack>> crafts : craftlist){
									if(count >= mincraft && count <= maxcraft){
										player.sendMessage("§6§l" + count + ".§r " + getCraftRecap(crafts));
									}
									count++;
								}
								if(maxcraft < craftlist.size()-1){
									player.sendMessage("§d§l§m-----§b(Next page : §3/... list " + (page+1) + "§b)");
								} else {
									player.sendMessage("§d§l§m-----§b(List finished)");
								}
							} else {
								player.sendMessage("§cNo craft to display.");
							}
							return true;
						}
						else if(args[0].equalsIgnoreCase("remove")){
							//Numéro du craft
							int nb = -1;
							if(args.length > 1){
								String p = args[1];
								try{
									nb = Integer.parseInt(p);
									if(nb < 0)
										nb = -1;
								}catch (Exception e){
									nb = -1;
								}
							}
							if(nb != -1){
								//On récup les crafts
								ArrayList<HashMap<String,ArrayList<ItemStack>>> craftlist = new ArrayList<HashMap<String,ArrayList<ItemStack>>>();
								if(getConfig().isSet("Crafts")){
									craftlist = (ArrayList<HashMap<String, ArrayList<ItemStack>>>) getConfig().get("Crafts");
								}
								if(craftlist.size()-1 >= nb){
									HashMap<String, ArrayList<ItemStack>> specraft = craftlist.get(nb);
									player.sendMessage("§7Removed craft: §c§m" + getCraftRecap(specraft));
									//On supprime
									craftlist.remove(nb);
									//Met à jour sur la config
									getConfig().set("Crafts", craftlist);
									saveConfig();
									//Reload config ;)
									reloadConfig();
									return true;
								}
							}
							player.sendMessage("§cInvalid craft number! §o(maybe check §4§o/caulcrafting list§c§o ?)");
							return true;
						}
					}
					player.sendMessage("§b§lCaulCrafting v" + getDescription().getVersion() + " §bby Slaymd.");
					player.sendMessage("§7§l§m-----");
					player.sendMessage("§e/caulcrafting §2create §7§oCreate a new craft");
					player.sendMessage("§e/caulcrafting §blist §7§oList of crafts");
					player.sendMessage("§e/caulcrafting §cremove <nb> §7§oRemove a specified craft");
					player.sendMessage("§7§l§m-----");
					return false;
				}
				sender.sendMessage("§cAre you a robot? Oo Sorry but you need to be connected! ;)");
				return false;
			}
			sender.sendMessage("§cYou need to be OP! :(");
			return false;
		}
		return false;
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
			reNeed = "§7§oUndefined";
		}
		//Elements donnés
		String reResult = "";
		if(!result.isEmpty()){
			for(ItemStack item : result){
				name = getName(item);
				reResult += (reResult == "" ? "" : ", ") + name + "§r";
			}
		} else {
			reResult = "§7§oUndefined";
		}
		//Recap
		String recap = reNeed + " §e§l§m--§e§l>§r " + reResult;
		return recap;
	}
	
	@SuppressWarnings("unchecked")
	public void addCraft(HashMap<String,ArrayList<ItemStack>> globalcraft){
		//Récupère la liste totale des crafts
		ArrayList<HashMap<String,ArrayList<ItemStack>>> craftlist = new ArrayList<HashMap<String,ArrayList<ItemStack>>>();
		if(getConfig().isSet("Crafts")){
			craftlist = (ArrayList<HashMap<String, ArrayList<ItemStack>>>) getConfig().get("Crafts");
		}
		//Ajoute à la liste
		craftlist.add(globalcraft);
		getConfig().set("Crafts", craftlist);
		saveConfig();
	}
	
	public static String getName(ItemStack stack) {
		if(nmsItemsName == true){
			return getItemsname().getItemStackName(stack);
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
	
	@EventHandler
	public void onPlayerChat(AsyncPlayerChatEvent e){
		Player player = e.getPlayer();
		String msg = e.getMessage();
		//S'il est dans l'éditeur
		if(editor.containsKey(player)){
			e.setCancelled(true);
			if(editor.get(player) < 3){
				String mode = "craft";
				if(editor.get(player) == 2)
					mode = "result";
				//quitter
				if(msg.equalsIgnoreCase("exit")){
					editor.remove(player);
					player.sendMessage("§cEditor left.");
				}
				//ajouter un craft
				else if(msg.equalsIgnoreCase("add")){
					ItemStack item = player.getInventory().getItemInMainHand();
					if(item != null){
						player.sendMessage("§7§l§m-----");
						//Affichage du nom
						String name = getName(item);
						player.sendMessage("§7Item added : §a" + name);
						//Ajout définitif
						ArrayList<ItemStack> globalcraft = craft.get(player).get(mode);
						globalcraft.add(item);
						HashMap<String,ArrayList<ItemStack>> totalcraft = craft.get(player);
						totalcraft.put(mode, globalcraft);
						craft.put(player, totalcraft);
						//Recap
						String recap = getCraftRecap(totalcraft);
						player.sendMessage("§eCraft contents :§r " + recap);
						//Rappel commandes
						player.sendMessage("§7§l§m-----");
					}
				}
				//supprimer le dernier
				else if(msg.equalsIgnoreCase("removelast")){
					ArrayList<ItemStack> globalcraft = craft.get(player).get(mode);
					if(globalcraft != null){
						ItemStack deleted = globalcraft.get(globalcraft.size()-1);
						//Supression du dernier item ajouté
						globalcraft.remove(globalcraft.size()-1);
						HashMap<String,ArrayList<ItemStack>> totalcraft = craft.get(player);
						totalcraft.put(mode, globalcraft);
						craft.put(player, totalcraft);
						player.sendMessage("§7§l§m-----");
						//Affichage du nom
						String name = getName(deleted);
						player.sendMessage("§7Item removed : §c§m" + name);
						//Recap
						String recap = getCraftRecap(totalcraft);
						player.sendMessage("§eCraft contents :§r " + recap);
						//Rappel commandes
						player.sendMessage("§7§l§m-----");
					}
				}
				//passer à l'étape suivante
				else if(msg.equalsIgnoreCase("next")){
					Integer phase = editor.get(player);
					if(phase == 1){ //Passage à l'éditeur de résultat
						ArrayList<ItemStack> globalcraft = craft.get(player).get("craft");
						if(!globalcraft.isEmpty()){
							player.sendMessage("§d§l➤ Making the craft rewards");
							player.sendMessage("§eSelect items in your §d§omain§d hand§e "
									+ "and write §2§ladd§e on chat.");
							player.sendMessage("§f§l§m-----");
							player.sendMessage("§7Write §oexit§7 to leave this editor.");
							player.sendMessage("§7Write §cremovelast§7 to remove the last item you added.");
							player.sendMessage("§eWrite §2next§e to go to the next step.");
							player.sendMessage("§d§l§m-----");
							player.sendMessage("§aCraft contents :§r " + getCraftRecap(craft.get(player)));
							editor.put(player, 2);
						} else {
							player.sendMessage("§cAdd items to the §lcraft§c before going to the next step !");
						}
					} else if(phase == 2){ //Enregistrement
						ArrayList<ItemStack> globalcraft = craft.get(player).get("craft");
						if(!globalcraft.isEmpty()){
							player.sendMessage("§a§l➤ Craft finished and saved. ;)");
							player.sendMessage("§eCraft created :§r " + getCraftRecap(craft.get(player)));
							addCraft(craft.get(player));
							craft.remove(player);
							editor.remove(player);
							reloadConfig();
						} else {
							player.sendMessage("§cAdd items to §lresult of the craft§c before going to the next step !");
						}
					}
				} else {
					player.sendMessage("§7§l§m-----");
					player.sendMessage("§bWrite §2§ladd§b to add items.");
					player.sendMessage("§7Write §oexit§7 to leave this editor.");
					player.sendMessage("§7Write §cremovelast§7 to remove the last item you added.");
					player.sendMessage("§eWrite §2next§e to go to the next step.");
					player.sendMessage("§7§l§m-----");
				}
			} else {
				if(editor.get(player) == 3){
					//Craft entré via commande, demande de confirmation
					if(msg.equalsIgnoreCase("yes")){
						player.sendMessage("§a§l➤ Craft finished and saved. ;)");
						player.sendMessage("§eCraft created :§r " + getCraftRecap(craft.get(player)));
						addCraft(craft.get(player));
						craft.remove(player);
						editor.remove(player);
						reloadConfig();
					} else if(msg.equalsIgnoreCase("no")){
						craft.remove(player);
						editor.remove(player);
						player.sendMessage("§cCraft canceled.");
					}
				}
			}
		}
		
	}
	
	HashMap<Player,ArrayList<Item>> inCaul = new HashMap<Player,ArrayList<Item>>();
	ArrayList<Player> craftProc = new ArrayList<Player>();
	HashMap<Player,Location> caulLoc = new HashMap<Player,Location>();
	HashMap<Player,ArrayList<ItemStack>> inCaulFin = new HashMap<Player,ArrayList<ItemStack>>();
	
	public static Entity[]  getNearbyEntities(Location l, int radius){
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
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			@SuppressWarnings("deprecation")
			public void run(){
				Location itemLoc = item.getLocation();
				//Si l'item est sur le sol
				if(item.isOnGround()){
					//Si l'item est dans un chadron
					if(itemLoc.getBlock().getType() == Material.CAULDRON){
						Block caul = itemLoc.getBlock();
						if(!caulLoc.containsKey(player))
							caulLoc.put(player, caul.getLocation());
						//Si le chaudron contient de l'eau
						if(caul.getData() > 0){
							//Particule dans le chadron
							for(int ii = 0; ii<100;ii++){
								player.getWorld().playEffect(itemLoc, Effect.POTION_SWIRL, 1);
							}
							//Son
							player.getWorld().playSound(player.getLocation(),Sound.BLOCK_BREWING_STAND_BREW,1, 0);
							//Ajout dans la liste du chaudron
							ArrayList<Item> itemToAdd = new ArrayList<Item>();
							if(inCaul.containsKey(player))
								itemToAdd = inCaul.get(player);
							itemToAdd.add(item);
							inCaul.put(player, itemToAdd);
						}
					}
				}
			}
		},20);
		if(!craftProc.contains(player)){
			craftProc.add(player);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				@SuppressWarnings({ "unchecked", "deprecation" })
				public void run(){
					if(!caulLoc.containsKey(player))
						return;
					//Suppression des valeurs.
					clearVar(player);
					ArrayList<Item> itemsInCaul = inCaul.get(player);
					int count = 0;
					for(Entity entIn : caulLoc.get(player).getChunk().getEntities()){
						if(entIn.getType() == EntityType.DROPPED_ITEM){
							ItemStack itms = ((Item)entIn).getItemStack();
							if(entIn.getLocation().getBlock().getType() == Material.CAULDRON){
								count++;
								//Ajout dans la liste du chaudron
								ArrayList<ItemStack> itemToAdd = new ArrayList<ItemStack>();
								if(inCaulFin.containsKey(player))
									itemToAdd = inCaulFin.get(player);
								itemToAdd.add(itms);
								inCaulFin.put(player, itemToAdd);
							}
						}
					}
					//On éxécute le craft		
					if(count == itemsInCaul.size()) {
						//On récup tous les crafts
						ArrayList<HashMap<String,ArrayList<ItemStack>>> allcrafts = new ArrayList<HashMap<String,ArrayList<ItemStack>>>();
						if(getConfig().isSet("Crafts"))
							allcrafts = (ArrayList<HashMap<String, ArrayList<ItemStack>>>) getConfig().get("Crafts");
						//Un par un on voit s'ils correspondent
						HashMap<String,ArrayList<ItemStack>> actualcraft = new HashMap<String,ArrayList<ItemStack>>();
						boolean stop = false;
						for(HashMap<String,ArrayList<ItemStack>> ecraft : allcrafts){
							if(stop == false){
								ArrayList<ItemStack> need = ecraft.get("craft");
								ArrayList<ItemStack> droped = inCaulFin.get(player);
								actualcraft = ecraft;
								//S'ils sont similaires on arrête la boucle
								if(droped.containsAll(need)){
									stop = true;
								}
							}
						}
						//Centre du cauldron
						Location cauldronlocation = caulLoc.get(player).add(0.5, 0, 0.5);
						if(stop == true){
							//Craft valide
							//On supprime les items dans le chaudron
							for(Entity ent : getNearbyEntities(cauldronlocation,1)){
								if(ent instanceof Item){
									Item itm = (Item)ent;
									if(actualcraft.get("craft").contains(itm.getItemStack())){
										ent.remove();
									}
								}
							}
							//Résultats du craft
							for(ItemStack itemresult : actualcraft.get("result"))
								cauldronlocation.getWorld().dropItemNaturally(cauldronlocation.clone().add(0, 1, 0), itemresult);
							//Effets graphiques
							for(int ii = 0; ii<100;ii++){
								player.getWorld().playEffect(cauldronlocation, Effect.FIREWORKS_SPARK, 1);
							}
							player.getWorld().playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1, 0);
							Block caul = caulLoc.get(player).getBlock();
							byte caulData = caul.getData();
							caul.setData((byte) (caulData-1));
						} else {
							//Craft invalide
							player.getWorld().playSound(player.getLocation(),Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,1, 0);
							for(int ii = 0; ii<100;ii++){
								player.getWorld().playEffect(cauldronlocation, Effect.WITCH_MAGIC, 1);
							}
						}
					}
				}
			},100);
		}	 
	}
	
	public void clearVar(final Player player){
		 Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			 public void run(){
				 inCaul.remove(player);
				 craftProc.remove(player);
				 caulLoc.remove(player);
				 inCaulFin.remove(player);
			 }
		 },4);
	 }
}
