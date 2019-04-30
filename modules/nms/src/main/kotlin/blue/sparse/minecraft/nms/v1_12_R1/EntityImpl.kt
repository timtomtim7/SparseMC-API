package blue.sparse.minecraft.nms.v1_12_R1

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.nms.api.EntityNMS
import net.minecraft.server.v1_12_R1.*
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftHumanEntity
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
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

	@Suppress("UNCHECKED_CAST")
	override fun getDrops(entity: Entity, item: ItemStack?, killer: LivingEntity?): List<ItemStack>? {
		val entityLiving = (entity as CraftEntity).handle as? EntityLiving ?: return null
		val lootingLevel = item?.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) ?: 0
		val entityLivingClazz = EntityLiving::class.java

		val damageSource =
				if (killer != null && killer is CraftHumanEntity)
					DamageSource.playerAttack(killer.handle)
				else
					DamageSource.GENERIC

		val dropsField = entityLivingClazz.getDeclaredField("drops").apply { isAccessible = true }
		dropsField[entityLiving] = ArrayList<ItemStack>()

		val aMethod = entityLivingClazz.getDeclaredMethod("a",
				Boolean::class.java,
				Int::class.java,
				DamageSource::class.java).apply { isAccessible = true }

		aMethod(entityLiving, false, lootingLevel, damageSource)

		val drops = dropsField[entityLiving] as? ArrayList<ItemStack> ?: return null
		dropsField[entityLiving] = null
		return drops
	}
}