package blue.sparse.minecraft.commands.parsing.impl

import blue.sparse.minecraft.commands.parsing.*
import blue.sparse.minecraft.commands.parsing.CharIterator
import blue.sparse.minecraft.util.tryOrNull
import kotlin.reflect.KClass

object EnumParser : Parser {

	override val clazz = Enum::class

	override fun parse(target: KClass<*>, iterator: CharIterator): Enum<*> {
		if (!target.java.isEnum)
			throw ParseFailException()

		val valueOf = target.java.getMethod("valueOf", String::class.java)

		val name = StringBuilder()

		var result: Any? = null
		var resultIndex = iterator.index

		while (iterator.hasNext()) {
			val text = iterator.takeWhile { it.isLetterOrDigit() || it == '_' }
			if(text.isBlank())
				break

			if (iterator.hasNext() && !iterator.next().isWhitespace())
				iterator.previous()

			name.append(text.toUpperCase())

			val nameString = name.toString()
//			val found = target.java.enumConstants.find { (it as Enum<*>).name == nameString }
//			val found = findEnumValue(target.java, name.toString())
			val found = tryOrNull { valueOf.invoke(null, nameString) }
			if (found != null) {
				result = found
				resultIndex = iterator.index
			}

			name.append('_')
		}

		iterator.index = resultIndex

		return result as Enum<*>
	}

	private fun findEnumValue(enumClass: Class<*>, name: String): Any? {
		println("Tried finding enum value for \"$name\"")
		return null
//		return tryOrNull { java.lang.Enum.valueOf(enumClass as Class<FakeEnum>, name) }
	}

	private enum class FakeEnum

}