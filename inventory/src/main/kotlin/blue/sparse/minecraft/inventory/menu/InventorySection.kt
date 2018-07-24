package blue.sparse.minecraft.inventory.menu

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class InventorySection(
		val inventory: Inventory,
		val origin: Vector2i,
		val size: Vector2i
) : Iterable<Vector2i> {

	override fun iterator(): Iterator<Vector2i> {
		return (0 until size.y).asSequence().flatMap { y ->
			(0 until size.x).asSequence().map { x -> Vector2i(x, y) }
		}.iterator()
	}

	fun clear() {
		forEach { set(it, null) }
	}

	operator fun set(position: Vector2i, item: ItemStack?) {
		inventory.setItem(getAbsoluteSlot(position), item)
	}

	operator fun get(position: Vector2i): ItemStack? = inventory.getItem(getAbsoluteSlot(position))
	operator fun contains(slot: Int) = getPositionByAbsoluteSlot(slot) in this
	operator fun contains(position: Vector2i): Boolean {
		return position.x >= 0 && position.y >= 0 && position.x < size.x && position.y < size.y
	}

	fun getAbsoluteSlot(position: Vector2i) = (position + origin).run { y * 9 + x }
	fun getSlot(position: Vector2i) = (position.y * size.x) + position.x
	fun getPosition(slot: Int) = Vector2i(slot % size.x, slot / size.x)
	fun getPositionByAbsoluteSlot(slot: Int) = getAbsolutePosition(slot) - origin
	fun getAbsolutePosition(slot: Int) = Vector2i(slot % 9, slot / 9)

	fun subsection(originOffset: Vector2i, size: Vector2i): InventorySection {
		//TODO: Bounds checking?
//		val newOrigin = origin + originOffset

		return InventorySection(
				inventory,
				origin + originOffset,
				size
		)

	}

}