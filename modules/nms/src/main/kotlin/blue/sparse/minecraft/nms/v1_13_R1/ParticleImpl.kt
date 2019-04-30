package blue.sparse.minecraft.nms.v1_13_R1

import blue.sparse.minecraft.nms.api.ParticleNMS
import blue.sparse.minecraft.nms.particle.ParticleData
import blue.sparse.minecraft.nms.particle.ParticleType
import net.minecraft.server.v1_13_R1.MinecraftKey
import net.minecraft.server.v1_13_R1.Particle
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.util.Vector

class ParticleImpl : ParticleNMS {
	override fun isAvailable(particle: ParticleType<*>): Boolean {
		return Particle.REGISTRY.get(MinecraftKey(particle.stringID)) != null
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
//		val param: ParticleParam? = when {
//			particle === ParticleType.Redstone -> ParticleParamRedstone(data.offsetX, data.offsetY, data.offsetZ, 1f)
//
//			else -> null
//		}

//		val bukkit = CraftParticle.toBukkit(Particle.REGISTRY.get(MinecraftKey(particle.stringID)))
//		player.spawnParticle(
//				bukkit, position.toLocation(player.world),
//				data.offsetX, data.offsetY, data.offsetZ,
//				data.data, data.count, *data.extra
//		)

//		val connection = (player as CraftPlayer).handle.playerConnection
//		connection.sendPacket(PacketPlayOutWorldParticles(
////				EnumParticle.a(particle.numericID),
//				CraftParticle.toNMS(bukkit, null),
//				true,
//				position.x.toFloat(),
//				position.y.toFloat(),
//				position.z.toFloat(),
//				data.offsetX, data.offsetY, data.offsetZ, data.count/*,
//				data.data, data.count, *data.extra*/
//		))
	}
}