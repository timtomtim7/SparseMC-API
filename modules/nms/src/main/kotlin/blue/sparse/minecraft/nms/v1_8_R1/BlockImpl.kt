package blue.sparse.minecraft.nms.v1_8_R1

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.nms.api.BlockNMS
import net.minecraft.server.v1_8_R1.NBTTagCompound
import org.bukkit.block.Block
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld
import org.bukkit.inventory.ItemStack

class BlockImpl : BlockNMS {
	override fun getNBT(block: Block): Compound? {
		val world = block.world as CraftWorld
		val tileEntity = world.getTileEntityAt(block.x, block.y, block.z) ?: return null

		val nmsCompound = NBTTagCompound()
		tileEntity.b(nmsCompound)
		return NBTUtil.nbtBaseValue(nmsCompound) as Compound
	}

	override fun setNBT(block: Block, compound: Compound): Boolean {
		val world = block.world as CraftWorld
		val tileEntity = world.getTileEntityAt(block.x, block.y, block.z) ?: return false

		val base = NBTUtil.valueToNBTBase(compound)
		tileEntity.a(base as NBTTagCompound)
		return true
	}

	override fun hasNBT(block: Block): Boolean {
		return (block.world as CraftWorld).getTileEntityAt(block.x, block.y, block.z) != null
	}

	override fun getDrops(block: Block, item: ItemStack?): List<ItemStack>? {
		TODO("not implemented")
	}

	override fun crack(block: Block, percent: Float) {

	}
}