package blue.sparse.minecraft.nms.placeholders

import blue.sparse.minecraft.nms.NMSModule
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.plugin.Plugin

interface ItemReplacer {
	fun replace(player: Player, item: ItemStack): ItemStack?

	companion object {

		fun register(plugin: Plugin, replacer: ItemReplacer): Boolean {
			return NMSModule.placeholderNMS.registerItemReplacer(plugin, replacer)
		}

		inline fun register(plugin: Plugin, crossinline replacer: (ItemStack) -> ItemStack?): Boolean {
			return register(plugin) { p, i -> replacer(i) }
		}

		inline fun register(plugin: Plugin, crossinline replacer: (Player, ItemStack) -> ItemStack?): Boolean {
			return register(plugin, object: ItemReplacer {
				override fun replace(player: Player, item: ItemStack): ItemStack? {
					return replacer(player, item)
				}
			})
		}

	}
}