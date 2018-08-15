package blue.sparse.minecraft.nms.api

import blue.sparse.minecraft.core.data.nbt.Compound
import org.bukkit.block.Block
import org.bukkit.inventory.ItemStack

interface BlockNMS: NMSHandler {
	fun getNBT(block: Block): Compound?
	fun setNBT(block: Block, compound: Compound): Boolean
	fun hasNBT(block: Block): Boolean

	fun getDrops(block: Block, item: ItemStack?): List<ItemStack>?
}