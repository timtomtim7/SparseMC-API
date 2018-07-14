package blue.sparse.minecraft.inventory.menu

import blue.sparse.minecraft.core.extensions.event.cancel
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent

object MenuListener : Listener {

	@EventHandler
	fun onInventoryClick(event: InventoryClickEvent) {
		val inventory = event.inventory ?: return
		val clickedInv = event.clickedInventory ?: return
		val holder = inventory.holder as? Menu ?: return
		val clickedHolder = clickedInv.holder as? Menu
		val player = event.whoClicked as? Player ?: return

		if (clickedHolder == null) {
			//TODO: Stuff
			if (event.isShiftClick)
				event.cancel()

			return
		}

		val slot = event.slot
		val x = slot % 9
		val y = slot / 9

		try {
			holder.onClick(event, player, Vector2i(x, y))
		} catch (t: Throwable) {
			event.cancel()
			System.err.println("Error in SparseMC-API menu click handler:")
			t.printStackTrace()
		}
	}

	@EventHandler
	fun onInventoryDrag(event: InventoryDragEvent) {
		(event.inventory ?: return).holder as? Menu ?: return

		//TODO: Re-enable item dragging?

		event.cancel()
	}

}