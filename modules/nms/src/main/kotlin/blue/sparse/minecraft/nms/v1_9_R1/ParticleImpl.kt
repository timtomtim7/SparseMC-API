package blue.sparse.minecraft.nms.v1_9_R1

import blue.sparse.minecraft.nms.api.ParticleNMS
import blue.sparse.minecraft.nms.particle.ParticleData
import blue.sparse.minecraft.nms.particle.ParticleType
import net.minecraft.server.v1_9_R1.EnumParticle
import net.minecraft.server.v1_9_R1.PacketPlayOutWorldParticles
import org.bukkit.Location
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class ParticleImpl : ParticleNMS {
	override fun isAvailable(particle: ParticleType<*>): Boolean {
		return EnumParticle.a(particle.numericID) != null
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
				EnumParticle.a(particle.numericID),
				true,
				position.x.toFloat(),
				position.y.toFloat(),
				position.z.toFloat(),
				data.offsetX, data.offsetY, data.offsetZ,
				data.data, data.count, *data.extra
		))
	}
}