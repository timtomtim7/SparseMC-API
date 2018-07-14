package blue.sparse.minecraft.inventory.menu

import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack

class InventorySection(
		val inventory: Inventory,
		val origin: Vector2i,
		val size: Vector2i
) : Iterable<Vector2i> {

	override fun iterator(): Iterator<Vector2i> {
		return (0 until size.x).asSequence().flatMap { x ->
			(0 until size.y).asSequence().map { y -> Vector2i(x, y) }
		}.iterator()
	}

	fun getSlot(position: Vector2i): Int {
		val full = position + origin
		return (full.y * 9) + full.x
	}

	fun clear() {
		forEach { set(it, null) }
	}

	operator fun set(position: Vector2i, item: ItemStack?) {
		inventory.setItem(getSlot(position), item)
	}

	operator fun get(position: Vector2i): ItemStack? {
		return inventory.getItem(getSlot(position))
	}

	operator fun contains(slot: Int): Boolean {
		return getPosition(slot) in this
	}

	operator fun contains(position: Vector2i): Boolean {
		return position.x >= 0 && position.y >= 0 && position.x < size.x && position.y < size.y
	}

	fun getPosition(slot: Int): Vector2i {
		return getAbsolutePosition(slot) - origin
	}

	fun getAbsolutePosition(slot: Int): Vector2i {
		val x = slot % 9
		val y = slot / 9
		return Vector2i(x, y)
	}

	fun subsection(originOffset: Vector2i, size: Vector2i): InventorySection {
		//TODO: Bounds checking?
//		val newOrigin = origin + originOffset
//		Throwable("$origin + $originOffset = ${origin + originOffset}").printStackTrace()

		return InventorySection(
				inventory,
				origin + originOffset,
				size
		)

	}

}