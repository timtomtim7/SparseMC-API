package blue.sparse.minecraft.inventory.item

import blue.sparse.minecraft.inventory.InventoryModule
import blue.sparse.minecraft.persistent.extensions.persistent
import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.*
import org.bukkit.event.player.PlayerInteractEvent

object CustomBlockListener : Listener {

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val type = CustomBlockType.getType(event.block) ?: return
        event.block.persistent(InventoryModule.plugin) {
            remove("SparseCustomBlock")
        }
        type.onBlockBreak(event, event.block, event.player)
    }

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        val itemType = CustomItemType.getType(event.itemInHand) ?: return

        event.block.persistent(InventoryModule.plugin) {
            compound("SparseCustomBlock") {
                string("id", itemType.id)
            }
        }

        val type = CustomBlockType.getType(event.block) ?: return
        type.onBlockPlace(event, event.block, event.itemInHand, event.player)
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