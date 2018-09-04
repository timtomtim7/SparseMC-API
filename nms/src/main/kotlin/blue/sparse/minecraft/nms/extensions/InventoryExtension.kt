package blue.sparse.minecraft.nms.extensions

import blue.sparse.minecraft.core.data.nbt.Compound
import org.bukkit.Material
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

fun Inventory.toNBT(): Compound {
	val contents = contents
	val compounds = contents.mapIndexedNotNull { index, item ->
		if(item == null || item.type == Material.AIR)
			null
		else {
			val compound = item.toNBT()
			compound.int("Slot", index)
			compound
		}
	}

	return Compound {
		int("size", contents.size)
		collection("contents", compounds)
	}
}

fun Inventory.fromNBT(compound: Compound) {
	val contents = Array<ItemStack?>(compound.int("size")) { null }

	val compounds = compound.collectionTyped<Compound>("contents")
	for (itemCompound in compounds) {
		val item = itemCompound.toItemStack()
		val slot = itemCompound.int("Slot")

		contents[slot] = item
	}

	this.contents = contents
}