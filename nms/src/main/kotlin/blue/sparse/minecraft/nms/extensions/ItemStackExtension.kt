package blue.sparse.minecraft.nms.extensions

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.nms.NMSModule
import org.bukkit.inventory.ItemStack

var ItemStack.nbt
	get() = NMSModule.itemNMS.getNBT(this)
	set(value) = NMSModule.itemNMS.setNBT(this, value)

inline fun <R> ItemStack.editNBT(body: Compound.() -> R): R {
	val nbt = nbt
	val result = nbt.run(body)
	this.nbt = nbt
	return result
}

fun ItemStack.toNBT(): Compound {
	return NMSModule.itemNMS.toNBT(this)
}

fun Compound.toItemStack(): ItemStack {
	return NMSModule.itemNMS.fromNBT(this)
}