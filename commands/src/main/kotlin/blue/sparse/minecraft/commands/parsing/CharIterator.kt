package blue.sparse.minecraft.commands.parsing

class CharIterator(val source: String): ListIterator<Char> {

	var index = 0

	inline fun <T> fold(start: T, func: (T, Char) -> T): T {
		var value = start

		while(hasNext())
			value = func(value, next())

		return value
	}

	inline fun takeWhile(filter: (Char) -> Boolean): String {
		val result = StringBuilder()
		while(hasNext()) {
			val c = next()
			if(!filter(c)) {
				previous()
				break
			}

			result.append(c)
		}

		return result.toString()
	}

	inline fun takeUntil(filter: (Char) -> Boolean): String {
		return takeWhile { !filter(it) }
	}

	fun takeUntilWhitespace(): String {
		return takeUntil(Char::isWhitespace)
	}

	override fun hasNext(): Boolean {
		return index < source.length
	}

	override fun hasPrevious(): Boolean {
		return index > 0
	}

	override fun next(): Char {
		if(!hasNext())
			throw NoSuchElementException()
		return source[index++]
	}

	override fun nextIndex(): Int {
		return index
	}

	override fun previous(): Char {
		if(!hasPrevious())
			throw NoSuchElementException()
		return source[--index]
	}

	override fun previousIndex(): Int {
		return index - 1
	}

}