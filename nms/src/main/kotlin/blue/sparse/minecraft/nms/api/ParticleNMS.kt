package blue.sparse.minecraft.nms.api

import blue.sparse.minecraft.nms.particle.ParticleData
import blue.sparse.minecraft.nms.particle.ParticleType
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

interface ParticleNMS: NMSHandler {

	fun isAvailable(particle: ParticleType<*>): Boolean

	fun spawn(particle: ParticleType<*>, location: Location, data: ParticleData)
	fun spawn(particle: ParticleType<*>, player: Player, position: Vector, data: ParticleData)
}