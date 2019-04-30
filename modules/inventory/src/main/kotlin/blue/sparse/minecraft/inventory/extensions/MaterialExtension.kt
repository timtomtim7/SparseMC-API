package blue.sparse.minecraft.inventory.extensions

import org.bukkit.Material
import org.bukkit.inventory.ItemStack

inline operator fun Material.invoke(
		amount: Int = 1,
		damage: Short = 0,
		body: ItemStack.() -> Unit
): ItemStack {
	return item(this,  amount, damage, body)
}