package blue.sparse.minecraft.nms.v1_8_R3

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.nms.api.EntityNMS
import net.minecraft.server.v1_8_R3.EntityInsentient
import net.minecraft.server.v1_8_R3.EntityLiving
import net.minecraft.server.v1_8_R3.NBTTagCompound
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.bukkit.entity.LivingEntity
import org.bukkit.inventory.ItemStack

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

	override fun getDrops(entity: Entity, item: ItemStack?, killer: LivingEntity?): List<ItemStack>? {
		val nmsEntity = (entity as CraftEntity).handle ?: return null
		val entityLiving = nmsEntity as? EntityLiving ?: return null
		val entitySentient = nmsEntity as? EntityInsentient ?: return null

		val dropsField = EntityLiving::class.java.getDeclaredField("drops").apply { isAccessible = true }
		dropsField.set(entityLiving, ArrayList<ItemStack>())

		val dropDeathLootMethod = EntityInsentient::class.java.getDeclaredMethod("dropDeathLoot")
				.apply { isAccessible = true }

		val lootingLevel = item?.getEnchantmentLevel(Enchantment.LOOT_BONUS_MOBS) ?: 0
		dropDeathLootMethod.invoke(entitySentient, false, lootingLevel)

		val drops = dropsField.get(entityLiving) as? ArrayList<ItemStack> ?: run {
			println("couldn't cast the drops to an array list of items.")
			return null
		}

		return drops
	}
}