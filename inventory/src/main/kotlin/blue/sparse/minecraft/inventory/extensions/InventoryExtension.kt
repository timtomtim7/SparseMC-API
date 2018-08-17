package blue.sparse.minecraft.inventory.extensions

import org.bukkit.inventory.Inventory

val Inventory.isFull
	get() = this.firstEmpty() == -1