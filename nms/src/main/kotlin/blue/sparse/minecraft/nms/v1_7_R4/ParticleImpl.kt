package blue.sparse.minecraft.nms.v1_7_R4

import blue.sparse.minecraft.nms.api.ParticleNMS
import blue.sparse.minecraft.nms.particle.ParticleData
import blue.sparse.minecraft.nms.particle.ParticleType
//import net.minecraft.server.v1_7_R4.EnumParticle
import net.minecraft.server.v1_7_R4.PacketPlayOutWorldParticles
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class ParticleImpl : ParticleNMS {
	override fun isAvailable(particle: ParticleType<*>): Boolean {
		return true //TODO: Check that it actually exists (PacketPlayOutWorldParticles.Particle enum is private, need to use reflection)
	}

	override fun spawn(particle: ParticleType<*>, location: Location, data: ParticleData) {
		val position = location.toVector()
		location.world
				.getNearbyEntities(location, 32.0, 32.0, 32.0)
				.filterIsInstance<Player>()
				.forEach { spawn(particle, it, position, data) }
	}

	override fun spawn(
			particle: ParticleType<*>,
			player: Player,
			position: Vector,
			data: ParticleData
	) {
		val connection = (player as CraftPlayer).handle.playerConnection
		connection.sendPacket(PacketPlayOutWorldParticles(
				particle.stringID,
				position.x.toFloat(),
				position.y.toFloat(),
				position.z.toFloat(),
				data.offsetX, data.offsetY, data.offsetZ,
				data.data, data.count
		))
	}
}