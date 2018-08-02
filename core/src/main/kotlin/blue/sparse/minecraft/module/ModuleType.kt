package blue.sparse.minecraft.module

import blue.sparse.maven.DependencyManager
import blue.sparse.maven.MavenArtifact

enum class ModuleType {
	CORE,
	NMS,
	COMPATIBILITY,
	COMMANDS,
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


	companion object {

		fun downloadAndLoadAll() {
			val toDownload = values().filter { it != CORE }.map { it.artifact }
			val depend = DependencyManager.downloadDependencies(
					toDownload,
					ModuleManager.modulesFolder
			)

			for ((artifact, file) in depend) {
				val module = values().find { it.artifact == artifact } ?: continue
				ModuleManager.loadModule(module, file)
			}
		}

	}
//	fun downloadAndLoad() {
//		if(this == CORE)
//			return
//		val file = File(ModuleManager.modulesFolder, name.toLowerCase() + ".jar")
//		file.parentFile.mkdirs()
//		if(!file.exists())
//			artifact.downloadLatest(file)
//		ModuleManager.loadModule(this, file)
//	}
}
