package blue.sparse.minecraft.nms.character

import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.core.i18n.LocalizedString
import blue.sparse.minecraft.nms.NMSModule
import blue.sparse.minecraft.nms.api.CharacterNMS
import blue.sparse.minecraft.util.*
import org.bukkit.Location
import org.bukkit.entity.Player
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

	fun swing() {
		handle.animateSwing()
	}

	fun remove() {
		despawn()
		characters.remove(this)
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
	}

	private fun spawn() {
		despawn()

		val players = nearbyPlayers
		players.forEach(handle::setVisible)
		visibleTo.addAll(players)
		lastLocation = location.clone()
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

}