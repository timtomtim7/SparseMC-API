package blue.sparse.minecraft.inventory.item

import blue.sparse.minecraft.inventory.InventoryModule
import blue.sparse.minecraft.persistent.extensions.persistent
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.event.*
import org.bukkit.event.block.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.InventoryHolder

object CustomBlockListener : Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onBlockBreak(event: BlockBreakEvent) {
        val type = CustomBlockType.getType(event.block) ?: return

        event.block.persistent(InventoryModule.plugin) {
            remove("SparseCustomBlock")
        }

        val inventory = (event.block as? InventoryHolder)?.inventory

        type.onBlockBreak(event, event.block, event.player)

        if (event.isCancelled)
            return

        event.isCancelled = true
        event.block.type = Material.AIR
        event.block.world.dropItem(event.block.location, type.item)

        if (inventory != null) {
            for (drop in inventory.contents)
                event.block.world.dropItem(event.block.location, drop)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onBlockPlace(event: BlockPlaceEvent) {
        val type = CustomItemType.getType(event.itemInHand) as? CustomBlockType ?: return
        type.onBlockPlace(event, event.block, event.itemInHand, event.player)

        if(event.isCancelled)
            return

        event.block.persistent(InventoryModule.plugin) {
            compound("SparseCustomBlock") {
                string("id", type.id)
            }
        }

    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        val block = event.clickedBlock ?: return
        val action = event.action

        val type = CustomBlockType.getType(block) ?: return

        if (action == Action.RIGHT_CLICK_BLOCK)
            type.onBlockRightClick(event, block, event.player)
        if (action == Action.LEFT_CLICK_BLOCK)
            type.onBlockLeftClick(event, block, event.player)
    }

    @EventHandler
    fun onBlockExplode(event: BlockExplodeEvent) {
        for (block in event.blockList()) {
            val type = CustomBlockType.getType(block) ?: continue
            type.onBlockExplode(event, block)
        }
    }

    @EventHandler
    fun onPistonExtend(event: BlockPistonExtendEvent) = handlePistonShit(event, event.blocks)

    @EventHandler
    fun onPistonRetract(event: BlockPistonRetractEvent) = handlePistonShit(event, event.blocks)

    private fun handlePistonShit(event: BlockPistonEvent, list: List<Block>) {
        for (block in list) {
            val type = CustomBlockType.getType(block) ?: return
            type.onPistonMove(event, block)
        }
    }
}