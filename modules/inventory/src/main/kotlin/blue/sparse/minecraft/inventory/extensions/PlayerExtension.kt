package blue.sparse.minecraft.inventory.extensions

import blue.sparse.minecraft.inventory.menu.Menu
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

fun Player.menu(plugin: Plugin, rows: Int = 3, setup: Menu.() -> Unit): Menu {
	val menu = Menu(plugin, this, rows).apply(setup)
	player.openInventory(menu.inventory)
	return menu
}

fun Player.menu(plugin: Plugin, rows: Int = 3, titleLocale: String, setup: Menu.() -> Unit): Menu {
	val menu = Menu(plugin, this, rows).apply { title(titleLocale) }.apply(setup)
	player.openInventory(menu.inventory)
	return menu
}