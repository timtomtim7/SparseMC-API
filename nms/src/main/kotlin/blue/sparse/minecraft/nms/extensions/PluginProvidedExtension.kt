package blue.sparse.minecraft.nms.extensions

import blue.sparse.minecraft.core.PluginProvided
import blue.sparse.minecraft.nms.placeholders.ItemReplacer

fun PluginProvided<*>.registerItemReplacer(replacer: ItemReplacer) {
	ItemReplacer.register(plugin, replacer)
}