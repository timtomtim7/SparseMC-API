package blue.sparse.minecraft.nms.api

import blue.sparse.minecraft.core.data.nbt.Compound
import org.bukkit.entity.Entity

interface EntityNMS: NMSHandler {
	fun getNBT(entity: Entity): Compound
	fun setNBT(entity: Entity, compound: Compound)
}