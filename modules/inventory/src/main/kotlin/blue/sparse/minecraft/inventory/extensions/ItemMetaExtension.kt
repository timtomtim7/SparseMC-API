package blue.sparse.minecraft.inventory.extensions

import blue.sparse.minecraft.core.extensions.getPluginLocale
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin

fun ItemMeta.displayName(plugin: Plugin, player: Player, key: String, vararg placeholders: Pair<String, Any>) {
	displayName = player.getPluginLocale(plugin).get(key, *placeholders)
}

fun ItemMeta.displayName(vararg parts: Any?) {
	displayName = parts.joinToString("")
}

fun ItemMeta.appendLore(vararg parts: Any?) {
	val newLore = (lore as? MutableList<String> ?: lore?.toMutableList() ?: ArrayList())
	newLore.addAll(parts.filterNotNull().joinToString("").split("\n"))
	lore = newLore
}

fun ItemMeta.prependLore(vararg parts: Any?) {
	val newLore = (lore as? MutableList<String> ?: lore?.toMutableList() ?: ArrayList())
	val lines = parts.filterNotNull().joinToString("").split("\n")
	if (newLore.isEmpty())
		newLore.addAll(lines)
	else
		newLore.addAll(0, lines)
	lore = newLore
}

fun ItemMeta.appendLore(plugin: Plugin, player: Player, key: String, vararg placeholders: Pair<String, Any>) {
	appendLore(player.getPluginLocale(plugin).get(key, *placeholders))
}

fun ItemMeta.enchantedEffect() {
	if (hasEnchants())
		return

	addEnchant(Enchantment.LURE, 1, true)
	addItemFlags(ItemFlag.HIDE_ENCHANTS)
}