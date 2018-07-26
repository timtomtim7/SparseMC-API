package blue.sparse.minecraft.nms.v1_9_R2

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.nms.api.EntityNMS
import net.minecraft.server.v1_9_R2.NBTTagCompound
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftEntity
import org.bukkit.entity.Entity

class EntityImpl: EntityNMS {
	override fun getNBT(entity: Entity): Compound {
		val cEntity = entity as CraftEntity
		val nmsCompound = NBTTagCompound()
		cEntity.handle.e(nmsCompound)
		return NBTUtil.nbtBaseValue(nmsCompound) as Compound
	}

	override fun setNBT(entity: Entity, compound: Compound) {
		val cEntity = entity as CraftEntity
		val base = NBTUtil.valueToNBTBase(compound)
		cEntity.handle.f(base as NBTTagCompound)
	}
}