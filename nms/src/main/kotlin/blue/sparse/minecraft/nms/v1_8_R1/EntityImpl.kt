package blue.sparse.minecraft.nms.v1_8_R1

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.nms.api.EntityNMS
import blue.sparse.minecraft.util.castDeclaredField
import net.minecraft.server.v1_8_R1.EntityLiving
import net.minecraft.server.v1_8_R1.NBTTagCompound
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

class EntityImpl : EntityNMS {
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

	@Suppress("UNCHECKED_CAST")
	override fun getDrops(entity: Entity, item: ItemStack?, killer: LivingEntity?): List<ItemStack>? {
		val entityLiving = (entity as CraftEntity).handle as? EntityLiving ?: return null
		val lootingLevel = item?.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) ?: 0

		val dropsField = EntityLiving::class.java.getDeclaredField("drops").apply { isAccessible = true }
		dropsField[entityLiving] = ArrayList<ItemStack>()

		for (methodName in listOf("dropDeathLoot", "dropEquipment")) {

			val method = EntityLiving::class.java
					.getDeclaredMethod(methodName, Boolean::class.java, Int::class.javaPrimitiveType)
					.apply {
						isAccessible = true
					}

			method(entityLiving, false, lootingLevel)
		}

		EntityLiving::class.java.getDeclaredMethod("getRareDrop")
				.apply { isAccessible = true }
				.invoke(entityLiving, false, lootingLevel)

		val drops = dropsField[entityLiving] as? ArrayList<ItemStack> ?: return null

		dropsField[entityLiving] = null
		return drops
	}
}