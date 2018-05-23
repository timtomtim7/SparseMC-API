package blue.sparse.minecraft.module

import jdk.internal.org.objectweb.asm.util.ASMifier
import java.io.File
import java.net.URLClassLoader
import java.util.jar.JarFile
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmName

class ModuleClassLoader(
		val file: File,
		parent: ClassLoader
): URLClassLoader(arrayOf(file.toURI().toURL()), parent) {

	val module: Module

	init {
		val classes = getClassNames(file)
		val possibleModuleClasses = classes.filter { it.endsWith("Module") }

		var module: Module? = null

		for (name in possibleModuleClasses) {
			val clazz = findClass(name).kotlin
			if (!clazz.isSubclassOf(Module::class))
				continue

			clazz.findAnnotation<ModuleDefinition>() ?: continue

			val instance = (clazz.objectInstance
					?: throw IllegalStateException("Modules must be objects (${clazz.jvmName})")) as Module

			if(module != null)
				throw IllegalArgumentException("JAR file contained multiple module classes")

			module = instance
		}

		if(module == null)
			throw IllegalArgumentException("JAR file did not contain a module class")

		this.module = module
	}

	private fun getClassNames(file: File): List<String> {
		val names = JarFile(file).use {
			it.entries()
					.asSequence()
					.filter { !it.isDirectory }
					.map { it.name }
					.toList()
		}

		return names.asSequence()
				.filter { it.endsWith(".class") }
				.map { it.removeSuffix(".class").replace('/', '.') }
				.toList()
	}
}