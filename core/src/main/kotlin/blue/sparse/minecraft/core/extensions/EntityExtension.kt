package blue.sparse.minecraft.core.extensions

import org.bukkit.block.Block
import org.bukkit.entity.Entity

val Entity.block: Block
	get() = location.block
