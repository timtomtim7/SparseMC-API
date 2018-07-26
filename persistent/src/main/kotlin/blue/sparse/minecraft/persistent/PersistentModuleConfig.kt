package blue.sparse.minecraft.persistent

import blue.sparse.data.config.file.FileConfig
import blue.sparse.minecraft.SparseMCAPIPlugin
import java.io.File

object PersistentModuleConfig
	: FileConfig(File(SparseMCAPIPlugin.getPlugin().dataFolder, "config.cfg")) {
	//TODO: Make configurable for each individual persistent data type to come.
	val autoSaveDelay by 60 // Every 60 seconds, auto save persistent data.

}