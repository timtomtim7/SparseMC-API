package blue.sparse.minecraft.math.extensions

import blue.sparse.math.vector.floats.Vector3f
import org.bukkit.Location
import org.bukkit.World

fun Vector3f.toLocation(world: World): Location {
	return Location(world, x.toDouble(), y.toDouble(), z.toDouble())
}