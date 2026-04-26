package com.github.ieatglu3.animatiumapi

import com.github.ieatglu3.animatiumapi.protocol.clientbound.PayloadClientboundSetServerFeatures
import com.github.retrooper.packetevents.protocol.player.User
import java.lang.invoke.MethodHandles
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import java.util.logging.Logger

/**
 * Player registry for animatium players (thread-safe)
 */
class AnimatiumPlayerRegistry(private val players: ConcurrentHashMap<UUID, AnimatiumPlayer>): Iterable<AnimatiumPlayer>
{
  /**
   * Gets a player by their UUID
   * @param uuid the player UUID
   * @return the animatium player
   */
  fun get(uuid: UUID): Optional<AnimatiumPlayer> = Optional.ofNullable(this.players[uuid])

  /**
   * Gets a player by their UUID, or null
   * @param uuid the player UUID
   * @return the animatium player, or null if no player with the given UUID hasn't sent animatium info to the server
   */
  fun getOrNull(uuid: UUID): AnimatiumPlayer? = this.players[uuid]

  /**
   * Checks if a player with the given UUID has sent animatium info to the server
   * @param uuid the player UUID
   */
  fun hasAnimatiumInfo(uuid: UUID): Boolean = this.players.containsKey(uuid)

  override fun iterator(): Iterator<AnimatiumPlayer> = this.players.values.iterator()
}

interface AnimatiumAPI
{

  companion object {

    private val instance: AtomicReference<AnimatiumAPI?> = AtomicReference(null)

    fun initialize(platform: AnimatiumAPI?)
    {
      if (!this.instance.compareAndSet(null, platform))
        throw IllegalStateException("Platform instance is already initialized")
    }

    fun deinitialize()
    {
      this.instance.set(null)
    }

    /**
     * Gets the platform instance
     * @throws IllegalStateException if the instance is uninitialized
     * @return platform instance
     */
    @JvmStatic
    fun get(): AnimatiumAPI
    {
      val platform = this.instance.get()
      if (platform != null) return platform
      throw IllegalStateException("Platform is uninitialized")
    }

    /**
     * Checks if the platform instance is initialized
     * @return true if initialized, false otherwise
     */
    @JvmStatic
    fun isInitialized(): Boolean = this.instance.get() != null
  }

  /**
   * Gets a player by their UUID
   * @param uuid the player UUID
   * @return the animatium player, or an empty optional if no player with the given UUID hasn't sent animatium info to the server
   */
  fun getPlayer(uuid: UUID): Optional<AnimatiumPlayer> = this.players().get(uuid)

  /**
   * Gets a player by their UUID, or null
   * @param uuid the player UUID
   * @return the animatium player, or null if no player with the given UUID hasn't sent animatium info to the server
   */
  fun getPlayerOrNull(uuid: UUID): AnimatiumPlayer? = this.players().getOrNull(uuid)

  /**
   * Checks if a player with the given UUID has sent animatium info to the server
   * @param uuid the player UUID
   */
  fun hasAnimatiumInfo(uuid: UUID): Boolean = this.players().hasAnimatiumInfo(uuid)

  /**
   * Gets the player manager for this platform
   * @return player manager
   */
  fun players() : AnimatiumPlayerRegistry

  /**
   * Gets a logger for this platform
   * @return logger
   */
  fun logger(): Logger
}

/**
 * A server feature that can be enabled or disabled for a player using Animatium
 */
enum class ServerFeature(val id: String)
{
  All("all"),
  MissPenalty("miss_penalty"),
  LeftClickItemUsage("left_click_item_usage"),
  MiningItemUsage("mining_item_usage"),
  HideFirstPersonRodBobber("hide_rod_bobber"),
  PickInflation("pick_inflation"),
  OldSneakHeight("old_sneak_height"),
  ClientsideEntities("clientside_entities"),
  FixSprintItemUse("disable_sprint_item_use"),
  FixSprintSneaking("disable_sprint_sneaking");

  companion object
  {

    val ENTRY_COUNT = ServerFeature.entries.size

    private val FEATURES_BY_ID = ServerFeature.entries.associateBy { it.id }

    /**
     * Gets a server feature by its string ID
     * @param id the string ID of the feature
     * @return the server feature, or null if no feature has the given ID
     */
    @JvmStatic
    fun fromId(id: String): ServerFeature? = FEATURES_BY_ID[id]

    /**
     * Gets a server feature by its ordinal (numeric ID)
     * @param ordinal the ordinal of the feature
     * @return the server feature, or null if no feature has the given ordinal
     */
    @JvmStatic
    fun fromOrdinal(ordinal: Int): ServerFeature? = if (ordinal !in 0..<ENTRY_COUNT) null else ServerFeature.entries[ordinal]

    /**
     * Gets a collection of all server features
     * @return collection of all server features
     */
    @JvmStatic
    fun all(): Collection<ServerFeature> = ServerFeature.entries
  }

  /**
   * Gets the ordinal of this server feature
   * @return ordinal
   */
  fun numericID(): Int = this.ordinal
}

/**
 * An animatium player (thread-safe)
 */
class AnimatiumPlayer(private val handle: User, private val version: Double)
{

  private val enabledFeatures = ConcurrentHashMap.newKeySet<ServerFeature>()

  /**
   * Gets the Animatium version the player is using
   * @return version
   */
  fun version(): Double = this.version

  /**
   * Gets the player's name.
   * @return name
   */
  fun name(): String = this.handle.name

  /**
   * Gets the player's UUID.
   * @return uuid
   */
  fun uuid(): UUID = this.handle.uuid

  /**
   * Enables a server feature for this player. If the feature is already enabled, this method does nothing.
   * @param feature the feature to enable
   */
  fun enableFeature(feature: ServerFeature)
  {
    if (!this.enabledFeatures.add(feature)) return
    this.sendEnabledFeatures()
  }

  /**
   * Sets the enabled server features for this player
   * @param features the features to enable
   */
  fun setEnabledFeatures(features: Collection<ServerFeature>)
  {
    this.enabledFeatures.clear()
    this.enabledFeatures.addAll(features)
    this.sendEnabledFeatures()
  }

  /**
   * Enables multiple server features for this player. If a feature is already enabled, it will be ignored.
   * @param features the features to enable
   */
  fun enableFeatures(features: Collection<ServerFeature>)
  {
    if (!this.enabledFeatures.addAll(features)) return
    this.sendEnabledFeatures()
  }

  /**
   * Disables a server feature for this player. If the feature is already disabled, this method does nothing.
   * @param feature the feature to disable
   */
  fun disableFeature(feature: ServerFeature)
  {
    if (!this.enabledFeatures.remove(feature)) return
    this.sendEnabledFeatures()
  }

  /**
   * Disables multiple server features for this player. If a feature is already disabled, it will be ignored.
   * @param features the features to disable
   */
  fun disableFeatures(features: Collection<ServerFeature>)
  {
    if (!this.enabledFeatures.removeAll(features.toSet())) return
    this.sendEnabledFeatures()
  }

  /**
   * Disables all server features for this player. If no features are enabled, this method does nothing.
   */
  fun disableAllFeatures() = this.setEnabledFeatures(emptyList())

  /**
   * Checks if a server feature is enabled for this player.
   * @param feature the feature to check
   * @return true if enabled, false otherwise
   */
  fun isFeatureEnabled(feature: ServerFeature): Boolean = this.enabledFeatures.contains(feature)

  /**
   * Gets a copied set of all enabled features for this player.
   * @return set of enabled features
   */
  fun enabledFeatures(): Set<ServerFeature> = HashSet(this.enabledFeatures)

  private fun sendEnabledFeatures() = PayloadClientboundSetServerFeatures(this.enabledFeatures()).send(this.handle)
}