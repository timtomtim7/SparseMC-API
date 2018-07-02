package blue.sparse.minecraft.inventory.extensions

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

fun ItemStack?.notEmptyOrNull(): ItemStack? {
	return if (this == null || this.type == Material.AIR) null else this
}

inline fun <R> ItemStack.editMeta(body: ItemMeta.() -> R): R {
	val meta = itemMeta
	val result = meta.run(body)
	itemMeta = meta

	return result
}

inline fun <reified M : ItemMeta> ItemStack.editMetaTyped(body: M.() -> Unit) {
	val meta = itemMeta as M
	val result = meta.run(body)
	itemMeta = meta
	return result
}

inline fun item(
		type: Material = Material.AIR,
		amount: Int = 1,
		damage: Short = 0,
		body: ItemStack.() -> Unit
): ItemStack {
	return ItemStack(type, amount, damage).apply(body)
}
