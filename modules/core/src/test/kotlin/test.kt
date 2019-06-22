import java.lang.Math.pow

//inline class Test(val value: Int) {
//	val something: String
//		get() = "hey $value"
//}

//@ExperimentalUnsignedTypes
fun main(args: Array<String>) {
//	println("Hey!")
//
//	val t: Either<Int, NumberFormatException> = Left(5)

//	var unsigned = 0u
//	while(true) {
//		unsigned += 1000u
//		println(unsigned.toString())
//	}
//	val test = Test(5)
//	println(test.something)

	test("a.b.c.d.e")
}

fun test(key: String) {
	val splits = key.split(".")
	val combinationCount = pow(2.0, splits.size.toDouble()).toInt()
	println(combinationCount)
	val combinations = (0 until combinationCount)
			.map { it
					.toString(2)
					.padStart(splits.size, '0')
					.map { c -> c == '1' }
			}
			.sortedBy { it.count { b -> b } }

	for (combo in combinations) {
		val builder = StringBuilder()
		for(i in splits.indices) {

			if(builder.isNotEmpty())
				builder.append('.')
			if(combo[i])
				builder.append('*')
			else
				builder.append(splits[i])

////			val result = getNoWildcard(builder.toString(), placeholders)
//			if(result != null)
//				return result
		}
		println(builder)
	}
}

