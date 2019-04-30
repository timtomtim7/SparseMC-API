package blue.sparse.minecraft.core.extensions

import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity

val Entity.block: Block
	get() = location.block

fun Entity.getNearbyEntities(radius: Double): List<Entity> = getNearbyEntities(radius, radius, radius)

fun Entity.lookAt(other: Location) {
	if (this is LivingEntity) {
		teleport(location.apply {
			direction = eyeLocation.directionTo(other)
		})
	}
	else
		teleport(location.lookAt(other))
}

fun Entity.lookAt(entity: Entity) {
	if (entity is LivingEntity)
		lookAt(entity.eyeLocation)
	else
		lookAt(entity.location)
}