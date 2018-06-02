package blue.sparse.minecraft.inventory.item

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.inventory.extensions.editMeta
import blue.sparse.minecraft.nms.extensions.editNBT
import blue.sparse.minecraft.nms.extensions.nbt
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.material.MaterialData
import java.util.concurrent.ThreadLocalRandom

abstract class CustomItemType(
		val id: String,
		val icon: MaterialData,
		val canStack: Boolean = true
) {

	protected fun create(vararg args: Any): ItemStack {
		val item = icon.toItemStack(1)

		item.editNBT {
			compound("sparseCustomItem") {
				string("id", id)
				if (!canStack)
					long("uid", ThreadLocalRandom.current().nextLong())

				compound("data", newData(args))
			}
		}
		item.editMeta { newMeta(args, this) }

		return item
	}

	fun isInstance(item: ItemStack): Boolean {
		val nbt = item.nbt
		if ("sparseCustomItem" !in nbt)
			return false

		return nbt.compound("sparseCustomItem").string("id") == this.id
	}


	open fun onWorldRightClick(player: Player, item: ItemStack, block: Block?, entity: Entity?) {}
	open fun onWorldRightClickBlock(player: Player, item: ItemStack, block: Block) {}
	open fun onWorldRightClickEntity(player: Player, item: ItemStack, entity: Entity) {}
	open fun onWorldRightClickNothing(player: Player, item: ItemStack) {}

	open fun onWorldLeftClick(player: Player, item: ItemStack, block: Block?, entity: Entity?) {}
	open fun onWorldLeftClickBlock(player: Player, item: ItemStack, block: Block) {}
	open fun onWorldLeftClickEntity(player: Player, item: ItemStack, entity: Entity) {}
	open fun onWorldLeftClickNothing(player: Player, item: ItemStack) {}

	open fun onDrop(player: Player, item: ItemStack, entity: Item) {}
	open fun onPickup(player: Player, item: ItemStack, entity: Item) {}

	protected fun getData(item: ItemStack): Compound {
		instanceCheck(item)

		return item.nbt.compound("sparseCustomItem").compound("data")
	}

	protected abstract fun newData(args: Array<out Any>): Compound

	protected abstract fun newMeta(args: Array<out Any>, meta: ItemMeta)

	protected fun instanceCheck(item: ItemStack) {
		if (!isInstance(item))
			throw IllegalArgumentException("Item is not an instance of this custom item type (${javaClass.name})")
	}

}