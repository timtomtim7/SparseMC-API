package blue.sparse.minecraft.commands.parsing.impl

import blue.sparse.minecraft.commands.parsing.*
import blue.sparse.minecraft.commands.parsing.CharIterator
import blue.sparse.minecraft.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KType

object EitherParser: Parser {
	override val clazz = Either::class

	override fun parse(target: KType, iterator: CharIterator): Any {
		val leftType = target.arguments[0].type!!
		val rightType = target.arguments[1].type!!

		val left = Parser.parse(leftType, iterator)
		if(left != null)
			return Left(left)

		val right = Parser.parse(rightType, iterator)
		if(right != null)
			return Right(right)

		if(leftType.isMarkedNullable)
			return Left(null)

		if(rightType.isMarkedNullable)
			return Right(null)

		throw ParseFailException()
	}

	override fun parse(target: KClass<*>, iterator: CharIterator): Any {
		throw ParseFailException()
	}
}