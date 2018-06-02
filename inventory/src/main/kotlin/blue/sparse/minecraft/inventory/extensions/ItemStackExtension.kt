package blue.sparse.minecraft.inventory.extensions

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

fun ItemStack?.notEmptyOrNull(): ItemStack? {
	return if(this == null || this.type == Material.AIR) null else this
}

inline fun <R> ItemStack.editMeta(body: ItemMeta.() -> R): R {
	val meta = itemMeta
	val result = meta.run(body)
	itemMeta = meta

	return result
}