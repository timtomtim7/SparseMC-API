data class Something(
		val hey: Int,
		val optional: Float?,
		val somethingElse: Something? = null
)

fun main(args: Array<String>) {
	val result = Compound {
		convert("key", Something(
				5,
				3.14f,
				null
		))
	}

	println(result)
}