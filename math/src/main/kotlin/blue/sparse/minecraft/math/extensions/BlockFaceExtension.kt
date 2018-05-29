package blue.sparse.minecraft.math.extensions

import blue.sparse.math.vector.floats.vec3f
import org.bukkit.block.BlockFace

fun BlockFace.toVector3f() = vec3f(modX.toFloat(), modY.toFloat(), modZ.toFloat())