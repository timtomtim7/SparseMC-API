package blue.sparse.minecraft.commands.parsing.util

class SpacedString(private val value: String) : CharSequence by value {
	override fun toString() = value

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (value === other) return true
		if (value == other) return true

		return false
	}

	override fun hashCode(): Int {
		return value.hashCode()
	}
}

class QuotedString(private val value: String) : CharSequence by value {
	override fun toString() = value

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (value === other) return true
		if (value == other) return true

		return false
	}

	override fun hashCode(): Int {
		return value.hashCode()
	}
}