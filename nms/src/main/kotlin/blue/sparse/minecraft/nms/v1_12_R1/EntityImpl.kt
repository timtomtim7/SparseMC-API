package blue.sparse.minecraft.nms.v1_12_R1

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.nms.api.EntityNMS
import net.minecraft.server.v1_12_R1.*
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.entity.*
import org.bukkit.entity.Entity
import org.bukkit.inventory.ItemStack

class EntityImpl : EntityNMS {
	override fun getNBT(entity: Entity): Compound {
		val cEntity = entity as CraftEntity
		val nmsCompound = NBTTagCompound()
		cEntity.handle.save(nmsCompound)
		return NBTUtil.nbtBaseValue(nmsCompound) as Compound
	}

	override fun setNBT(entity: Entity, compound: Compound) {
		val cEntity = entity as CraftEntity
		val base = NBTUtil.valueToNBTBase(compound)
		cEntity.handle.f(base as NBTTagCompound)
	}

	override fun getDrops(entity: Entity, item: ItemStack?, killer: LivingEntity?): List<ItemStack>? {
		val nmsEntity = (entity as CraftEntity).handle as? EntityInsentient ?: return null
		val insentientClass = EntityInsentient::class.java

		val bCField = insentientClass.getDeclaredField("bC")
		bCField.isAccessible = true

		var key = bCField.get(nmsEntity) as MinecraftKey?
//		var key = nmsEntity.castDeclaredField<MinecraftKey?>("bC")
		if (key == null) {
			val method = insentientClass.getDeclaredMethod("J")
			method.isAccessible = true
			key = method.invoke(nmsEntity) as MinecraftKey?
		}

		if (key == null)
			return null

		val lootTable = nmsEntity.world.lootTableRegistry.a(key)
		var info = LootTableInfo.a(nmsEntity.world as WorldServer).a(nmsEntity)

		if(killer != null && killer is HumanEntity) {
			val nmsKiller = (killer as CraftHumanEntity).handle
			info = info.a(nmsKiller).a(nmsKiller.du())
		}

		//TODO: Killed by player?

		return lootTable.a(nmsEntity.world.random, info.a())?.map { CraftItemStack.asCraftMirror(it) }
	}
}