package blue.sparse.minecraft.nms.api

import blue.sparse.minecraft.core.data.nbt.Compound
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

interface EntityNMS: NMSHandler {
	fun getNBT(entity: Entity): Compound
	fun setNBT(entity: Entity, compound: Compound)

	fun getDrops(entity: Entity, item: ItemStack?, killer: LivingEntity?): List<ItemStack>?
}