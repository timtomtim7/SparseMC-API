package blue.sparse.minecraft.inventory.menu.element

import blue.sparse.minecraft.core.extensions.event.cancel
import blue.sparse.minecraft.inventory.menu.ElementContainer
import blue.sparse.minecraft.inventory.menu.Vector2i
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class InventoryElement(position: Vector2i, size: Vector2i, parent: ElementContainer) : Element(position, size, parent) {

	val items: List<ItemStack?>
		get() = section.map { section[it] }

	var editable: Boolean = false

	private var onClickCallback: InventoryClickEvent.() -> Unit = {}

	fun onClick(callback: InventoryClickEvent.() -> Unit) {
		this.onClickCallback = callback
	}

	operator fun get(slot: Int): ItemStack? {
		return section[section.getPosition(slot)]
	}

	operator fun get(position: Vector2i): ItemStack? {
		return section[position]
	}

	operator fun set(slot: Int, item: ItemStack?) {
		section[section.getPosition(slot)] = item
	}

	operator fun set(position: Vector2i, item: ItemStack?) {
		section[position] = item
	}

	override fun onClick(event: InventoryClickEvent, player: Player, position: Vector2i) {
		if(!editable)
			event.cancel()
		onClickCallback(event)
	}

	override fun setup() {}

	companion object : Element.Type<InventoryElement> {
		override fun create(position: Vector2i, size: Vector2i, parent: ElementContainer): InventoryElement {
			return InventoryElement(position, size, parent)
		}
	}

}