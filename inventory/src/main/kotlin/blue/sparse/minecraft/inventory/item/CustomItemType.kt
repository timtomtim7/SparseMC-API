package blue.sparse.minecraft.inventory.item

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.inventory.extensions.editMeta
import blue.sparse.minecraft.inventory.extensions.notEmptyOrNull
import blue.sparse.minecraft.nms.extensions.editNBT
import blue.sparse.minecraft.nms.extensions.nbt
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.*
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.material.MaterialData
import java.util.concurrent.ThreadLocalRandom

abstract class CustomItemType(
        val id: String,
        val defaultIcon: MaterialData,
        val canStack: Boolean = true
) {

    constructor(id: String, material: Material, canStack: Boolean) : this(id, MaterialData(material), canStack)

    protected fun create(vararg args: Any): ItemStack {
        val item = defaultIcon.toItemStack(1)

        item.editNBT {
            compound("sparseCustomItem") {
                string("id", id)
                if (!canStack)
                    long("uid", ThreadLocalRandom.current().nextLong())

                compound("data", newData(args))
            }
        }
        item.editMeta { newMeta(args, this) }

        return item
    }

    fun isInstance(item: ItemStack): Boolean {
        val nbt = item.nbt
        if ("sparseCustomItem" !in nbt)
            return false

        return nbt.compound("sparseCustomItem").string("id") == this.id
    }


    open fun onWorldRightClick(event: PlayerInteractEvent, player: Player, item: ItemStack, block: Block?, entity: Entity?) {}
    open fun onWorldRightClickBlock(event: PlayerInteractEvent, player: Player, item: ItemStack, block: Block) {}
    open fun onWorldRightClickEntity(event: PlayerInteractAtEntityEvent, player: Player, item: ItemStack, entity: Entity) {}
    open fun onWorldRightClickNothing(event: PlayerInteractEvent, player: Player, item: ItemStack) {}

    open fun onWorldLeftClick(event: PlayerInteractEvent, player: Player, item: ItemStack, block: Block?, entity: Entity?) {}
    open fun onWorldLeftClickBlock(event: PlayerInteractEvent, player: Player, item: ItemStack, block: Block) {}
    open fun onWorldLeftClickEntity(event: PlayerInteractAtEntityEvent, player: Player, item: ItemStack, entity: Entity) {}
    open fun onWorldLeftClickNothing(event: PlayerInteractEvent, player: Player, item: ItemStack) {}

    open fun onPlayerDrop(event: PlayerDropItemEvent, player: Player, item: ItemStack, entity: Item) {}
    open fun onPlayerPickup(event: PlayerPickupItemEvent, player: Player, item: ItemStack, entity: Item) {}
    open fun onDrop(event: ItemSpawnEvent, item: ItemStack, entity: Item) {}

    open fun onDisplay(player: Player, item: ItemStack): ItemStack? = null

    /**
     * Called when this item is attempted to be swapped or stacked with another in an inventory.
     */
    open fun onInventoryClickOtherOnThis(event: InventoryClickEvent, player: Player, item: ItemStack, other: ItemStack) {}

    open fun onInventoryClickThisOnOther(event: InventoryClickEvent, player: Player, item: ItemStack, other: ItemStack) {}
    open fun onInventoryClickSplit(event: InventoryClickEvent, player: Player, item: ItemStack) {}

    open fun onTickDropped(item: Item) {}
    open fun onTickPlayerInventory(player: Player, item: ItemStack, slot: Int) {}

    protected fun getData(item: ItemStack): Compound {
        instanceCheck(item)

        return item.nbt.compound("sparseCustomItem").compound("data")
    }

    protected open fun newData(args: Array<out Any>): Compound {
        return Compound()
    }

    protected open fun newMeta(args: Array<out Any>, meta: ItemMeta) {}

    protected fun instanceCheck(item: ItemStack) {
        if (!isInstance(item))
            throw IllegalArgumentException("Item is not an instance of this custom item type (${javaClass.name})")
    }

    companion object {

        private val registered = HashMap<String, CustomItemType>()

        fun register(type: CustomItemType): Boolean {
            if (type.id in registered)
                return false

            registered[type.id] = type
            return true
        }

        fun unregister(type: CustomItemType) = registered.remove(type.id) != null

        operator fun get(id: String) = registered[id]

        fun getType(item: ItemStack): CustomItemType? {
            if (item.notEmptyOrNull() == null)
                return null

            val nbt = item.nbt
            val custom = nbt.optionalCompound("sparseCustomItem") ?: return null
            val id = custom.optionalString("id") ?: return null

            return get(id)
        }

    }

}