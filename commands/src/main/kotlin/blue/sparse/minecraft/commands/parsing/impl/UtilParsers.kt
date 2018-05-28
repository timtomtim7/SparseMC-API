package blue.sparse.minecraft.commands.parsing.impl

import blue.sparse.minecraft.commands.parsing.*
import blue.sparse.minecraft.commands.parsing.CharIterator
import blue.sparse.minecraft.commands.parsing.util.QuotedString
import blue.sparse.minecraft.commands.parsing.util.SpacedString
import java.util.UUID
import kotlin.reflect.KClass

val uuidParser = Parser.of({ it.isLetterOrDigit() || it == '-' }, UUID::fromString)
val stringParser = Parser.of({ !it.isWhitespace() }, { it })

val spacedStringParser = Parser.of({ true }, { SpacedString(it.takeIf(String::isNotBlank)!!) })

object QuotedStringParser : Parser {

	override val clazz = QuotedString::class

	override fun parse(target: KClass<*>, iterator: CharIterator): Any {

		val first = iterator.next()
		if (first !in "\"'`")
			throw ParseFailException()

		val result = StringBuilder()
		var foundEnd = false
		var escaped = false

		while (iterator.hasNext()) {
			val next = iterator.next()

			if (!escaped) {
				if (next == '\\') {
					escaped = true
					continue
				}
				if (next == first) {
					foundEnd = true
					break
				}
			}

			result.append(next)
		}

		if (!foundEnd)
			throw ParseFailException()

		return QuotedString(result.toString())
	}
}