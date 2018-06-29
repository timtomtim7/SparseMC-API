package blue.sparse.minecraft.nms.api

import blue.sparse.minecraft.nms.placeholders.ItemReplacer
import org.bukkit.plugin.Plugin

interface PlaceholderNMS: NMSHandler {
	fun registerItemReplacer(plugin: Plugin, replacer: ItemReplacer): Boolean
}