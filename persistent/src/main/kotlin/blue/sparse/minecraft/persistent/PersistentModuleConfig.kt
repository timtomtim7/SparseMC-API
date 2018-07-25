package blue.sparse.minecraft.persistent

import blue.sparse.data.config.file.FileConfig
import java.io.File

object PersistentModuleConfig : FileConfig(File(PersistentModule.folder, "config.cfg")) {
	//TODO: Make configurable for each individual persistent data type to come.
	val autosaveDelay by 60 // Every 60 seconds, auto save persistent data.
	val debugMessage by 1 //1 means show debug, 0 means don't. Tom, make booleans work.

	internal val doDebugMessages get() = debugMessage != 0
}