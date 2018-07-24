package blue.sparse.minecraft.inventory.menu.element

import blue.sparse.minecraft.inventory.menu.ElementContainer
import blue.sparse.minecraft.inventory.menu.Vector2i
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

abstract class Element(
		val position: Vector2i,
		val size: Vector2i,
		val parent: ElementContainer
) {

	val visible get() = parent.isVisible(this)

	val min = position
	val max = position + size

	val section = parent.section.subsection(min, size)

	operator fun contains(point: Vector2i): Boolean {
		return point.x >= min.x
				&& point.y >= min.y
				&& point.x < max.x
				&& point.y < max.y
	}

	abstract fun onClick(
			event: InventoryClickEvent,
			player: Player,
			position: Vector2i
	)

	abstract fun setup()

	interface Type<E : Element> {
		fun create(position: Vector2i, size: Vector2i, parent: ElementContainer): E
	}
}