package blue.sparse.minecraft.inventory.menu

import blue.sparse.minecraft.core.extensions.event.cancel
import blue.sparse.minecraft.core.i18n.PluginLocale
import blue.sparse.minecraft.inventory.extensions.editMeta
import blue.sparse.minecraft.inventory.extensions.prependLore
import blue.sparse.minecraft.inventory.menu.element.Element
import blue.sparse.minecraft.inventory.menu.element.StaticElement
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemStack

abstract class ElementContainer(val contentSize: Vector2i, open val locale: PluginLocale) {
	protected val elements = LinkedHashSet<Element>()
	abstract val section: InventorySection

	operator fun get(position: Vector2i): Element? {
		return elements.find { position in it }
	}

	open fun add(element: Element): Boolean {
		//TODO: Check for intersections?
		if (!elements.add(element))
			return false
		setup(element)
		return true
	}

	fun remove(element: Element): Boolean {
		return elements.remove(element)
	}

	fun isVisible(element: Element): Boolean {
		return element in elements
	}

	inline fun <T : Element> element(
			type: Element.Type<T>,
			position: Vector2i,
			size: Vector2i = 1 x 1,
			body: T.() -> Unit = {}
	): T {
//		println("adding element $position (slot ${section.getAbsoluteSlot(position)}) to container $this")
		val element = type.create(position, size, this).apply(body)
		add(element)
		return element
	}

	inline fun item(
			position: Vector2i,
			icon: ItemStack = ItemStack(Material.STONE),
			localeName: String? = null,
			localeLore: String? = null,
			vararg placeholders: Pair<String, Any>,
			size: Vector2i = 1 x 1,
			body: StaticElement.() -> Unit = {}
	) {
		element(StaticElement, position, size) {
			if (localeName != null || localeLore != null) {
				icon.editMeta {
					if(localeName != null)
						displayName = locale.get(localeName, *placeholders) ?: displayName
					if(localeLore != null) {
						val lore = locale.get(localeLore, *placeholders)
						if(lore != null) {
							prependLore(lore)
						}
//						lore = locale.get(localeLore, *placeholders)?.lines() ?: lore
					}
				}
			}
			this.icon = icon
			body()
		}
	}

	inline fun item(
			position: Vector2i,
			icon: Material,
			localeName: String? = null,
			localeLore: String? = null,
			vararg placeholders: Pair<String, Any>,
			size: Vector2i = 1 x 1,
			body: StaticElement.() -> Unit = {}
	) {
		item(
				position,
				ItemStack(icon),
				localeName,
				localeLore,
				placeholders = *placeholders,
				size = size,
				body = body
		)
	}

	open fun onClick(
			event: InventoryClickEvent,
			player: Player,
			position: Vector2i
	) {
		val element = get(position) ?: return event.cancel()
		element.onClick(event, player, position - element.position)
	}

	private fun setup(element: Element) {
		element.setup()
	}

	infix fun Int.x(y: Int) = Vector2i(this, y)
}
