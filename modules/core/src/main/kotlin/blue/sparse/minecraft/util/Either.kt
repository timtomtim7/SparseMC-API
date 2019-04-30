package blue.sparse.minecraft.util

sealed class Either<out L, out R> {
	val left: L
		get() = (this as Left<L>).value

	val right: R
		get() = (this as Right<R>).value

//	override fun toString(): String {
//		return fold(Any?::toString)
//	}
}

data class Left<out T>(val value: T) : Either<T, Nothing>() {
	override fun toString() = value.toString()
}

data class Right<out T>(val value: T) : Either<Nothing, T>() {
	override fun toString() = value.toString()
}

inline fun <L, R, T> Either<L, R>.fold(left: (L) -> T, right: (R) -> T): T {
	return when (this) {
		is Left -> left(value)
		is Right -> right(value)
	}
}

inline fun <C, L : C, R : C, T> Either<L, R>.fold(func: (C) -> T): T {
	return fold(func, func)
}