package blue.sparse.minecraft.nms.v1_7_R4

import blue.sparse.minecraft.nms.api.NMSHandler
import blue.sparse.minecraft.nms.api.PlaceholderNMS
import blue.sparse.minecraft.nms.placeholders.ItemReplacer
import org.bukkit.plugin.Plugin

class PlaceholderImpl: PlaceholderNMS, NMSHandler {
	override fun registerItemReplacer(plugin: Plugin, replacer: ItemReplacer): Boolean {
		TODO("not implemented")
	}
}