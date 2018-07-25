package blue.sparse.minecraft.nms.api

import blue.sparse.minecraft.core.data.nbt.Compound
import org.bukkit.block.Block

interface BlockNMS {
	fun getNBT(block: Block): Compound?
	fun setNBT(block: Block, compound: Compound): Boolean
	fun hasNBT(block: Block): Boolean
}