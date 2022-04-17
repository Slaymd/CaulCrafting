package fr.dariusmtn.caulcrafting.events;

import java.util.ArrayList;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class CaulCraftFailEvent extends Event implements Cancellable{
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	
	private ArrayList<ItemStack> items = null;
	private Player player = null;
	private Block cauldron = null;
	
	private Particle particle = null;
	private Sound sound = null;
	
	public CaulCraftFailEvent(ArrayList<ItemStack> items, Player player, Block cauldron, Particle particle, Sound sound) {
		this.setItems(items);
		this.setPlayer(player);
		this.setCauldron(cauldron);
		this.setParticle(particle);
		this.setSound(sound);
	}
	 
    @Override //La classe Event nous oblige à surcharger la méthode getHandler()
    public HandlerList getHandlers() {
        return handlers;
    }
 
    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @param player the player to set
	 */
	public void setPlayer(Player player) {
		this.player = player;
	}

	/**
	 * @return the cauldron
	 */
	public Block getCauldron() {
		return cauldron;
	}

	/**
	 * @param cauldron the cauldron to set
	 */
	public void setCauldron(Block cauldron) {
		this.cauldron = cauldron;
	}

	/**
	 * @return the particle
	 */
	public Particle getParticle() {
		return particle;
	}

	/**
	 * @param particle the particle to set
	 */
	public void setParticle(Particle particle) {
		this.particle = particle;
	}

	/**
	 * @return the sound
	 */
	public Sound getSound() {
		return sound;
	}

	/**
	 * @param sound the sound to set
	 */
	public void setSound(Sound sound) {
		this.sound = sound;
	}

	/**
	 * @return the items
	 */
	public ArrayList<ItemStack> getItems() {
		return items;
	}

	/**
	 * @param items the items to set
	 */
	public void setItems(ArrayList<ItemStack> items) {
		this.items = items;
	}

}
