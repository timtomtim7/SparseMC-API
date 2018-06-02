package blue.sparse.minecraft.inventory.extensions

import org.bukkit.inventory.meta.ItemMeta

fun ItemMeta.displayName(vararg parts: Any?) {
	displayName = parts.joinToString("")
}

fun ItemMeta.appendLore(vararg parts: Any?) {
	val newLore = (lore as? MutableList<String> ?: lore?.toMutableList() ?: ArrayList<String>())
	newLore.addAll(parts.joinToString("").split("\n"))
	lore = newLore
}