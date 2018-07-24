package blue.sparse.minecraft.persistent.extensions

import blue.sparse.minecraft.core.PluginProvided
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.persistent.Persistent

val PluginProvided<*>.persistent: Persistent.PersistentPlugin
	get() = server.persistent[plugin]