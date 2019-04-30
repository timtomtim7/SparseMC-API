package blue.sparse.minecraft.nms.v1_7_R4

import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.core.i18n.LocalizedString
import blue.sparse.minecraft.nms.api.CharacterNMS
import blue.sparse.minecraft.nms.character.Skin
import blue.sparse.minecraft.util.*
import net.minecraft.server.v1_7_R4.*
import net.minecraft.util.com.mojang.authlib.GameProfile
import net.minecraft.util.com.mojang.authlib.properties.Property
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_7_R4.CraftServer
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import java.util.UUID

class CharacterImpl: CharacterNMS {
	override val isSecondaryHandSupported = false
	override val isAnimationSwimSupported = false
	override val isAnimationElytraSupported = false

	private var useCallback: ((Player, Int, CharacterNMS.UseAction) -> Unit)? = null

	override fun spawn(name: Either<String, LocalizedString>, location: Location, skin: Skin?): CharacterNMS.CharacterHandle {
		return CharacterHandleImpl(name, skin, location)
	}

	override fun getSkin(player: Player): Skin? {
		val profile = (player as CraftPlayer).handle.profile
		val textures = profile.properties["textures"]?.firstOrNull() ?: return null
		return Skin(textures.value, textures.signature)
	}

	override fun setEntityUseCallback(body: (Player, Int, CharacterNMS.UseAction) -> Unit) {
		useCallback = body
	}

	internal fun handleUseEntity(player: Player, packet: PacketPlayInUseEntity) {
		val entityID = packet.castDeclaredField<Int>("a")
		val action = when (packet.c()) {
			EnumEntityUseAction.INTERACT -> CharacterNMS.UseAction.INTERACT
			EnumEntityUseAction.ATTACK -> CharacterNMS.UseAction.ATTACK
			else -> throw IllegalStateException()
		}
		useCallback?.invoke(player, entityID, action)
	}

	class CharacterHandleImpl(
			override var name: Either<String, LocalizedString>,
			var skin: Skin?,
			location: Location
	) : CharacterNMS.CharacterHandle {

		private val nms: EntityPlayer

		private val world = location.world

		private val location: Location
			get() = Location(world, nms.locX, nms.locY, nms.locZ, nms.yaw, nms.pitch)

		private val nearbyPlayers: List<CraftPlayer>
			get() = location.world.entities
					.filterIsInstance<CraftPlayer>()
					.filter { location.distanceSquared(it.location) <= 50*50 }

		override val eyeHeight: Double
			get() = 1.62///nms.headHeight.toDouble()

		override val id: Int
			get() = nms.id

		init {
			val nmsWorld = (location.world as CraftWorld).handle
			val nmsServer = (server as CraftServer).server

			val name = name.fold({ it }, { it.default })
//			val profile = GameProfile(UUID.fromString("f01763ef-0925-4f3f-b3a4-9bef42068f5e"), name)
			val profile = GameProfile(UUID.randomUUID(), name)
			val skin = skin
			if (skin != null)
				profile.properties.put("textures", Property("textures", skin.value, skin.signature))

			nms = EntityPlayer(nmsServer, nmsWorld, profile, PlayerInteractManager(nmsWorld))
			nms.setLocation(location.x, location.y, location.z, location.yaw, location.pitch)
		}

		private fun sendPacketNearby(packet: Packet) {
			nearbyPlayers.sendPacket(packet)
		}

		private fun Iterable<Player>.sendPacket(packet: Packet) {
			forEach { it.sendPacket(packet) }
		}

		private fun Player.sendPacket(packet: Packet) {
			(this as CraftPlayer).handle.playerConnection.sendPacket(packet)
		}

		override fun setVisible(player: Player) {
			val name = name.fold({ it }, { it.get(player) ?: it.key })
			nms.profile.reflection["name"].declaredFieldValue = name
			nms.displayName = name

			skin?.let { skin ->
				val properties = nms.profile.properties
				properties.keySet().remove("textures")
				properties.put("textures", Property("textures", skin.value, skin.signature))
//				println(properties["textures"].first().value)
			}

//			val field = EntityHuman::class.java.getDeclaredField("br")
//			field.isAccessible = true
//			@Suppress("UNCHECKED_CAST")
//			nms.dataWatcher.a(field.get(null) as Int, 0xFF.toByte())

			player.sendPacket(PacketPlayOutPlayerInfo.addPlayer(nms))
			player.sendPacket(PacketPlayOutNamedEntitySpawn(nms))
			server.scheduler.scheduleSyncDelayedTask(SparseMCAPIPlugin.getPlugin(), {
				player.sendPacket(PacketPlayOutPlayerInfo.removePlayer(nms))
			}, 20L)
			createLookPackets().forEach { player.sendPacket(it) }
		}

		override fun setInvisible(player: Player) {
			player.sendPacket(PacketPlayOutEntityDestroy(nms.id))
		}

		override fun teleport(x: Double, y: Double, z: Double) {
			nms.locX = x
			nms.locY = y
			nms.locZ = z
			sendPacketNearby(PacketPlayOutEntityTeleport(nms))
		}

		override fun teleport(x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
			nms.locX = x
			nms.locY = y
			nms.locZ = z
			nms.yaw = yaw
			nms.pitch = pitch
			sendPacketNearby(PacketPlayOutEntityTeleport(nms))
		}

		override fun moveTo(x: Double, y: Double, z: Double) {
			val deltaX = ((x * 32 - location.x * 32) * 128).toLong()
			val deltaY = ((y * 32 - location.y * 32) * 128).toLong()
			val deltaZ = ((z * 32 - location.z * 32) * 128).toLong()
			nms.locX = x
			nms.locY = y
			nms.locZ = z

			sendPacketNearby(PacketPlayOutRelEntityMove(nms.id, deltaX.toByte(), deltaY.toByte(), deltaZ.toByte(), nms.onGround))
		}

		override fun moveTo(x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
			val deltaX = ((x * 32 - location.x * 32) * 128).toLong()
			val deltaY = ((y * 32 - location.y * 32) * 128).toLong()
			val deltaZ = ((z * 32 - location.z * 32) * 128).toLong()
			val byteYaw = (yaw * 256.0F / 360.0F).toByte()
			val bytePitch = (pitch * 256.0F / 360.0F).toByte()
			nms.locX = x
			nms.locY = y
			nms.locZ = z
			nms.yaw = yaw
			nms.pitch = pitch

			sendPacketNearby(PacketPlayOutRelEntityMoveLook(nms.id, deltaX.toByte(), deltaY.toByte(), deltaZ.toByte(), byteYaw, bytePitch, nms.onGround))
		}

		override fun look(yaw: Float, pitch: Float) {
			nms.yaw = yaw
			nms.pitch = pitch

			createLookPackets().forEach(this::sendPacketNearby)
		}

		private fun createLookPackets(): Collection<Packet> {
			val byteYaw = (nms.yaw * 256.0F / 360.0F).toByte()
			val bytePitch = (nms.pitch * 256.0F / 360.0F).toByte()
			return listOf(
					PacketPlayOutEntityLook(nms.id, byteYaw, bytePitch, true),
					PacketPlayOutEntityHeadRotation(nms, byteYaw)
			)
		}

		override fun setItem(slot: EquipmentSlot, item: org.bukkit.inventory.ItemStack?) {
			TODO()
		}

		override fun animateSwing() {
			sendPacketNearby(PacketPlayOutAnimation(nms, 0))
		}

		override fun animateDamage() {
			sendPacketNearby(PacketPlayOutAnimation(nms, 1))
		}

		override fun setAnimationSwim(value: Boolean) {
			server.logger.warning("Swim animation is not supported by this version of Minecraft.")
		}

		override fun setAnimationElytra(value: Boolean) {
			TODO()
		}

		override fun setAnimationSneaking(value: Boolean) {
			TODO()
		}

		override fun setAnimationSprinting(value: Boolean) {
			TODO()
		}
	}
}