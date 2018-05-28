package blue.sparse.minecraft.module

import blue.sparse.minecraft.util.reflection
import com.sun.beans.finder.ClassFinder.findClass
import java.io.File
import java.net.*
import java.util.jar.JarFile
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmName

class ModuleClassLoader(
		val file: File,
		parent: ClassLoader
)// : URLClassLoader(arrayOf(file.toURI().toURL()), parent) {
{
	val module: Module

	init {
		load(parent as URLClassLoader, file)
//		(parent as URLClassLoader).reflection["addURL"].method(URL::class.java)!!.invoke(parent, file.toURI().toURL())

		val classes = getClassNames(file)
		val possibleModuleClasses = classes.filter { it.endsWith("Module") }

		var module: Module? = null

		for (name in possibleModuleClasses) {
			val clazz = Class.forName(name, true, parent).kotlin//parent.loadClass(name).kotlin
			if (!clazz.isSubclassOf(Module::class))
				continue

			clazz.findAnnotation<ModuleDefinition>() ?: continue

			val instance = (clazz.objectInstance
					?: throw IllegalStateException("Modules must be objects (${clazz.jvmName})")) as Module

			if (module != null)
				throw IllegalArgumentException("JAR file contained multiple module classes")

			module = instance
		}

		if (module == null)
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

	companion object {
		fun load(classLoader: URLClassLoader, jar: File) {
			addURL(classLoader, jar.toURI().toURL())
		}

		private fun addURL(loader: URLClassLoader, url: URL) {
			val addURL = URLClassLoader::class.java.getDeclaredMethod("addURL", URL::class.java)
			addURL.isAccessible = true
			addURL.invoke(loader, url)
		}
	}
}