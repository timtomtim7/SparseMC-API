package blue.sparse.minecraft.nms.v1_9_R2

import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.core.i18n.LocalizedString
import blue.sparse.minecraft.nms.api.CharacterNMS
import com.mojang.authlib.GameProfile
import net.minecraft.server.v1_9_R2.*
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_9_R2.CraftServer
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer
import org.bukkit.entity.Player
import java.util.UUID

class CharacterImpl: CharacterNMS {
	override val isSecondaryHandSupported = true
	override val isAnimationSwimSupported = false
	override val isAnimationElytraSupported = true

	override fun spawn(name: String, location: Location): CharacterNMS.CharacterHandle {
		return CharacterHandleImpl(name, location)
	}

	override fun spawn(name: LocalizedString, location: Location): CharacterNMS.CharacterHandle {
		return spawn(name.default ?: "!!!", location) // TODO: Change name per player
	}

	class CharacterHandleImpl(val name: String, private var location: Location): CharacterNMS.CharacterHandle {

		private val nms: EntityPlayer

		private val nearbyPlayers: List<CraftPlayer>
			get() = location.world.getNearbyEntities(location, 50.0, 50.0, 50.0)
					.filterIsInstance<CraftPlayer>()

		init {
			val nmsWorld = (location.world as CraftWorld).handle
			val nmsServer = (server as CraftServer).server

			nms = EntityPlayer(nmsServer, nmsWorld, GameProfile(UUID.randomUUID(), name), PlayerInteractManager(nmsWorld))
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
			player.sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, nms))
			player.sendPacket(PacketPlayOutNamedEntitySpawn(nms))
			player.sendPacket(PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, nms))
		}

		override fun setInvisible(player: Player) {
			player.sendPacket(PacketPlayOutEntityDestroy(nms.id))
		}

		override fun teleport(x: Double, y: Double, z: Double) {
			location = Location(location.world, x, y, z)
			nms.locX = x
			nms.locY = y
			nms.locZ = z
			sendPacketNearby(PacketPlayOutEntityTeleport(nms))
		}

		override fun teleport(x: Double, y: Double, z: Double, yaw: Float, pitch: Float) {
			location = Location(location.world, x, y, z, yaw, pitch)
			nms.locX = x
			nms.locY = y
			nms.locZ = z
			nms.yaw = yaw
			nms.pitch = pitch
			sendPacketNearby(PacketPlayOutEntityTeleport(nms))
		}

		override fun look(yaw: Float, pitch: Float) {
			nms.yaw = yaw
			nms.pitch = pitch
			location.yaw = yaw
			location.pitch = pitch

			val byteYaw = (yaw * 256.0F / 360.0F).toByte()
			val bytePitch = (pitch * 256.0F / 360.0F).toByte()
			sendPacketNearby(PacketPlayOutEntity.PacketPlayOutEntityLook(nms.id, byteYaw, bytePitch, true))
			sendPacketNearby(PacketPlayOutEntityHeadRotation(nms, byteYaw))
		}

//		override fun setItemInPrimaryHand(item: ItemStack?) {
////			nms.itemInMainHand = CraftItemStack.asNMSCopy(item)
//		}
//
//		override fun setItemInSecondaryHand(item: ItemStack?) {
//			TODO("not implemented")
//		}
//
//		override fun setPrimaryHand(right: Boolean) {
//			TODO("not implemented")
//		}
//
//		override fun setArmor(armor: Array<out ItemStack?>) {
//			TODO("not implemented")
//		}
//
//		override fun setArmor(index: Int, item: ItemStack?) {
//			TODO("not implemented")
//		}

		override fun animateSwing() {
			TODO("not implemented")
		}

		override fun animateDamage() {
			TODO("not implemented")
		}

		override fun animateSwim()
				= server.logger.warning("Swim animation is not supported by this version of Minecraft.")

		override fun animateElytra() {
			TODO("not implemented")
		}

		override fun remove() {
			TODO("not implemented")
		}

	}

}