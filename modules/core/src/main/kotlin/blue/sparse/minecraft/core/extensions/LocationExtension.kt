package blue.sparse.minecraft.core.extensions

import org.bukkit.Location
import org.bukkit.util.Vector

fun Location.lookAt(other: Location) = clone().apply {
	direction = directionTo(other)
}

fun Location.directionTo(other: Location): Vector = other.toVector().subtract(toVector()).normalize()