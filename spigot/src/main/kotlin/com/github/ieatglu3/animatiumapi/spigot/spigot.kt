package com.github.ieatglu3.animatiumapi.spigot

import com.github.ieatglu3.animatiumapi.AnimatiumAPI
import com.github.ieatglu3.animatiumapi.AnimatiumPlayer
import com.github.ieatglu3.animatiumapi.AnimatiumPlayerRegistry
import com.github.ieatglu3.animatiumapi.protocol.AnimatiumPayloadRegistry
import com.github.ieatglu3.animatiumapi.protocol.serverbound.PayloadServerboundInfo
import com.github.ieatglu3.animatiumapi.spigot.event.AnimatiumPlayerInfoEvent
import com.github.ieatglu3.animatiumapi.spigot.event.AnimatiumPlayerQuitEvent
import com.github.ieatglu3.payloadcrafter.CustomPayloadListener
import com.github.ieatglu3.payloadcrafter.PayloadEvent
import com.github.ieatglu3.payloadcrafter.ServerboundCustomPayload
import org.bukkit.Bukkit
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.plugin.java.JavaPlugin
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import java.util.logging.Logger

class InternalPayloadListener(private val playerMap: ConcurrentHashMap<UUID, AnimatiumPlayer>) : CustomPayloadListener(AnimatiumPayloadRegistry.handle)
{
  override fun onPayloadReceive(event: PayloadEvent<ServerboundCustomPayload>)
  {
    val payload = event.payload()
    val user = event.user()
    if (payload.type() != PayloadServerboundInfo.TYPE)
      return
    if (this.playerMap.containsKey(user.uuid))
      return
    runSync {
      val bukkitPlayer = Bukkit.getPlayer(user.uuid)
      if (bukkitPlayer != null)
        fireEvent(AnimatiumPlayerInfoEvent(AnimatiumPlayer(user, (payload as PayloadServerboundInfo).version), bukkitPlayer))
    }
  }
}

class InternalBukkitListener(private val playerMap: ConcurrentHashMap<UUID, AnimatiumPlayer>) : Listener
{

  @EventHandler
  fun onPlayerInfo(event: AnimatiumPlayerInfoEvent)
  {
    val animatiumPlayer = event.getAnimatiumPlayer()
    this.playerMap[animatiumPlayer.uuid()] = animatiumPlayer
  }

  @EventHandler
  fun onPlayerQuit(event: PlayerQuitEvent)
  {
    val animatiumPlayer = this.playerMap.remove(event.player.uniqueId)
    val bukkitPlayer = event.player
    if (animatiumPlayer != null)
      fireEvent(AnimatiumPlayerQuitEvent(animatiumPlayer, bukkitPlayer))
  }
}

class AnimatiumAPISpigot : AnimatiumAPI, JavaPlugin()
{

  private val playerMap = ConcurrentHashMap<UUID, AnimatiumPlayer>()
  private val playerManager = AnimatiumPlayerRegistry(this.playerMap)
  private val internalPayloadListener = InternalPayloadListener(this.playerMap)

  override fun onEnable()
  {
    AnimatiumAPI.initialize(this)
    this.internalPayloadListener.startListening()
    this.server.pluginManager.registerEvents(InternalBukkitListener(this.playerMap), this)
  }

  override fun onDisable()
  {
    AnimatiumAPI.deinitialize()
    this.internalPayloadListener.stopListening()
    HandlerList.unregisterAll(this)
  }

  override fun logger(): Logger = this.logger
  override fun players(): AnimatiumPlayerRegistry = this.playerManager
}

private fun fireEvent(event: Event) = Bukkit.getServer().pluginManager.callEvent(event)
private fun runSync(block: () -> Unit) {
  if (Bukkit.isPrimaryThread())
    block()
  else
    Bukkit.getScheduler().runTask(AnimatiumAPI.get() as JavaPlugin, block)
}