package blue.sparse.minecraft.commands.parsing.impl

import blue.sparse.minecraft.commands.parsing.ParseFailException
import blue.sparse.minecraft.commands.parsing.Parser

private const val NUMBER_CHARS = "0123456789-+.e"

private inline fun <reified T : Any> numberParser(crossinline func: (String) -> T): Parser {

	return Parser.of { text ->
		var string = text.takeWhile { it in NUMBER_CHARS }
		while (string.endsWith(".")) {
			string = string.removeSuffix(".")
			text.previous()
		}

		if (string.isEmpty())
			throw ParseFailException()

		func(string)
	}

//	return object: Parser {
//		override val clazz: KClass<*>
//			get() = T::class
//
//		override fun parse(target: KClass<*>, iterator: CharIterator): Any {
//
//		}
//
//	}

//	return Parser.of({ it in NUMBER_CHARS }, {
//		if(it.endsWith("."))
//			throw ParseFailException()
//		func(it)
//	})
}

val doubleParser = numberParser(String::toDouble)
val floatParser = numberParser(String::toFloat)
val longParser = numberParser(String::toLong)
val intParser = numberParser(String::toInt)
val shortParser = numberParser(String::toShort)
val byteParser = numberParser(String::toByte)

val bigIntegerParser = numberParser(String::toBigInteger)
val bigDecimalParser = numberParser(String::toBigDecimal)