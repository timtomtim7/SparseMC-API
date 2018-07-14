import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

object InventoryListener : Listener {

    @EventHandler
    fun InventoryClickEvent.onClick() {
        val inventory = this.clickedInventory ?: return
        if (slot < 0 || slot >= inventory.size)
            return

        val menu = inventory.holder as? Menu ?: return
        val menuItem = menu.menuItems.find { it.slot == slot } ?: return

        isCancelled = !menuItem.removable
        menuItem.clickCallback()
    }

}