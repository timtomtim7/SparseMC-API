package blue.sparse.minecraft.inventory.item

import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.inventory.InventoryModule
import blue.sparse.minecraft.persistent.extensions.persistent
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData

abstract class CustomBlockType(
        id: String,
        defaultIcon: MaterialData,
        canStack: Boolean
): CustomItemType(id, defaultIcon, canStack) {

    constructor(
            id: String,
            defaultIcon: Material,
            canStack: Boolean
    ): this(id, MaterialData(defaultIcon), canStack)

    val item get() = create()

    open fun onBlockPlace(event: BlockPlaceEvent, block: Block, item: ItemStack, player: Player) {}
    open fun onBlockBreak(event: BlockBreakEvent, block: Block, player: Player?) {}
    open fun onBlockRightClick(event: PlayerInteractEvent, block: Block, player: Player) {}
    open fun onBlockLeftClick(event: PlayerInteractEvent, block: Block, player: Player) {}
    open fun onBlockExplode(event: BlockExplodeEvent, block: Block) {}
    open fun onPistonMove(event: BlockPistonEvent, block: Block) {}
    open fun onBlockTick(block: Block) {}

    fun isInstance(block: Block) = CustomBlockType.getType(block) == this

    companion object {
        fun getType(block: Block): CustomBlockType? {
            val pers = block.persistent(InventoryModule.plugin)
            val customBlock = pers.optionalCompound("SparseCustomBlock") ?: return null
            val id = customBlock.optionalString("id") ?: return null
            return CustomItemType[id] as? CustomBlockType
        }
    }
}