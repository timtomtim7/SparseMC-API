package blue.sparse.minecraft.nms.block

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.nms.extensions.editNBT
import blue.sparse.minecraft.nms.extensions.nbt
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

data class BlockSnapshot(
		val material: Material,
		val data: Byte,
		val tileEntityNBT: Compound? = null
) {

	val item: ItemStack
		get() {
			val item = ItemStack(material, 1)
			if (tileEntityNBT != null)
				item.editNBT { compound("BlockEntityTag", tileEntityNBT) }
			return item
		}

	fun place(block: Block) {
		block.type = material
		try {
			val method = block.javaClass.getDeclaredMethod("setData", Byte::class.java)
			method.invoke(block, data)
		}catch(t: Throwable) { }
		block.nbt = tileEntityNBT
	}
}