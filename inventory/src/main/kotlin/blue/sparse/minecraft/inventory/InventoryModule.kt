package blue.sparse.minecraft.inventory

import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.inventory.inventory.InventoryListener
import blue.sparse.minecraft.module.*

@ModuleDefinition
object InventoryModule: Module {
	override val type = ModuleType.INVENTORY
	
	override fun onEnable() {
		val pl = SparseMCAPIPlugin.getPlugin()
		val pm = server.pluginManager
		pm.registerEvents(InventoryListener, pl)
	}
}