package blue.sparse.minecraft.inventory.menu.element

import blue.sparse.minecraft.core.extensions.event.cancel
import blue.sparse.minecraft.inventory.menu.InventorySection
import blue.sparse.minecraft.inventory.menu.Vector2i
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent

class PaginatedElement(position: Vector2i, size: Vector2i, parentSection: InventorySection) : Element(position, size, parentSection) {

	override fun onClick(event: InventoryClickEvent, player: Player, position: Vector2i) {
		event.cancel()
	}

	override fun setup() {

	}

	companion object : Element.Type<PaginatedElement> {
		override fun create(position: Vector2i, size: Vector2i, parentSection: InventorySection): PaginatedElement {
			return PaginatedElement(position, size, parentSection)
		}
	}

}