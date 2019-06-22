package blue.sparse.minecraft.inventory.menu.element

import blue.sparse.minecraft.core.extensions.event.cancel
import blue.sparse.minecraft.inventory.extensions.displayName
import blue.sparse.minecraft.inventory.extensions.editMeta
import blue.sparse.minecraft.inventory.menu.*
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack
import kotlin.math.max
import kotlin.math.min

class PaginatedElement<T : PaginatedElement.PageItem>(
		position: Vector2i,
		size: Vector2i,
		parent: ElementContainer
) : Element(position, size, parent) {

	class InventoryAction internal constructor(val leftClick: Boolean, val rightClick: Boolean, val shiftClick: Boolean)

	private val items = ArrayList<T>()

	var nextPageIconPosition = Vector2i(size.x - 1, 0)
	var prevPageIconPosition = Vector2i(0, 0)

	var nextPageIcon = ItemStack(Material.PAPER).apply {
		editMeta { displayName(ChatColor.GREEN, "Next Page ->") }
	}

	var prevPageIcon = ItemStack(Material.PAPER).apply {
		editMeta { displayName(ChatColor.GREEN, "<- Previous Page") }
	}

	val itemsPerPage = (size.x * size.y) - 2

	var page = 0
		set(value) {
			val clamped = min(max(value, 0), pageCount)
			field = clamped
			setupPage(clamped)
		}

	val pageCount: Int
		get() = (items.size + (items.size - items.size % itemsPerPage)) / itemsPerPage

	val hasPrevPage: Boolean
		get() = page > 0
	val hasNextPage: Boolean
		get() = page < pageCount - 1

	override fun setup() {
		setupPage(page)
	}

	override fun onTick(menu: Menu): Boolean {
		var updated = false

		var index = page * itemsPerPage
		for (pos in section) {
			if ((hasNextPage && pos == nextPageIconPosition) || (hasPrevPage && pos == prevPageIconPosition))
				continue
			val item = items.getOrNull(index++) ?: break
			if (item.onTick(menu)) {
				section[pos] = item.icon
				updated = true
			}
		}

		return updated
	}

	private fun setupPage(page: Int) {
		section.clear()
		if(hasNextPage)
			section[nextPageIconPosition] = nextPageIcon
		if(hasPrevPage)
			section[prevPageIconPosition] = prevPageIcon

		var index = page * itemsPerPage
		for (pos in section) {
			if ((hasNextPage && pos == nextPageIconPosition) || (hasPrevPage && pos == prevPageIconPosition))
				continue
			val item = items.getOrNull(index++) ?: break
			section[pos] = item.icon
		}
	}

	override fun onClick(event: InventoryClickEvent, player: Player, position: Vector2i) {
		event.cancel()
		if (hasNextPage && position == nextPageIconPosition) {
			page++
			return
		} else if (hasPrevPage && position == prevPageIconPosition) {
			page--
			return
		}

		val sectionIndex = section.indexOf(position)
		if(sectionIndex == -1)
			return

		val index = (page * itemsPerPage) + sectionIndex
		val item = items.getOrNull(index) ?: return
		item.onClick(player)
		item.onClick(player, InventoryAction(event.isLeftClick, event.isRightClick, event.isShiftClick))
		item.onClick(player, event)
		section[position] = item.icon
	}

	fun addItem(item: T): Boolean {
		return items.add(item)
	}

	fun removeItem(item: T): Boolean {
		return items.remove(item)
	}

//	companion object : Element.Type<PaginatedElement> {
//		override fun create(position: Vector2i, size: Vector2i, parentSection: InventorySection): PaginatedElement {
//			return PaginatedElement(position, size, parentSection)
//		}
//	}

	class Type<T : PageItem> : Element.Type<PaginatedElement<T>> {
		override fun create(position: Vector2i, size: Vector2i, parent: ElementContainer): PaginatedElement<T> {
			return PaginatedElement(position, size, parent)
		}
	}

	interface PageItem {
		val icon: ItemStack

		fun onTick(menu: Menu): Boolean {
			return false
		}

		fun onClick(player: Player) {}
		fun onClick(player: Player, action: InventoryAction) {}
		fun onClick(player: Player, event: InventoryClickEvent) {}
	}
}