package blue.sparse.minecraft.persistent.extensions

import blue.sparse.minecraft.persistent.PersistentModule
import org.bukkit.World

val World.persistent
	get() = PersistentModule.getWorldPersistent(this)