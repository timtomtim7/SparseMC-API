package blue.sparse.minecraft.scripting.kotlin.old

import jdk.internal.org.objectweb.asm.*
import jdk.internal.org.objectweb.asm.commons.RemappingClassAdapter
import jdk.internal.org.objectweb.asm.commons.SimpleRemapper
import jdk.internal.org.objectweb.asm.tree.ClassNode
import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineBase
import java.io.File

class KotlinScriptClassLoader(parent: ClassLoader = KotlinScriptClassLoader::class.java.classLoader) : ClassLoader(parent) {
	private var classCount: Int = 0

	private fun generateNextClassName() = "Script${classCount++}"

	private fun renameClass(classBytes: ByteArray, nameMap: Map<String, String>): ByteArray {
		val reader = ClassReader(classBytes)
		val node = ClassNode(Opcodes.ASM4)
		reader.accept(node, ClassReader.EXPAND_FRAMES)

		val metadata = node.visibleAnnotations.find { it.desc == "Lkotlin/Metadata;" }
		if (metadata != null) {
			//Maybe I *shouldn't* remove the metadata
			node.visibleAnnotations.remove(metadata)

//			val values = metadata.values
//			val d2 = values[values.indexOf("d2") + 1] as List<String>
//			println(d2.joinToString("\n") { "-> $it" })
		}

		val remapper = SimpleRemapper(nameMap)
		val writer = ClassWriter(ClassWriter.COMPUTE_MAXS)
		node.accept(RemappingClassAdapter(writer, remapper))

		return writer.toByteArray()
	}

	fun compiledScriptToClass(
			script: KotlinJsr223JvmScriptEngineBase.CompiledKotlinScript,
			className: String? = null,
			directory: File? = null
	): Class<*> {
		directory?.deleteRecursively()
		directory?.mkdirs()
		var mainClass: Class<*>? = null
		val newMainClassName = className ?: generateNextClassName()
		val oldMainClassName = script.compiledData.mainClassName

		val nameMap = HashMap<String, String>()
		script.compiledData.classes.filter { it.path.endsWith(".class") }.forEach {
			val name = it.path.removeSuffix(".class")
			nameMap[name] = name.replace(oldMainClassName, newMainClassName)
		}

		script.compiledData.classes.filter { it.path.endsWith(".class") }.forEach {
			val name = it.path.removeSuffix(".class")

			val newName = nameMap[name] ?: return@forEach
			val newBytes = renameClass(it.bytes, nameMap)

			val clazz = defineClass(newName.replace('/', '.'), newBytes, 0, newBytes.size)
			resolveClass(clazz)

			if (directory != null)
				File(directory, "$newName.class").writeBytes(newBytes)
//			cacheTo?.writeBytes(newBytes)

			if (name == script.compiledData.mainClassName || name.endsWith("/${script.compiledData.mainClassName}"))
				mainClass = clazz
		}

		val main = mainClass ?: throw IllegalArgumentException("Compiled script did not contain its main class (?!)")

		if (directory != null)
			File(directory, "main").writeText(main.name)

		return main
	}

	fun loadCachedScript(folder: File): Class<*> {
		val classes = folder.listFiles().mapNotNull { file ->
			if (file.extension != "class")
				return@mapNotNull  null
			val bytes = file.readBytes()
			defineClass(file.nameWithoutExtension, bytes, 0, bytes.size)
		}

		val main = File(folder, "main").readText()
		return classes.first { it.name == main }
	}
}