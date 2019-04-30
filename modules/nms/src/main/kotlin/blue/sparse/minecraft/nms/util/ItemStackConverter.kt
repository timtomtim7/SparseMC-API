package blue.sparse.minecraft.nms.util

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.core.data.nbt.converter.NBTConverter
import blue.sparse.minecraft.nms.extensions.toItemStack
import blue.sparse.minecraft.nms.extensions.toNBT
import org.bukkit.inventory.ItemStack

object ItemStackConverter : NBTConverter.Class<ItemStack>(ItemStack::class) {

	override fun toNBT(value: ItemStack): Compound {
		return value.toNBT()
	}

	override fun fromNBT(value: Compound): ItemStack {
		return value.toItemStack()
	}

}