package blue.sparse.minecraft.persistent.extensions

import blue.sparse.minecraft.persistent.PersistentModule
import org.bukkit.block.Block

val Block.persistent
	get() = PersistentModule.getBlockPersistent(this)