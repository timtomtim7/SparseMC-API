package blue.sparse.minecraft.scripting.kotlin

import jdk.internal.org.objectweb.asm.*
import jdk.internal.org.objectweb.asm.commons.RemappingClassAdapter
import jdk.internal.org.objectweb.asm.commons.SimpleRemapper
import jdk.internal.org.objectweb.asm.tree.ClassNode
import org.jetbrains.kotlin.cli.common.repl.KotlinJsr223JvmScriptEngineBase
import java.io.File

class KotlinScriptClassLoader : ClassLoader(KotlinScriptClassLoader::class.java.classLoader) {
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

	fun compiledScriptToClass(script: KotlinJsr223JvmScriptEngineBase.CompiledKotlinScript, className: String? = null, directory: File? = null): Class<*> {
		var mainClass: Class<*>? = null
		val newMainClassName = className ?: generateNextClassName()
		val oldMainClassName = script.compiledData.mainClassName

		val nameMap = HashMap<String, String>()
		script.compiledData.classes.filter { it.path.endsWith(".class") }.forEach {
			val name = it.path.removeSuffix(".class")
			nameMap.put(name, name.replace(oldMainClassName, newMainClassName))
		}

		script.compiledData.classes.filter { it.path.endsWith(".class") }.forEach {
			val name = it.path.removeSuffix(".class")

			val newName = nameMap[name] ?: return@forEach
			val newBytes = renameClass(it.bytes, nameMap)

			if (directory != null)
				File(directory, "$newName.class").writeBytes(newBytes)

			val clazz = defineClass(newName.replace('/', '.'), newBytes, 0, newBytes.size)
			resolveClass(clazz)
			if (name == script.compiledData.mainClassName || name.endsWith("/${script.compiledData.mainClassName}"))
				mainClass = clazz
		}

		return mainClass ?: throw IllegalArgumentException("Compiled script did not contain its main class (?!)")
	}

//	override fun loadClass(name: String?): Class<*>
//	{
//		return super.loadClass(name)
//	}
}