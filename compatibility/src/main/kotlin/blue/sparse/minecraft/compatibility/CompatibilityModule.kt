package blue.sparse.minecraft.compatibility

import blue.sparse.minecraft.compatibility.factions.extensions.claimedBy
import blue.sparse.minecraft.compatibility.factions.extensions.contains
import blue.sparse.minecraft.compatibility.factions.extensions.faction
import blue.sparse.minecraft.compatibility.factions.extensions.notClaimedBy
import blue.sparse.minecraft.module.AbstractModule
import blue.sparse.minecraft.module.ModuleType
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent

object CompatibilityModule : AbstractModule(ModuleType.COMPATIBILITY) {
	init {
		val player = Bukkit.getPlayer("Tom1024")

		val chunk = player.world.getChunkAt(0, 0)

		val listener = object: Listener {
			@EventHandler
			fun onBlockPlace(event: BlockPlaceEvent) {
				val block = event.block
				val player = event.player

				if(block notClaimedBy player) {
					event.isCancelled = true
					player.sendMessage("you can only place blocks in your land.")
				}
			}
		}

	}
}