package blue.sparse.minecraft.nms.extensions

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.nms.NMSModule
import blue.sparse.minecraft.nms.block.BlockSnapshot
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData

//TODO: potentially a `toNBT`

var Block.nbt
	get() = NMSModule.blockNMS.getNBT(this)
	set(value) {
		NMSModule.blockNMS.setNBT(this, value ?: return)
	}

inline fun <R> Block.editNBT(body: Compound.() -> R): R {
	val nbt = nbt ?: throw IllegalAccessException("Block type $type does not carry NBT.")
	val result = nbt.run(body)
	this.nbt = nbt
	return result
}

val Block.hasNBT get() = NMSModule.blockNMS.hasNBT(this)
val Block.snapshot: BlockSnapshot
	get() {
		if (hasNBT)
			return BlockSnapshot(type, data, nbt)
		return BlockSnapshot(type, data)
	}

fun Block.getDropsWithEnchanted(item: ItemStack?): List<ItemStack>? {
	return NMSModule.blockNMS.getDrops(this, item)
}

fun Block.crack(percent: Float) {
	NMSModule.blockNMS.crack(this, percent)
}

fun Block.displayBreakParticles(particle: MaterialData = MaterialData(type, data)) {
	NMSModule.blockNMS.displayBreakParticles(this, particle)
}