package blue.sparse.minecraft.nms.block

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.nms.extensions.editNBT
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

data class BlockSnapshot(
		val material: Material,
		val tileEntityNBT: Compound? = null
) {

	val item: ItemStack
		get() {
			val item = ItemStack(material, 1)
			if (tileEntityNBT != null)
				item.editNBT { compound("BlockEntityTag", tileEntityNBT) }
			return item
		}
}