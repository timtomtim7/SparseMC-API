package blue.sparse.minecraft.inventory.menu

import blue.sparse.minecraft.core.extensions.event.cancel
import blue.sparse.minecraft.core.extensions.server
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.*

object MenuListener : Listener {

	@EventHandler
	fun onClickPlayerInventory(event: InventoryClickEvent) {
		val inventory = event.inventory ?: return
		val holder = inventory.holder as? Menu ?: return

		if(event.clickedInventory == inventory)
			return

		holder.clickPlayerInventoryCallback(event)
	}

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
		if ((event.inventory ?: return).holder !is Menu) return

		//TODO: Re-enable item dragging?

		event.cancel()
	}

	@EventHandler
	fun onInventoryClose(event: InventoryCloseEvent) {
		val inventory = event.inventory ?: return
		val clickedInv = event.inventory ?: return
		val holder = inventory.holder as? Menu ?: return

		holder.closed()
	}

	internal fun tick() {
		for (player in server.onlinePlayers) {
			val top = player.openInventory.topInventory ?: continue
			val holder = top.holder as? Menu ?: continue
			if (holder.tick(holder)) {
				player.updateInventory()
			}
		}
	}

}