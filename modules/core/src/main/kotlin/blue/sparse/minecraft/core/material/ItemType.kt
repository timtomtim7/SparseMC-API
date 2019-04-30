package blue.sparse.minecraft.core.material

import org.bukkit.NamespacedKey

abstract class ItemType(val key: NamespacedKey, val maxStackSize: Int = 64) {
	constructor(id: String, maxStackSize: Int = 64): this(NamespacedKey.minecraft(id), maxStackSize)
}