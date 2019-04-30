package blue.sparse.minecraft.math.extensions

import blue.sparse.math.quaternion.floats.FloatQuaternion
import blue.sparse.math.vector.floats.Vector3f
import org.bukkit.Chunk
import org.bukkit.entity.Entity

val Entity.position: Vector3f
	get() = location.toVector3f()

val Entity.rotation: FloatQuaternion
	get() = location.rotation

val Entity.chunk: Chunk get() = location.chunk

fun Entity.getChunksInRadius(radiusX: Int, radiusZ: Int = radiusX): Set<Chunk> {
	val origin = chunk
	val result = HashSet<Chunk>()
	for(x in -radiusX..radiusX)
		for(z in -radiusZ..radiusZ)
			result.add(world.getChunkAt(origin.x + x, origin.z + z))

	return result
}