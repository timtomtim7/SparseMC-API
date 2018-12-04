package blue.sparse.minecraft.inventory.extensions

import blue.sparse.minecraft.core.extensions.getPluginLocale
import blue.sparse.minecraft.core.i18n.PluginLocale
import blue.sparse.minecraft.inventory.item.CustomItemType
import blue.sparse.minecraft.plugin.SparsePlugin
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.plugin.Plugin

val ItemStack.customItemType: CustomItemType?
	get() = CustomItemType.getType(this)

fun ItemStack?.notEmptyOrNull(): ItemStack? {
	return if (this == null || this.type == Material.AIR) null else this
}

//inline fun <R> ItemStack.editMeta(body: ItemMeta.() -> R): R {
//	val meta = itemMeta
//	val result = meta.run(body)
//	itemMeta = meta
//
//	return result
//}

inline fun ItemStack.editMeta(body: ItemMeta.() -> Unit) {
	itemMeta = itemMeta.apply(body)
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
		body: ItemStack.() -> Unit = {}
): ItemStack {
	return ItemStack(type, amount, damage).apply(body)
}

fun ItemStack.localize(
		plugin: Plugin,
		player: Player,
		localizedName: String,
		localizedNameReplacements: Map<String, Any>,
		localizedLore: String,
		localizedLoreReplacements: Map<String, Any>
): ItemStack {
	val locale = player.getPluginLocale(plugin)
	return apply {
		editMeta {
			displayName = locale[localizedName, localizedNameReplacements]
			lore = locale[localizedLore, localizedLoreReplacements]?.lines()
		}
	}
}

fun ItemStack.localize(
		plugin: Plugin,
		localizedName: String,
		localizedNameReplacements: Map<String, Any>,
		localizedLore: String,
		localizedLoreReplacements: Map<String, Any>
): ItemStack {
	val locale = PluginLocale.default(plugin)
	return apply {
		editMeta {
			displayName = locale[localizedName, localizedNameReplacements]
			lore = locale[localizedLore, localizedLoreReplacements]?.lines()
		}
	}
}
