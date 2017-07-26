package fr.dariusmtn.caulcrafting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
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
import mkremins.fanciful.FancyMessage;
import net.md_5.bungee.api.ChatColor;

public class CaulCrafting extends JavaPlugin implements Listener {
	
	public ConfigUpdate configUpdate = new ConfigUpdate(this);
	public Config configUtils = new Config(this);
	public Language lang = new Language(this);
	public CraftStorage craftStorage = new CraftStorage(this);
	public CraftFormatting craftFormat = new CraftFormatting(this);
	
	//Languages availables list 
	HashMap<String, String> languagesAvailable = new HashMap<String,String>();
	
	@SuppressWarnings({ "unused" })
	public void onEnable(){
		Bukkit.getPluginManager().registerEvents(this, this);
		//Setup languages
		languagesAvailable.put("en", "English");
		languagesAvailable.put("fr", "Français");
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
		MetricsLite metrics = new MetricsLite(this);
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
		}
		return itemsname != null;
	}
	
	HashMap<Player,Integer> editor = new HashMap<Player,Integer>();
	HashMap<Player,HashMap<String,ArrayList<ItemStack>>> craft = new HashMap<Player,HashMap<String,ArrayList<ItemStack>>>();
	
	@SuppressWarnings({ "deprecation" })
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		//Config command (internal)
		if(cmd.getName().equalsIgnoreCase("caulcraftingconfig")) {
			if(args.length > 0) {
				if(args[0].equalsIgnoreCase("setlang")) {
					if(args.length == 2 && languagesAvailable.containsKey(args[1])) {
						String oldlang = lang.getExactLanguage();
						lang.setLanguage(args[1]);
						if(sender instanceof Player && oldlang.equalsIgnoreCase("default")) {
							Player player = (Player)sender;
							player.playSound(player.getLocation(), Sound.BLOCK_NOTE_PLING, 1, 0);
							player.sendMessage("§d§l➤ " + lang.getTranslation("welcome_lets_start_something"));
							new FancyMessage(" §e•§2§l " + lang.getTranslation("welcome_create_craft")).tooltip("§b" + lang.getTranslation("general_click_here")).command("/caulcrafting create").send(player);
							new FancyMessage(" §e•§2 " + lang.getTranslation("welcome_main_command")).tooltip("§b" + lang.getTranslation("general_click_here")).command("/caulcrafting").send(player);
						}
					}
					return false;
				}
				return false;
			}
			if(sender instanceof Player) {
				((Player)sender).performCommand("caulcrafting");
			}
			return false;
		}
		if(cmd.getName().equalsIgnoreCase("caulcrafting")){
			//Joueur réel
			if(sender instanceof Player){
				Player player = (Player)sender;
				//Language selection
				if(lang.getExactLanguage().equalsIgnoreCase("default")) {
					if(player.isOp()){
						player.sendMessage("§d§l➤ Thank you for downloading §b§lCaulCrafting§d§l !");
						player.sendMessage("§eFor a better experience, please §bselect§e by clicking with mouse your §bcorresponding §llanguage§e :");
						HashMap<String, String> langs = languagesAvailable;
						for(String loc : langs.keySet()) {
							new FancyMessage(" §e•§2 " + langs.get(loc))
							.tooltip("§b" + lang.getTranslation("welcome_language_pickup_tooltip", loc))
							.command("/caulcraftingconfig setlang " + loc)
							.send(player);
						}
						return true;
					}
					player.sendMessage("§cThe plugin need to be initialized by an OP player");
					return false;
				} else {
					//Sous commandes
					if(args.length > 0){
						if(args[0].equalsIgnoreCase("create")){
							if(player.hasPermission("caulcrafting.admin.create")){
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
																	player.sendMessage("§c" + lang.getTranslation("craftmaking_wrong") + eitm);
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
													player.sendMessage("§d§l➤ " + lang.getTranslation("craftmaking_thats_right"));
													player.sendMessage("§b" + lang.getTranslation("craftmaking_craft_typed") + "§r " + craftFormat.getCraftRecap(craftcmd));
													player.sendMessage("§e" + lang.getTranslation("craftmaking_craft_confirm_cmd"));
													editor.put(player, 3);
													craft.put(player, craftcmd);
													return true;
												}
											}
										} else {
											player.sendMessage("§c" + lang.getTranslation("craftmaking_craft_need_confirm"));
											player.sendMessage("§e" + lang.getTranslation("craftmaking_craft_confirm_cmd"));
											return false;
										}
									}
									player.sendMessage("§e" + lang.getTranslation("craftmaking_example_cmd_1"));
									player.sendMessage("§b" + lang.getTranslation("craftmaking_example_cmd_2"));
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
										player.sendMessage("§d§l➤ " + lang.getTranslation("craftmaking_step_1"));
										player.sendMessage("§e" + lang.getTranslation("craftmaking_step_1_explain"));
										player.sendMessage("§f§l§m-----");
										player.sendMessage("§7" + lang.getTranslation("craftmaking_editor_cmd_exit"));
										player.sendMessage("§7" + lang.getTranslation("craftmaking_editor_cmd_removelast"));
										player.sendMessage("§e" + lang.getTranslation("craftmaking_editor_cmd_next"));
										player.sendMessage("§d§l§m-----");
										return true;
									}
									player.sendMessage("§c" + lang.getTranslation("craftmaking_already_in"));
									return false;
								}
							}
							player.sendMessage("§c" + lang.getTranslation("general_do_not_permission"));
							return false;
						}
						else if(args[0].equalsIgnoreCase("list")){
							if(player.hasPermission("caulcrafting.admin.list")) {
								//Intervalle d'action (pagination)
								int page = 0;
								if(args.length > 1){
									String p = args[1];
									try{
										page = Integer.parseInt(p);
										if(page < 0)
											page = 0;
									}catch (Exception e){
										player.sendMessage("§c" + lang.getTranslation("craftlist_no_display"));
										return true;
									}
								}
								int mincraft = 10*page;
								int maxcraft = mincraft + 10;
								//On récup les crafts
								ArrayList<HashMap<String,ArrayList<ItemStack>>> craftlist = craftStorage.getCrafts();
								//On affiche la liste
								int count = 0;
								if(craftlist.size()-1 >= mincraft){
									player.sendMessage("§d§l§m-----§b(" + lang.getTranslation("craftlist_page_nb").replace("%number%", "" + page) + ")");
									for(HashMap<String,ArrayList<ItemStack>> crafts : craftlist){
										if(count >= mincraft && count <= maxcraft){
											player.sendMessage("§6§l" + count + ".§r " + craftFormat.getCraftRecap(crafts));
										}
										count++;
									}
									if(maxcraft < craftlist.size()-1){
										player.sendMessage("§d§l§m-----§b(" + lang.getTranslation("craftlist_next_page") + " §3/... list " + (page+1) + "§b)");
									} else {
										player.sendMessage("§d§l§m-----§b(" + lang.getTranslation("craftlist_list_finished") + ")");
									}
								} else {
									player.sendMessage("§c" + lang.getTranslation("craftlist_no_display"));
								}
								return true;
							}
							player.sendMessage("§c" + lang.getTranslation("general_do_not_permission"));
							return false;
						}
						else if(args[0].equalsIgnoreCase("remove")){
							if(player.hasPermission("caulcrafting.admin.remove")) {
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
									//Getting all crafts
									ArrayList<HashMap<String,ArrayList<ItemStack>>> craftlist = craftStorage.getCrafts();
									//If craft number is lower than the higher craft number
									if(craftlist.size()-1 >= nb){
										HashMap<String, ArrayList<ItemStack>> specraft = craftlist.get(nb);
										player.sendMessage("§7" + lang.getTranslation("craftremove_removed") + " §c§m" + ChatColor.stripColor(craftFormat.getCraftRecap(specraft)));
										//Removing
										craftStorage.removeCraft(nb);
										//Reload config ;)
										reloadConfig();
										return true;
									}
								}
								player.sendMessage("§c" + lang.getTranslation("craftremove_error"));
								return true;
							}
							player.sendMessage("§c" + lang.getTranslation("general_do_not_permission"));
							return false;
						}
					}
					player.sendMessage("§b§lCaulCrafting v" + getDescription().getVersion() + " §b" + lang.getTranslation("maincmd_by"));
					player.sendMessage("§7§l§m-----");
					player.sendMessage("§6§l" + lang.getTranslation("maincmd_create").toUpperCase());
					player.sendMessage("§e/caulcrafting §2create §7§o" + lang.getTranslation("maincmd_create_easy"));
					player.sendMessage("§e§o/caulcrafting §2§ocreate §b§o" + lang.getTranslation("maincmd_create_cmd_args") + " §7§o" + lang.getTranslation("maincmd_create_fast"));
					player.sendMessage("§7§l" + lang.getTranslation("maincmd_list").toUpperCase());
					player.sendMessage("§e/caulcrafting §blist");
					player.sendMessage("§7§l" + lang.getTranslation("maincmd_remove").toUpperCase());
					player.sendMessage("§e/caulcrafting §cremove " + lang.getTranslation("maincmd_remove_args"));
					player.sendMessage("§7§l§m-----");
					player.sendMessage("§d§l" + lang.getTranslation("maincmd_discord"));
					player.sendMessage("§bhttps://discord.gg/w628upr");
					return false;
				}
			}
			sender.sendMessage("§c" + lang.getTranslation("maincmd_robot"));
			return false;
		}
		return false;
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
					player.sendMessage("§c" + lang.getTranslation("craftmaking_editor_left"));
				}
				//ajouter un craft
				else if(msg.equalsIgnoreCase("add")){
					ItemStack item = player.getInventory().getItemInMainHand();
					if(item != null){
						player.sendMessage("§7§l§m-----");
						//Affichage du nom
						String name = craftFormat.getName(item);
						player.sendMessage("§7" + lang.getTranslation("craftmaking_item_added") + "§a " + name);
						//Ajout définitif
						ArrayList<ItemStack> globalcraft = craft.get(player).get(mode);
						globalcraft.add(item);
						HashMap<String,ArrayList<ItemStack>> totalcraft = craft.get(player);
						totalcraft.put(mode, globalcraft);
						craft.put(player, totalcraft);
						//Recap
						String recap = craftFormat.getCraftRecap(totalcraft);
						player.sendMessage("§e" + lang.getTranslation("craftmaking_craft_contents") + "§r " + recap);
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
						String name = craftFormat.getName(deleted);
						player.sendMessage("§7" + lang.getTranslation("craftmaking_item_removed") + " §c§m" + name);
						//Recap
						String recap = craftFormat.getCraftRecap(totalcraft);
						player.sendMessage("§e" + lang.getTranslation("craftmaking_craft_contents") + "§r " + recap);
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
							player.sendMessage("§d§l➤ " + lang.getTranslation("craftmaking_step_2"));
							player.sendMessage("§e" + lang.getTranslation("craftmaking_step_2_explain"));
							player.sendMessage("§f§l§m-----");
							player.sendMessage("§7" + lang.getTranslation("craftmaking_editor_cmd_exit"));
							player.sendMessage("§7" + lang.getTranslation("craftmaking_editor_cmd_removelast"));
							player.sendMessage("§e" + lang.getTranslation("craftmaking_editor_cmd_next"));
							player.sendMessage("§d§l§m-----");
							player.sendMessage("§a" + lang.getTranslation("craftmaking_craft_contents") + "§r " + craftFormat.getCraftRecap(craft.get(player)));
							editor.put(player, 2);
						} else {
							player.sendMessage("§c" + lang.getTranslation("craftmaking_step_1_add_items"));
						}
					} else if(phase == 2){ //Enregistrement
						ArrayList<ItemStack> globalcraft = craft.get(player).get("craft");
						if(!globalcraft.isEmpty()){
							player.sendMessage("§a§l➤ " + lang.getTranslation("craftmaking_step_final"));
							player.sendMessage("§e" + lang.getTranslation("craftmaking_craft_created") + "§r " + craftFormat.getCraftRecap(craft.get(player)));
							craftStorage.addCraft(craft.get(player));
							craft.remove(player);
							editor.remove(player);
							reloadConfig();
						} else {
							player.sendMessage("§c" + lang.getTranslation("craftmaking_step_2_add_items"));
						}
					}
				} else {
					player.sendMessage("§7§l§m-----");
					player.sendMessage("§b" + lang.getTranslation("craftmaking_editor_cmd_add"));
					player.sendMessage("§7" + lang.getTranslation("craftmaking_editor_cmd_exit"));
					player.sendMessage("§7" + lang.getTranslation("craftmaking_editor_cmd_removelast"));
					player.sendMessage("§e" + lang.getTranslation("craftmaking_editor_cmd_next"));
					player.sendMessage("§7§l§m-----");
				}
			} else {
				if(editor.get(player) == 3){
					//Craft entré via commande, demande de confirmation
					if(msg.equalsIgnoreCase("yes")){
						player.sendMessage("§a§l➤ " + lang.getTranslation("craftmaking_step_final"));
						player.sendMessage("§e" + lang.getTranslation("craftmaking_craft_created") + "§r " + craftFormat.getCraftRecap(craft.get(player)));
						craftStorage.addCraft(craft.get(player));
						craft.remove(player);
						editor.remove(player);
						reloadConfig();
					} else if(msg.equalsIgnoreCase("no")){
						craft.remove(player);
						editor.remove(player);
						player.sendMessage("§c" + lang.getTranslation("craftmaking_canceled"));
					}
				}
			}
		}
		
	}
	
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
		//EDITOR BY DROPPING
		if(editor.containsKey(player)){
			e.setCancelled(true);
			if(editor.get(player) < 3){
				String mode = "craft";
				if(editor.get(player) == 2)
					mode = "result";
				if(item != null){
					player.sendMessage("§7§l§m-----");
					//Affichage du nom
					String name = craftFormat.getName(item.getItemStack());
					player.sendMessage("§7" + lang.getTranslation("craftmaking_item_added") + "§a " + name);
					//Ajout définitif
					ArrayList<ItemStack> globalcraft = craft.get(player).get(mode);
					globalcraft.add(item.getItemStack());
					HashMap<String,ArrayList<ItemStack>> totalcraft = craft.get(player);
					totalcraft.put(mode, globalcraft);
					craft.put(player, totalcraft);
					//Recap
					String recap = craftFormat.getCraftRecap(totalcraft);
					player.sendMessage("§e" + lang.getTranslation("craftmaking_craft_contents") + "§r " + recap);
					//Rappel commandes
					player.sendMessage("§7§l§m-----");
					e.setCancelled(true);
					return;
				}
				
			}
		}
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			@SuppressWarnings("deprecation")
			public void run(){
				Location itemLoc = item.getLocation();
				//Si l'item est sur le sol
				if(item.isOnGround()){
					//Si l'item est dans un chadron
					if(itemLoc.getBlock().getType() == Material.CAULDRON){
						if(player.hasPermission("caulcrafting.use")) {
							Block caul = itemLoc.getBlock();
							if(!caulLoc.containsKey(player))
								caulLoc.put(player, caul.getLocation());
							//Si le chaudron contient de l'eau
							if(caul.getData() > 0){
								sendDebug(player,"STEP1 a/b - detecting dropping into cauldron " + item.getItemStack().getType());
								//Particule dans le chadron
								sendDebug(player,"STEP1 b/b - sending particle and sound");
								itemLoc.getWorld().spawnParticle(Particle.SPELL_MOB, itemLoc, 100);
								//Son
								player.getWorld().playSound(player.getLocation(),Sound.BLOCK_BREWING_STAND_BREW,1, 0);
							}
						}
					}
				}
			}
		},20);
		if(!craftProc.contains(player)){
			craftProc.add(player);
			Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
				@SuppressWarnings({ "deprecation" })
				public void run(){
					if(!caulLoc.containsKey(player))
						return;
					//Suppression des valeurs.
					clearVar(player);
					int count = 0;
					sendDebug(player,"STEP2 a/c - verifying cauldron content...");
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
								sendDebug(player,"STEP2 b/c - items in cauldron +" + itms.getType().toString());
							}
						}
					}
					//On éxécute le craft
					sendDebug(player,"STEP2 c/c - cauldron content ok (" + count + " items)");
					//On récup tous les crafts
					ArrayList<HashMap<String,ArrayList<ItemStack>>> allcrafts = craftStorage.getCrafts();
					//Un par un on voit s'ils correspondent
					HashMap<String,ArrayList<ItemStack>> actualcraft = new HashMap<String,ArrayList<ItemStack>>();
					boolean stop = false;
					sendDebug(player,"STEP3 a/a - checking the craft");
					for(HashMap<String,ArrayList<ItemStack>> ecraft : allcrafts){
						if(stop == false){
							ArrayList<ItemStack> need = ecraft.get("craft");
							ArrayList<ItemStack> droped = inCaulFin.get(player);
							actualcraft = ecraft;
							//S'ils sont similaires on arrête la boucle
							if(droped.containsAll(need)){
								stop = true;
								sendDebug(player,"STEP4a a/d - craft detected : " + ecraft.toString());
							}
						}
					}
					//Centre du cauldron
					Location cauldronlocation = caulLoc.get(player).add(0.5, 0, 0.5);
					if(stop == true){
						sendDebug(player,"STEP4a b/d - removing cauldrons items (into)");
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
						sendDebug(player,"STEP4a c/d - sending rewards");
						//Craft reward
						for(ItemStack itemresult : actualcraft.get("result"))
							cauldronlocation.getWorld().dropItemNaturally(cauldronlocation.clone().add(0, 1, 0), itemresult);
						//Particles
						sendDebug(player,"STEP4a d/d - sending particles");
						cauldronlocation.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, cauldronlocation, 40);
						player.getWorld().playSound(player.getLocation(),Sound.ENTITY_PLAYER_LEVELUP,1, 0);
						//Water layer
						if(!player.hasPermission("caulcrafting.nowaterconsume")) {
							sendDebug(player,"STEP4a* a/a - modifying water layers");
							Block caul = caulLoc.get(player).getBlock();
							byte caulData = caul.getData();
							caul.setData((byte) (caulData-1));
						}
					} else {
						//Craft invalide
						sendDebug(player,"STEP4b a/b - detecting wrong process");
						player.getWorld().playSound(player.getLocation(),Sound.ITEM_BOTTLE_FILL_DRAGONBREATH,1, 0);
						cauldronlocation.getWorld().spawnParticle(Particle.SPELL_WITCH, cauldronlocation, 100);
						sendDebug(player,"STEP4b b/b - wrong process succeed");
						
					}
				}
			},100);
		}	 
	}
	
	public void sendDebug(Player player, String msg) {
		if(getConfig().getBoolean("debug_message") == true) {
			getLogger().info("CaulCrafting DEBUG " + player.getName() + ": " + msg);
		}
	}
	
	public void clearVar(final Player player){
		 Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable(){
			 public void run(){
				 sendDebug(player,"STEP5 a/b - starting removing vars");
				 craftProc.remove(player);
				 caulLoc.remove(player);
				 inCaulFin.remove(player);
				 sendDebug(player,"STEP5 b/b - removing vars succeed");
			 }
		 },4);
	 }
}
