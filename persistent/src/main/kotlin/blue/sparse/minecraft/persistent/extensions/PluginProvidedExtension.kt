package blue.sparse.minecraft.persistent.extensions

import blue.sparse.minecraft.core.PluginProvided
import blue.sparse.minecraft.persistent.PersistentModule
import blue.sparse.minecraft.persistent.persistents.PluginPersistent

val PluginProvided<*>.persistent: PluginPersistent
	get() = PersistentModule.getPluginPersistent(this.plugin)