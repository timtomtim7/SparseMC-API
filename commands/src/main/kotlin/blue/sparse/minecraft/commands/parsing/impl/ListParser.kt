package blue.sparse.minecraft.commands.parsing.impl

import blue.sparse.minecraft.commands.parsing.*
import blue.sparse.minecraft.commands.parsing.CharIterator
import kotlin.reflect.KClass
import kotlin.reflect.KType

object ListParser: Parser {
	override val clazz = List::class

	override fun parse(target: KType, iterator: CharIterator): Any {

		val type = target.arguments.first().type!!

		if (iterator.next() != '[')
			throw ParseFailException()

		val result = ArrayList<Any>()

		while(true) {
			val value = Parser.parse(type, iterator) ?: break
			result.add(value)

			iterator.takeWhile { it in ", " }
		}

		if (iterator.next() != ']')
			throw ParseFailException()

		return result
	}

	override fun parse(target: KClass<*>, iterator: CharIterator): Any {
		throw ParseFailException()
	}

}