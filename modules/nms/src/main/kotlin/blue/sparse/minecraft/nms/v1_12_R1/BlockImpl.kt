package blue.sparse.minecraft.nms.v1_12_R1

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.nms.api.BlockNMS
import net.minecraft.server.v1_12_R1.*
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData

class BlockImpl : BlockNMS {
	override fun getNBT(block: Block): Compound? {
		val world = block.world as CraftWorld
		val tileEntity = world.getTileEntityAt(block.x, block.y, block.z) ?: return null

		val nmsCompound = NBTTagCompound()
		tileEntity.save(nmsCompound)
		return NBTUtil.nbtBaseValue(nmsCompound) as Compound
	}

	override fun setNBT(block: Block, compound: Compound): Boolean {
		val world = block.world as CraftWorld
		val tileEntity = world.getTileEntityAt(block.x, block.y, block.z) ?: return false

		val base = NBTUtil.valueToNBTBase(compound)
		tileEntity.load(base as NBTTagCompound)
		return true
	}

	override fun hasNBT(block: Block): Boolean {
		return (block.world as CraftWorld).getTileEntityAt(block.x, block.y, block.z) != null
	}

	override fun getDrops(block: Block, item: ItemStack?): List<ItemStack>? {
		val nmsBlock = CraftMagicNumbers.getBlock(block)
		val nmsItem = CraftItemStack.asNMSCopy(item)
		val nmsWorld = (block.world as CraftWorld).handle
		val nmsPosition = BlockPosition(block.x, block.y, block.z)
		val nmsData = nmsWorld.getType(nmsPosition)

		val nmsBlockClass = net.minecraft.server.v1_12_R1.Block::class.java
		val silkDropsMethod = nmsBlockClass.getDeclaredMethod("n")
		silkDropsMethod.isAccessible = true
		val silkDrops = silkDropsMethod.invoke(nmsBlock) as Boolean

		val nmsResult = if(silkDrops && EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, nmsItem) > 0) {
//			val method = nmsBlock.reflection["u"].method(IBlockData::class.java)!!
			val method = nmsBlockClass.getDeclaredMethod("u", IBlockData::class.java)
			method.isAccessible = true
			method.invoke(nmsBlock, nmsData) as net.minecraft.server.v1_12_R1.ItemStack

		} else {
			val i = EnchantmentManager.getEnchantmentLevel(Enchantments.LOOT_BONUS_BLOCKS, nmsItem)
			val type = nmsBlock.getDropType(nmsData, nmsWorld.random, i)
			if(type === Items.a)
				return emptyList()

			val count = nmsBlock.getDropCount(i, nmsWorld.random)

			net.minecraft.server.v1_12_R1.ItemStack(type, count)
		}

		return listOf(CraftItemStack.asCraftMirror(nmsResult))

//		if (this.n() && EnchantmentManager.getEnchantmentLevel(Enchantments.SILK_TOUCH, itemstack) > 0) {
//			val itemstack1 = this.u(iblockdata)
//			a(world, blockposition, itemstack1)
//		} else {
//			val i = EnchantmentManager.getEnchantmentLevel(Enchantments.LOOT_BONUS_BLOCKS, itemstack)
//			this.b(world, blockposition, iblockdata, i)
//		}
	}

	override fun displayBreakParticles(block: Block, particle: MaterialData) {
		val id = (particle.data.toInt() shl 12) or particle.itemType.id
		val position = BlockPosition(block.x, block.y, block.z)

		val packet = PacketPlayOutWorldEvent(2001, position, id, false)
		block.world.getNearbyEntities(block.location, 20.0, 20.0, 20.0)
				.filterIsInstance<CraftPlayer>()
				.forEach { it.handle.playerConnection.sendPacket(packet) }
	}

	override fun crack(block: Block, percent: Float) {
		val position = BlockPosition(block.x, block.y, block.z)
		val packet = PacketPlayOutBlockBreakAnimation(
				position.hashCode() * -13,
				position,
				if (percent !in 0f..1f) -1 else (percent * 7f).toInt()
		)

		block.world.getNearbyEntities(block.location, 20.0, 20.0, 20.0)
				.filterIsInstance<CraftPlayer>()
				.forEach { it.handle.playerConnection.sendPacket(packet) }
	}
}