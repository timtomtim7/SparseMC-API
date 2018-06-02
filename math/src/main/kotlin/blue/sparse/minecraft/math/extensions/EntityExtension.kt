package blue.sparse.minecraft.math.extensions

import blue.sparse.math.vector.floats.Vector3f
import org.bukkit.entity.Entity

val Entity.position: Vector3f
	get() = location.toVector3f()