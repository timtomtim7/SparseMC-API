package blue.sparse.minecraft.core

import blue.sparse.minecraft.SparseMCPlugin
import blue.sparse.minecraft.core.extensions.send
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.module.Module
import blue.sparse.minecraft.module.ModuleDefinition
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent

@ModuleDefinition
object CoreModule: Module, Listener {

	override fun onEnable() {
		server.pluginManager.registerEvents(this, SparseMCPlugin.getPlugin())
	}

	@EventHandler
	fun PlayerJoinEvent.onPlayerJoin() {
		player.send(SparseMCPlugin.getPlugin(), "test")
	}
}