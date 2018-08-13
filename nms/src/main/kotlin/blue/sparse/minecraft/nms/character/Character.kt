package blue.sparse.minecraft.nms.character

import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.core.i18n.LocalizedString
import blue.sparse.minecraft.nms.NMSModule
import blue.sparse.minecraft.nms.api.CharacterNMS
import blue.sparse.minecraft.util.*
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.Objects

class Character(
		name: Either<String, LocalizedString> = Left("Unnamed"),
		var location: Location,
		val skin: Skin? = null
) {

	private val handle: CharacterNMS.CharacterHandle = NMSModule.characterNMS.spawn(name, location.clone(), skin)

	private val dataHash: Int
		get() = Objects.hash(name, location.world, skin)

	private var lastDataHash = dataHash
	private var lastLocation = location.clone()

	private val visibleTo = HashSet<Player>()

	var name: Either<String, LocalizedString>
		get() = handle.name
		set(value) {
			handle.name = value
		}

	val nearbyPlayers: List<Player>
		get() = location.world.getNearbyEntities(location, 50.0, 50.0, 50.0).filterIsInstance<Player>()

	val equipment = Equipment()

	constructor(name: String, location: Location, skin: Skin? = null) : this(Left(name), location, skin)
	constructor(name: LocalizedString, location: Location, skin: Skin? = null) : this(Right(name), location, skin)

	init {
		spawn()
		characters.add(this)
	}

	fun setName(name: String) {
		handle.name = Left(name)
	}

	fun setName(name: LocalizedString) {
		handle.name = Right(name)
	}

	fun lookAt(target: Vector) {
		location.direction = location.toVector().add(Vector(0.0, handle.eyeHeight, 0.0)).subtract(target)
	}

	fun dropBlockItems(hand: ItemStack, block: Block) {
		handle.breakBlock(hand, block)
	}

	fun remove() {
		despawn()
		characters.remove(this)
	}

	fun animateSwing() {
		handle.animateSwing()
	}

	internal fun tick() {
		val newHash = dataHash
		if (newHash != lastDataHash) {
			println("Respawning:  $newHash != $lastDataHash")
			lastDataHash = newHash
			respawn()
		}

		val notVisible = visibleTo.filterNot(Player::isOnline)
		notVisible.forEach(handle::setInvisible)
		visibleTo.removeAll(notVisible)

		val newVisible = nearbyPlayers - visibleTo
		newVisible.forEach(handle::setVisible)
		visibleTo.addAll(newVisible)

		if(newVisible.isNotEmpty())
			equipment.setAll()

		if (lastLocation != location) {
			if (lastLocation.distanceSquared(location) <= 8 * 8) {
				handle.moveTo(location.x, location.y, location.z, location.yaw, location.pitch)
			}else{
				handle.teleport(location.x, location.y, location.z, location.yaw, location.pitch)
			}
			lastLocation = location.clone()
		}
	}

	private fun respawn() {
		visibleTo.forEach(handle::setInvisible)
		visibleTo.forEach(handle::setVisible)
		equipment.setAll()
	}

	private fun spawn() {
		despawn()

		val players = nearbyPlayers
		players.forEach(handle::setVisible)
		visibleTo.addAll(players)
		lastLocation = location.clone()
		equipment.setAll()
	}

	private fun despawn() {
		visibleTo.forEach(handle::setInvisible)
		visibleTo.clear()
	}

	companion object {

		private val characters = HashSet<Character>()

		init {
			server.scheduler.scheduleSyncRepeatingTask(NMSModule.plugin, ::tick, 1L, 1L)
		}

		private fun tick() {
			characters.iterator().forEach(Character::tick)
		}

	}

	inner class Equipment {
		private val equipmentItems = mutableMapOf<EquipmentSlot, ItemStack?>()

		operator fun get(slot: EquipmentSlot): ItemStack? {
			return equipmentItems[slot]
		}

		operator fun set(slot: EquipmentSlot, item: ItemStack?) {
			equipmentItems[slot] = item
			handle.setItem(slot, item)
		}

		internal fun setAll() {
			equipmentItems.forEach { slot, item ->
				handle.setItem(slot, item)
			}
		}
	}
}