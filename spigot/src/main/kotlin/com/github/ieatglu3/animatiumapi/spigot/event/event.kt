package com.github.ieatglu3.animatiumapi.spigot.event

import com.github.ieatglu3.animatiumapi.AnimatiumPlayer
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.HandlerList

/**
 * Event called when an Animatium player sends their info packet to the server.
 * @param player the Animatium player
 */
class AnimatiumPlayerInfoEvent(private val player: AnimatiumPlayer, private val bukkitPlayer: Player) : Event()
{
  companion object {
    private val handlers = HandlerList()
    @JvmStatic
    fun getHandlerList(): HandlerList = handlers
  }

  /**
   * Gets the Animatium player
   * @return the Animatium player
   */
  fun getAnimatiumPlayer(): AnimatiumPlayer = this.player

  /**
   * Gets the corresponding Bukkit player
   * @return the Bukkit player
   */
  fun getPlayer(): Player = this.bukkitPlayer

  override fun getHandlers(): HandlerList = AnimatiumPlayerInfoEvent.handlers
}

/**
 * Event called when an Animatium player leaves the server.
 * @param animatiumPlayer the Animatium player
 */
class AnimatiumPlayerQuitEvent(private val animatiumPlayer: AnimatiumPlayer, private val bukkitPlayer: Player) : Event()
{
  companion object {
    private val handlers = HandlerList()
    @JvmStatic
    fun getHandlerList(): HandlerList = handlers
  }

  /**
   * Gets the Animatium player
   * @return the Animatium player
   */
  fun getAnimatiumPlayer(): AnimatiumPlayer = this.animatiumPlayer

  /**
   * Gets the corresponding Bukkit player
   * @return the Bukkit player
   */
  fun getPlayer(): Player = this.bukkitPlayer

  override fun getHandlers(): HandlerList = AnimatiumPlayerQuitEvent.handlers
}