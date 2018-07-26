package blue.sparse.minecraft.persistent.data

import blue.sparse.minecraft.persistent.PersistentPlugin
import java.io.File

abstract class PersistentManager<T>(val plugin: PersistentPlugin) {

	open val folder: File
		get() = plugin.folder

	abstract operator fun get(value: T): Persistent<T>

	abstract fun saveAll()

}