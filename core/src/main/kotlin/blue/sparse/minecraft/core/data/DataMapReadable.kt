package blue.sparse.minecraft.core.data

interface DataMapReadable<K> {

	fun keys(): Set<K>

	fun byte(key: K): Byte
	fun short(key: K): Short
	fun int(key: K): Int
	fun long(key: K): Long

	fun float(key: K): Float
	fun double(key: K): Double

	fun string(key: K): String

	fun byteArray(key: K): ByteArray
	fun shortArray(key: K): ShortArray
	fun intArray(key: K): IntArray
	fun longArray(key: K): LongArray

	operator fun contains(key: K): Boolean {
		return key in keys()
	}

}