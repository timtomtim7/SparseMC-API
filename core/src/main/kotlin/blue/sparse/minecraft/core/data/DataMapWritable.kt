package blue.sparse.minecraft.core.data

interface DataMapWritable<K> {

	fun byte(key: K, value: Byte)
	fun short(key: K, value: Short)
	fun int(key: K, value: Int)
	fun long(key: K, value: Long)

	fun float(key: K, value: Float)
	fun double(key: K, value: Double)

	fun string(key: K, value: String)

	fun byteArray(key: K, value: ByteArray)
	fun shortArray(key: K, value: ShortArray)
	fun intArray(key: K, value: IntArray)
	fun longArray(key: K, value: LongArray)

}