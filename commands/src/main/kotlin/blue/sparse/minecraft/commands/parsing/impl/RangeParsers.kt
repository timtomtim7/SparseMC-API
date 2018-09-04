package blue.sparse.minecraft.commands.parsing.impl

import blue.sparse.minecraft.commands.parsing.*
import blue.sparse.minecraft.commands.parsing.CharIterator
import kotlin.reflect.KClass

class RangeParser<V: Comparable<V>, R: ClosedRange<V>>(
		override val clazz: KClass<R>,
		val rangeType: KClass<V>,
		val toRange: (V, V) -> R
): Parser {

	@Suppress("UNCHECKED_CAST")
	override fun parse(target: KClass<*>, iterator: CharIterator): Any {
		val a = Parser.parse(rangeType, iterator)
		println(a)

		if(!iterator.hasNext() || iterator.next() != ':')
			throw ParseFailException()

		val b = Parser.parse(rangeType, iterator)
		println(b)

		if(a == null/* || !rangeType.isInstance(a)*/)
			throw ParseFailException()

		if(b == null/* || !rangeType.isInstance(b)*/)
			throw ParseFailException()

		return toRange(a as V, b as V)
	}

}

val intRangeParser = RangeParser(IntRange::class, Int::class) { a, b -> a..b }