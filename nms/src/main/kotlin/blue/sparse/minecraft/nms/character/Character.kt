package blue.sparse.minecraft.nms.character

import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.core.i18n.LocalizedString
import blue.sparse.minecraft.nms.NMSModule
import blue.sparse.minecraft.nms.api.CharacterNMS
import blue.sparse.minecraft.util.*
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.Objects

open class Character(
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
		get() = location.world.entities
				.filterIsInstance<Player>()
				.filter { location.distanceSquared(it.location) <= 50*50 }

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
		location.direction = target.clone().subtract(location.toVector().add(Vector(0.0, handle.eyeHeight, 0.0)))
	}

	fun remove() {
		despawn()
		characters.remove(this)
	}

	fun animateSwing() {
		handle.animateSwing()
	}

	fun animateDamage() {
		handle.animateDamage()
	}

	protected open fun onAttacked(player: Player) {}

	protected open fun onRightClicked(player: Player) {}

	protected open fun onTick() {}

	internal fun tick() {
		onTick()

		val newHash = dataHash
		if (newHash != lastDataHash) {
//			println("Respawning:  $newHash != $lastDataHash")
			lastDataHash = newHash
			respawn()
		}

		val notVisible = visibleTo.filterTo(ArrayList()) {
			!it.isOnline || it.world != location.world || it.location.distanceSquared(location) >= 55.0 * 55.0
		}
		visibleTo.removeAll(notVisible)
		notVisible.removeAll { !it.isOnline }
		notVisible.forEach(handle::setInvisible)

		val newVisible = nearbyPlayers - visibleTo
		newVisible.forEach(handle::setVisible)
		visibleTo.addAll(newVisible)

		if(newVisible.isNotEmpty())
			equipment.setAll()

		if (lastLocation != location) {
			if(lastLocation.toVector() != location.toVector()) {
				if (lastLocation.distanceSquared(location) <= 8 * 8) {
					handle.moveTo(location.x, location.y, location.z, location.yaw, location.pitch)
				}else{
					handle.teleport(location.x, location.y, location.z, location.yaw, location.pitch)
				}
			}else if(lastLocation.direction != location.direction) {
				handle.look(location.yaw, location.pitch)
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
			NMSModule.characterNMS.setEntityUseCallback(::useCallback)
		}

		private fun tick() {
			characters.iterator().forEach(Character::tick)
		}

		private fun useCallback(player: Player, id: Int, action: CharacterNMS.UseAction) {
			server.scheduler.scheduleSyncDelayedTask(SparseMCAPIPlugin.getPlugin()) {
				val used = characters.find { it.handle.id == id } ?: return@scheduleSyncDelayedTask
				when(action) {
					CharacterNMS.UseAction.INTERACT -> used.onRightClicked(player)
					CharacterNMS.UseAction.ATTACK -> used.onAttacked(player)
				}
			}
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