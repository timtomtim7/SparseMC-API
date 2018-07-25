package blue.sparse.minecraft.nms.api

import blue.sparse.minecraft.core.data.nbt.Compound
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

interface ItemStackNMS : NMSHandler {

	fun getNBT(item: ItemStack): Compound

	fun setNBT(item: ItemStack, compound: Compound)

	fun toNBT(item: ItemStack): Compound

	fun fromNBT(compound: Compound): ItemStack

}