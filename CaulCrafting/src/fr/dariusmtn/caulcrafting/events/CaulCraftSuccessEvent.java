package fr.dariusmtn.caulcrafting.events;

import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import fr.dariusmtn.caulcrafting.CraftArray;

public class CaulCraftSuccessEvent extends Event implements Cancellable{
	
	private static final HandlerList handlers = new HandlerList();
	private boolean cancelled = false;
	
	private CraftArray craft = null;
	private Player player = null;
	private Block cauldron = null;
	
	private Particle particle = null;
	private Sound sound = null;
	//default
	private boolean itemDeleting = true;
	private boolean sendingRewards = true;
	private boolean editingWaterLayer = true;
	
	public CaulCraftSuccessEvent(CraftArray craft, Player player, Block cauldron, Particle particle, Sound sound) {
		this.craft = craft;
		this.player = player;
		this.cauldron = cauldron;
		this.particle = particle;
		this.setSound(sound);
	}
	 
    @Override
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
	 * @return the craft
	 */
	public CraftArray getCraft() {
		return craft;
	}

	/**
	 * @param craft the craft to set
	 */
	public void setCraft(CraftArray craft) {
		this.craft = craft;
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
	 * @return if delete item or not
	 */
	public boolean isItemDeleting() {
		return itemDeleting;
	}

	/**
	 * @param itemDeleting delete item or not ?
	 */
	public void setItemDeleting(boolean itemDeleting) {
		this.itemDeleting = itemDeleting;
	}

	/**
	 * @return if sending rewards or not
	 */
	public boolean isSendingRewards() {
		return sendingRewards;
	}

	/**
	 * @param sendingRewards sending rewards or not ?
	 */
	public void setSendingRewards(boolean sendingRewards) {
		this.sendingRewards = sendingRewards;
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
	 * @return if editing cauldron water layer or not
	 */
	public boolean isEditingWaterLayer() {
		return editingWaterLayer;
	}

	/**
	 * @param editingWaterLayer editing cauldron water layer or not ?
	 */
	public void setEditingWaterLayer(boolean editingWaterLayer) {
		this.editingWaterLayer = editingWaterLayer;
	}

}
