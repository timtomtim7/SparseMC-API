package blue.sparse.minecraft.nms.extensions

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.nms.NMSModule
import org.bukkit.entity.Entity

private val nms by lazy { NMSModule.entityNMS }

//TODO potential 'toNBT'

var Entity.nbt
	get() = nms.getNBT(this)
	set(value) = nms.setNBT(this, value)

inline fun <R> Entity.editNBT(body: Compound.() -> R): R {
	val cachedNbt = nbt
	val result = cachedNbt.run(body)
	this.nbt = cachedNbt
	return result
}