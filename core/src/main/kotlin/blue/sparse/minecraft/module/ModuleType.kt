package blue.sparse.minecraft.module

import blue.sparse.maven.MavenArtifact
import java.io.File

enum class ModuleType {
	CORE,
	NMS,
	COMPATIBILITY,
	COMMANDS,
	CONFIGURATION,
	INVENTORY,
	MATH,
	PERSISTENT,
	SCHEDULER,
	SCRIPTING;

	val artifact = MavenArtifact(
			ModuleManager.MODULE_MAVEN_REPO,
			ModuleManager.MODULE_MAVEN_GROUP,
			name.toLowerCase()
	)

	fun downloadAndLoad() {
		if(this == CORE)
			return
		val file = File(ModuleManager.modulesFolder, name.toLowerCase() + ".jar")
		file.parentFile.mkdirs()
		if(!file.exists())
			artifact.downloadLatest(file)
		ModuleManager.loadModule(this, file)
	}
}
