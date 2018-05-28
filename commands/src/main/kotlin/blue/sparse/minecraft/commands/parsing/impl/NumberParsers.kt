package blue.sparse.minecraft.commands.parsing.impl

import blue.sparse.minecraft.commands.parsing.Parser

private const val NUMBER_CHARS = "0123456789-+.e"

private inline fun <reified T : Any> numberParser(crossinline func: (String) -> T): Parser {
	return Parser.of({ it in NUMBER_CHARS }, func)
}

val doubleParser = numberParser(String::toDouble)
val floatParser = numberParser(String::toFloat)
val longParser = numberParser(String::toLong)
val intParser = numberParser(String::toInt)
val shortParser = numberParser(String::toShort)
val byteParser = numberParser(String::toByte)

val bigIntegerParser = numberParser(String::toBigInteger)
val bigDecimalParser = numberParser(String::toBigDecimal)