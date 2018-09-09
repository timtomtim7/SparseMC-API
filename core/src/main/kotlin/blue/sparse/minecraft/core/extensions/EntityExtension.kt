package blue.sparse.minecraft.core.extensions

import org.bukkit.block.Block
import org.bukkit.entity.Entity

val Entity.block: Block
	get() = location.block

fun Entity.getNearbyEntities(radius: Double): List<Entity> = getNearbyEntities(radius, radius, radius)