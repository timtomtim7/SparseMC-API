package blue.sparse.minecraft.persistent.extensions

import blue.sparse.minecraft.persistent.PersistentModule
import org.bukkit.Chunk

val Chunk.persistent
	get() = PersistentModule.getChunkPersistent(this)