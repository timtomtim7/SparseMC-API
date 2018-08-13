package blue.sparse.minecraft.nms.v1_9_R1

import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.core.i18n.LocalizedString
import blue.sparse.minecraft.nms.api.CharacterNMS
import blue.sparse.minecraft.nms.character.Skin
import blue.sparse.minecraft.util.*
import com.mojang.authlib.GameProfile
import com.mojang.authlib.properties.Property
import net.minecraft.server.v1_9_R1.*
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_9_R1.CraftServer
import org.bukkit.craftbukkit.v1_9_R1.CraftWorld
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_9_R1.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import java.util.UUID

class CharacterImpl: CharacterNMS {
	override val isSecondaryHandSupported = true
	override val isAnimationSwimSupported = false
	override val isAnimationElytraSupported = true

	override fun spawn(name: Either<String, LocalizedString>, location: Location, skin: Skin?): CharacterNMS.CharacterHandle {
		return CharacterHandleImpl(name, skin, location)
	}

	override fun getSkin(player: Player): Skin? {
		val profile = (player as CraftPlayer).handle.profile
		val textures = profile.properties["textures"]?.firstOrNull() ?: return null
		return Skin(textures.value, textures.signature)
	}

	class CharacterHandleImpl(
			override var name: Either<String, LocalizedString>,
			var skin: Skin?,
			location: Location
	): CharacterNMS.CharacterHandle {

		private val nms: EntityPlayer

		private val world = location.world

		private val location: Location
			get() = Location(world, nms.locX, nms.locY, nms.locZ, nms.yaw, nms.pitch)

		private val nearbyPlayers: List<CraftPlayer>
			get() = location.world.getNearbyEntities(location, 50.0, 50.0, 50.0)
					.filterIsInstance<CraftPlayer>()

		override val eyeHeight: Double
			get() = 1.62///nms.headHeight.toDouble()

		init {
			val nmsWorld = (location.world as CraftWorld).handle
			val nmsServer = (server as CraftServer).server

			val name = name.fold({it}, {it.default})
			val profile = GameProfile(UUID.fromString("f01763ef-0925-4f3f-b3a4-9bef42068f5e"), name)
			val skin = skin
			if(skin != null)
				profile.properties.put("textures", Property("textures", skin.value, skin.signature))

			nms = EntityPlayer(nmsServer, nmsWorld, profile, PlayerInteractManager(nmsWorld))
			nms.setLocation(location.x, location.y, location.z, location.yaw, location.pitch)
		}

		private fun sendPacketNearby(packet: Packet<*>) {
			nearbyPlayers.sendPacket(packet)
		}

		private fun Iterable<Player>.sendPacket(packet: Packet<*>) {
			forEach { it.sendPacket(packet) }
		}

		private fun Player.sendPacket(packet: Packet<*>) {
			(this as CraftPlayer).handle.playerConnection.sendPacket(packet)
		}

		override fun setVisible(player: Player) {
			val name = name.fold({ it }, { it.get(player) ?: it.key })
			nms.profile.reflection["name"].declaredFieldValue = name
			nms.displayName = name

//			skin?.let { skin ->
//				val properties = nms.profile.properties
//				properties.keySet().remove("textures")
//				properties.put("textures", Property("textures", skin.value, skin.signature))
//				println(properties["textures"].first().value)
//			}

			player.sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, nms))
			player.sendPacket(PacketPlayOutNamedEntitySpawn(nms))
			player.sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, nms))
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

			sendPacketNearby(PacketPlayOutEntity.PacketPlayOutRelEntityMove(nms.id, deltaX, deltaY, deltaZ, nms.onGround))
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

			sendPacketNearby(PacketPlayOutEntity.PacketPlayOutRelEntityMoveLook(nms.id, deltaX, deltaY, deltaZ, byteYaw, bytePitch, nms.onGround))
		}

		override fun look(yaw: Float, pitch: Float) {
			nms.yaw = yaw
			nms.pitch = pitch

			val byteYaw = (yaw * 256.0F / 360.0F).toByte()
			val bytePitch = (pitch * 256.0F / 360.0F).toByte()
			sendPacketNearby(PacketPlayOutEntity.PacketPlayOutEntityLook(nms.id, byteYaw, bytePitch, true))
			sendPacketNearby(PacketPlayOutEntityHeadRotation(nms, byteYaw))
		}

		override fun setItem(slot: EquipmentSlot, item: ItemStack?) {
			sendPacketNearby(PacketPlayOutEntityEquipment(nms.id, EnumItemSlot.values()[slot.ordinal], CraftItemStack.asNMSCopy(item)))
		}

		override fun animateSwing() {
			sendPacketNearby(PacketPlayOutAnimation(nms, 0))
		}

		override fun animateDamage() {
			sendPacketNearby(PacketPlayOutAnimation(nms, 1))

//			nms.world.broadcastEntityEffect(nms, 2)
		}

		override fun setAnimationSwim(value: Boolean) {
			server.logger.warning("Swim animation is not supported by this version of Minecraft.")
		}

		override fun setAnimationElytra(value: Boolean) {
			nms.setFlag(8, value)
		}

		override fun setAnimationSneaking(value: Boolean) {
			nms.setFlag(1, value)
		}

		override fun setAnimationSprinting(value: Boolean) {
			nms.setFlag(2, value)
		}

		override fun breakBlock(hand: ItemStack, block: Block): Boolean {
			if (block.world != world)
				throw IllegalStateException("Attempt to break block in different world.")
			return nms.playerInteractManager.breakBlock(BlockPosition(block.x, block.y, block.z))
		}
	}

}