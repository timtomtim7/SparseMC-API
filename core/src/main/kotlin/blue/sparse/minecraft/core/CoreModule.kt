package blue.sparse.minecraft.core

import blue.sparse.minecraft.SparseMCPlugin
import blue.sparse.minecraft.core.extensions.sendMessage
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.module.Module
import blue.sparse.minecraft.module.ModuleDefinition
import blue.sparse.minecraft.plugin.SparsePluginLoader
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@ModuleDefinition
object CoreModule: Module, Listener {

	override fun onEnable() {
		server.pluginManager.registerEvents(this, SparseMCPlugin.getPlugin())
		server.pluginManager.registerInterface(SparsePluginLoader::class.java)
	}

	@EventHandler
	fun PlayerJoinEvent.onPlayerJoin() {
		player.sendMessage(SparseMCPlugin.getPlugin(), "test")
	}

}