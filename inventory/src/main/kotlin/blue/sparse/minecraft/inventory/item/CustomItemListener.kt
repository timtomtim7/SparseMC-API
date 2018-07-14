package blue.sparse.minecraft.inventory.item

import blue.sparse.minecraft.core.extensions.event.isLeftClick
import blue.sparse.minecraft.core.extensions.event.isRightClick
import blue.sparse.minecraft.inventory.extensions.customItemType
import blue.sparse.minecraft.inventory.extensions.notEmptyOrNull
import blue.sparse.minecraft.nms.placeholders.ItemReplacer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.*
import org.bukkit.inventory.ItemStack

object CustomItemListener : Listener, ItemReplacer {

	@EventHandler
	fun onPlayerInteract(event: PlayerInteractEvent) {
		val item = event.item ?: return
		val type = CustomItemType.getType(item) ?: return

		val action = event.action
		if (action.isLeftClick()) {
			type.onWorldLeftClick(event, event.player, item, event.clickedBlock, null)
			if (event.clickedBlock == null)
				type.onWorldLeftClickNothing(event, event.player, item)
			else
				type.onWorldLeftClickBlock(event, event.player, item, event.clickedBlock)
		} else if (action.isRightClick()) {
			type.onWorldRightClick(event, event.player, item, event.clickedBlock, null)
			if (event.clickedBlock == null)
				type.onWorldRightClickNothing(event, event.player, item)
			else
				type.onWorldRightClickBlock(event, event.player, item, event.clickedBlock)
		}
	}

	@EventHandler
	fun onPlayerDrop(event: PlayerDropItemEvent) {
		val entity = event.itemDrop ?: return
		val item = entity.itemStack ?: return
		val type = CustomItemType.getType(item) ?: return

		type.onPlayerDrop(event, event.player, item, entity)
	}

	@EventHandler
	fun onPlayerPickup(event: PlayerPickupItemEvent) {
		val entity = event.item ?: return
		val item = entity.itemStack ?: return
		val type = CustomItemType.getType(item) ?: return

		type.onPlayerPickup(event, event.player, item, entity)
	}

	@EventHandler
	fun onDrop(event: ItemSpawnEvent) {
		val entity = event.entity ?: return
		val item = entity.itemStack ?: return
		val type = CustomItemType.getType(item) ?: return

		type.onDrop(event, item, entity)
	}

	@EventHandler
	fun onInventoryClick(event: InventoryClickEvent) {
		val player = event.whoClicked as? Player ?: return
		val cursor = event.cursor.notEmptyOrNull()
		val current = event.currentItem.notEmptyOrNull()

		if (cursor != null && current != null) {
			val cursorType = CustomItemType.getType(cursor)
			val currentType = CustomItemType.getType(current)

			cursorType?.onInventoryClickThisOnOther(event, player, cursor, current)
			currentType?.onInventoryClickOtherOnThis(event, player, current, cursor)
		}

		if(cursor == null && current != null && event.action == InventoryAction.PICKUP_HALF) {
			val currentType = CustomItemType.getType(current) ?: return

			currentType.onInventoryClickSplit(event, player, current)
		}
	}

	override fun replace(player: Player, item: ItemStack): ItemStack? {
		return item.customItemType?.onDisplay(player, item)
	}
}