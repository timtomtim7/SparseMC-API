package blue.sparse.minecraft.inventory.menu

import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.core.i18n.LocalizedString
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.plugin.Plugin

class Menu(val plugin: Plugin, val player: Player, rows: Int) : ElementContainer(Vector2i(9, rows)), InventoryHolder {
	private var _inventory = createInventory()
	private var closeCallback: () -> Unit = {}

	override val section: InventorySection
		get() = InventorySection(_inventory, Vector2i(0, 0), contentSize)

	var title: LocalizedString? = null
		set(value) {
			field = value
			// TODO: This could cause problems if the menu was already open.
			_inventory = createInventory()
		}

	private fun createInventory(): Inventory {
		val slotCount = contentSize.x * contentSize.y
		return server.createInventory(
			this, slotCount, title?.get(player) ?: "!!!"
		)
	}

	fun title(title: LocalizedString?) {
		this.title = title
	}

	fun title(key: String, vararg placeholders: Pair<String, Any>) {
		title(LocalizedString(plugin, key, placeholders.toMap()))
	}

	fun onClose(body: () -> Unit) {
		closeCallback = body
	}

	override fun getInventory() = _inventory
}