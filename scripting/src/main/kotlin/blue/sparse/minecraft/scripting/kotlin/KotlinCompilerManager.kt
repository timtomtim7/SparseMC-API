package blue.sparse.minecraft.scripting.kotlin

import blue.sparse.minecraft.SparseMCAPIPlugin
import org.jetbrains.kotlin.cli.common.CLIConfigurationKeys
import org.jetbrains.kotlin.cli.common.messages.MessageRenderer
import org.jetbrains.kotlin.cli.common.messages.PrintingMessageCollector
import org.jetbrains.kotlin.cli.jvm.compiler.*
import org.jetbrains.kotlin.cli.jvm.config.addJvmClasspathRoots
import org.jetbrains.kotlin.codegen.GeneratedClassLoader
import org.jetbrains.kotlin.com.intellij.openapi.Disposable
import org.jetbrains.kotlin.config.*
import org.jetbrains.kotlin.script.KotlinScriptDefinition
import java.io.File
import java.nio.file.Files
import kotlin.reflect.KClass

class KotlinCompilerManager(
		val classPath: List<File> = emptyList(),
		val parentClassLoader: ClassLoader? = null,
		val nameProvider: ScriptNameProvider = ScriptNameProvider.Sequential()
) {

	private val tempFolder get() = Files.createTempDirectory("kcompiler").toFile()

	fun compileScript(
			source: String,
			template: KClass<*> = BlankScriptTemplate::class,
			name: String = nameProvider.get(source)
	): KClass<*> {
		val file = File(tempFolder, "$name.kts")
		file.writeText(source)
		val result = compileScript(file, template)
		file.delete()
		return result
	}

	fun compileStandard(
			source: String,
			name: String = nameProvider.get(source)
	): KClass<*>? {
		val file = File(tempFolder, "$name.kt")
		file.writeText(source)
		val result = compileStandard(file)
		file.delete()
		return result
	}

	fun compileScript(file: File, template: KClass<*> = BlankScriptTemplate::class): KClass<*> {
		val env = KotlinCoreEnvironment.createForProduction(
				Disposable { },
				createScriptConfig(file, template),
				EnvironmentConfigFiles.JVM_CONFIG_FILES
		)

		val clazz = KotlinToJVMBytecodeCompiler.compileScript(env, parentClassLoader ?: javaClass.classLoader)
		return (clazz ?: throw IllegalStateException("Failed to compile script")).kotlin
	}

	fun compileStandard(file: File): KClass<*>? {
		val env = KotlinCoreEnvironment.createForProduction(
				Disposable { },
				createStandardConfig(file),
				EnvironmentConfigFiles.JVM_CONFIG_FILES
		)

		val state = KotlinToJVMBytecodeCompiler.analyzeAndGenerate(env)!! //TODO: Maybe return null?
		val classLoader = GeneratedClassLoader(state.factory, parentClassLoader ?: javaClass.classLoader)

		val classFileName = state.factory.asList().firstOrNull()?.relativePath ?: return null
		val className = classFileName.removeSuffix(".class").replace('/', '.')

		return classLoader.loadClass(className).kotlin
	}

	private fun createStandardConfig(file: File): CompilerConfiguration {
		val configuration = CompilerConfiguration()
		configuration.addKotlinSourceRoot(file.absolutePath)
		configuration.addJvmClasspathRoots(currentClasspath())
		configuration.addJvmClasspathRoots(classPath)

		configuration.put(JVMConfigurationKeys.JVM_TARGET, JvmTarget.JVM_1_8)
		configuration.put(JVMConfigurationKeys.RETAIN_OUTPUT_IN_MEMORY, false)
		configuration.put(
				CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
				PrintingMessageCollector(System.err, MessageRenderer.PLAIN_RELATIVE_PATHS, true)
		)

		return configuration
	}

	private fun createScriptConfig(file: File, template: KClass<*>): CompilerConfiguration {
		val configuration = CompilerConfiguration()
		configuration.addKotlinSourceRoot(file.absolutePath)
		configuration.addJvmClasspathRoots(currentClasspath())
		configuration.addJvmClasspathRoots(classPath)

		configuration.put(JVMConfigurationKeys.JVM_TARGET, JvmTarget.JVM_1_8)
		configuration.put(JVMConfigurationKeys.RETAIN_OUTPUT_IN_MEMORY, false)
		configuration.put(JVMConfigurationKeys.DISABLE_STANDARD_SCRIPT_DEFINITION, true)
		configuration.put(
				CLIConfigurationKeys.MESSAGE_COLLECTOR_KEY,
				PrintingMessageCollector(System.err, MessageRenderer.PLAIN_RELATIVE_PATHS, true)
		)
		configuration.put(
				JVMConfigurationKeys.SCRIPT_DEFINITIONS,
				listOf(KotlinScriptDefinition(template))
		)

		return configuration
	}

	companion object {
		init {
			System.setProperty("idea.io.use.nio2", "true")
		}

		fun withMinecraftDependencies(parentClassLoader: ClassLoader? = null, nameProvider: ScriptNameProvider = ScriptNameProvider.Sequential()): KotlinCompilerManager {
			val plugins = SparseMCAPIPlugin.getPlugin().dataFolder.parentFile
					.listFiles().filter { it.extension == "jar" || it.extension == "spl" }
			val modules = SparseMCAPIPlugin.getModulesFolder()
					.listFiles().filter { it.extension == "jar" }
			val dependencies = SparseMCAPIPlugin.getDependenciesFolder()
					.listFiles().filter { it.extension == "jar" }

			return KotlinCompilerManager(plugins + modules + dependencies, parentClassLoader, nameProvider)
		}
	}

}