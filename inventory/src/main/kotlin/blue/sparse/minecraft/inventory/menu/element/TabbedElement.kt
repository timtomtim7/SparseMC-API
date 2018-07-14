package blue.sparse.minecraft.inventory.menu.element

import blue.sparse.minecraft.core.extensions.event.cancel
import blue.sparse.minecraft.inventory.menu.*
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

class TabbedElement(
		position: Vector2i,
		size: Vector2i,
		parentSection: InventorySection
) : Element(position, size, parentSection) {

	private val tabs = LinkedHashSet<Tab>()

	var tabsSide = Side.TOP
	var dividers: Array<out ItemStack>? = null
	val hasDividers get() = dividers != null

	var currentTab: Tab? = null
		private set

	val tabContentSection get() = section.subsection(tabOrigin, tabSize)

	inline fun divider(
			base: ItemStack = ItemStack(Material.STAINED_GLASS_PANE),
			body: ItemStack.() -> Unit = {}
	) {
		dividers = arrayOf(base.apply(body))
	}

	inline fun tab(icon: ItemStack = ItemStack(Material.STONE), body: Tab.() -> Unit = {}): Tab {
		val tab = addTab()
		tab.icon = icon
		return tab.apply(body)
	}

	inline fun tab(icon: Material, body: Tab.() -> Unit = {}): Tab {
		return tab(ItemStack(icon), body)
	}

	fun addTab(): Tab {
		currentTab?.save()
		val tab = Tab(tabSize, tabContentSection)
		currentTab = tab
		tabs.add(tab)
		return tab
	}

	override fun onClick(event: InventoryClickEvent, player: Player, position: Vector2i) {
		val slot = section.getSlot(position)
		val tabPosition = tabContentSection.getPosition(slot)
		if (tabPosition in tabContentSection) {
			val currentTab = currentTab ?: return event.cancel()

			currentTab.onClick(event, player, tabPosition)
		} else {
			event.cancel()

			val newTab = tabs.withIndex()
					.find { getTabIconPosition(it.index) == position }
					?.value ?: return

			currentTab?.save()
			currentTab = newTab
			newTab.load()
			newTab.selectedCallback()
		}
	}

	override fun setup() {
		val div = dividers
		if (div != null) {
			var i = 0
			while (true) {
				val pos = getTabIconPosition(i++, 1)
				if (pos !in this)
					break

				section[pos] = div[i % div.size]
			}
		}

		for ((i, tab) in tabs.withIndex()) {
			val pos = getTabIconPosition(i)
			section[pos] = tab.icon
		}
	}

	companion object : Element.Type<TabbedElement> {
		override fun create(position: Vector2i, size: Vector2i, parentSection: InventorySection): TabbedElement {
			return TabbedElement(position, size, parentSection)
		}
	}

	inner class Tab internal constructor(
			contentSize: Vector2i,
			override val section: InventorySection
	) : ElementContainer(contentSize) {

		private var saved: Map<Vector2i, ItemStack?>? = null

		var icon = ItemStack(Material.STONE)
		var selectedCallback = {}

		init {
			section.clear()
		}

		fun onSelected(selectedCallback: () -> Unit) {
			this.selectedCallback = selectedCallback
		}

		inline fun icon(base: ItemStack = icon, body: ItemStack.() -> Unit) {
			icon = base.apply(body)
		}

		@PublishedApi
		internal fun save() {
			saved = section.map { it to section[it] }.toMap()
		}

		internal fun load() {
			section.clear()
			val saved = saved
			if (saved != null) {
				for ((pos, item) in saved)
					section[pos] = item
			}
		}
	}

	private val tabOrigin: Vector2i
		get() {
			val amt = if (hasDividers) 2 else 1
			return when (tabsSide) {
				Side.TOP -> Vector2i(0, amt)
				Side.BOTTOM -> Vector2i(0, 0)//Vector2i(0, (size.y - 1) - amt)
				Side.LEFT -> Vector2i(amt, 0)
				Side.RIGHT -> Vector2i(0, 0)//Vector2i((size.x - 1) - amt, 0)
			}
		}

	private val tabSize: Vector2i
		get() {
			val amt = if (hasDividers) 2 else 1
			return when (tabsSide) {
				Side.TOP, Side.BOTTOM -> Vector2i(size.x, size.y - amt)
				Side.LEFT, Side.RIGHT -> Vector2i(size.x - amt, size.y)
			}
		}

	private fun getTabIconPosition(tab: Int, offset: Int = 0): Vector2i {
		return when (tabsSide) {
			Side.TOP -> Vector2i(tab, offset)
			Side.BOTTOM -> Vector2i(tab, (size.y - 1) - offset)
			Side.LEFT -> Vector2i(offset, tab)
			Side.RIGHT -> Vector2i((size.x - 1) - offset, tab)
		}
	}

	enum class Side(val axis: Vector2i) {
		TOP(Vector2i(0, -1)),
		BOTTOM(Vector2i(0, 1)),
		LEFT(Vector2i(-1, 0)),
		RIGHT(Vector2i(1, 0));

		val perp = Vector2i(1, 1) - axis.abs()
	}


//	enum class Side(val getPosition: (tab: Int, offset: Int, size: Vector2i) -> Vector2i) {
//		TOP({ tab, offset, _ -> Vector2i(tab, offset) }),
//		LEFT({ tab, offset, _ -> Vector2i(offset, tab) }),
//		BOTTOM({ tab, offset, size -> Vector2i(tab, (size.y - 1) - offset) }),
//		RIGHT({ tab, offset, size -> Vector2i((size.x - 1) - offset, tab) });
//	}
}