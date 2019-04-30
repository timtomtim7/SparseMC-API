import blue.sparse.minecraft.util.Either
import blue.sparse.minecraft.util.Left

//inline class Test(val value: Int) {
//	val something: String
//		get() = "hey $value"
//}

//@ExperimentalUnsignedTypes
fun main(args: Array<String>) {
	println("Hey!")

	val t: Either<Int, NumberFormatException> = Left(5)

//	var unsigned = 0u
//	while(true) {
//		unsigned += 1000u
//		println(unsigned.toString())
//	}
//	val test = Test(5)
//	println(test.something)
}