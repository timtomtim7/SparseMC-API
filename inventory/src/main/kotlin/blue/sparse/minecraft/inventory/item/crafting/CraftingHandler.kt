package blue.sparse.minecraft.inventory.item.crafting

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

abstract class CraftingHandler {

	abstract fun getResult(player: Player, matrix: Array<out ItemStack>): ItemStack?

	abstract fun onCraft(player: Player, matrix: Array<out ItemStack>, result: ItemStack): Boolean

}