package blue.sparse.minecraft.inventory.menu.element

import blue.sparse.minecraft.inventory.menu.InventorySection
import blue.sparse.minecraft.inventory.menu.Vector2i
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class EditableElement(position: Vector2i, size: Vector2i, parentSection: InventorySection) : Element(position, size, parentSection) {

	val items: List<ItemStack?>
		get() = section.map { section[it] }

	operator fun get(slot: Int): ItemStack? {
		return section[section.getAbsolutePosition(slot)]
	}

	operator fun set(slot: Int, item: ItemStack?) {
		section[section.getAbsolutePosition(slot)] = item
	}

	override fun onClick(event: InventoryClickEvent, player: Player, position: Vector2i) {}

	override fun setup() {}

	companion object : Element.Type<EditableElement> {
		override fun create(position: Vector2i, size: Vector2i, parentSection: InventorySection): EditableElement {
			return EditableElement(position, size, parentSection)
		}
	}

}