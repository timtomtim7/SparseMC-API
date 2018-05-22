package blue.sparse.minecraft.core.data.nbt

import blue.sparse.minecraft.core.data.*

class Compound: DataMap<String> {

	private val backingMap = LinkedHashMap<String, NBTValue<*>>()

	fun keys(): MutableSet<String> {
		return backingMap.keys
	}

	fun values(): MutableCollection<NBTValue<*>> {
		return backingMap.values
	}

	override fun byte(key: String): Byte {
		return (backingMap[key] as NBTValue.NBTByte).value
	}

	override fun short(key: String): Short {
		return (backingMap[key] as NBTValue.NBTShort).value
	}

	override fun int(key: String): Int {
		return (backingMap[key] as NBTValue.NBTInt).value
	}

	override fun long(key: String): Long {
		return (backingMap[key] as NBTValue.NBTLong).value
	}

	override fun float(key: String): Float {
		return (backingMap[key] as NBTValue.NBTFloat).value
	}

	override fun double(key: String): Double {
		return (backingMap[key] as NBTValue.NBTDouble).value
	}

	override fun string(key: String): String {
		return (backingMap[key] as NBTValue.NBTString).value
	}

	override fun byteArray(key: String): ByteArray {
		return (backingMap[key] as NBTValue.NBTByteArray).value
	}

	override fun shortArray(key: String): ShortArray {
		return (backingMap[key] as NBTValue.NBTIntArray).value.map(Int::toShort).toShortArray()
	}

	override fun intArray(key: String): IntArray {
		return (backingMap[key] as NBTValue.NBTIntArray).value
	}

	override fun longArray(key: String): LongArray {
		return (backingMap[key] as NBTValue.NBTLongArray).value
	}

	fun compound(key: String): Compound {
		return (backingMap[key] as NBTValue.NBTCompound).value
	}





	override fun byte(key: String, value: Byte) {
		backingMap[key] = NBTValue.NBTByte(value)
	}

	override fun short(key: String, value: Short) {
		backingMap[key] = NBTValue.NBTShort(value)
	}

	override fun int(key: String, value: Int) {
		backingMap[key] = NBTValue.NBTInt(value)
	}

	override fun long(key: String, value: Long) {
		backingMap[key] = NBTValue.NBTLong(value)
	}

	override fun float(key: String, value: Float) {
		backingMap[key] = NBTValue.NBTFloat(value)
	}

	override fun double(key: String, value: Double) {
		backingMap[key] = NBTValue.NBTDouble(value)
	}

	override fun string(key: String, value: String) {
		backingMap[key] = NBTValue.NBTString(value)
	}

	override fun byteArray(key: String, value: ByteArray) {
		backingMap[key] = NBTValue.NBTByteArray(value)
	}

	override fun shortArray(key: String, value: ShortArray) {
		backingMap[key] = NBTValue.NBTIntArray(value.map(Short::toInt).toIntArray())
	}

	override fun intArray(key: String, value: IntArray) {
		backingMap[key] = NBTValue.NBTIntArray(value)
	}

	override fun longArray(key: String, value: LongArray) {
		backingMap[key] = NBTValue.NBTLongArray(value)
	}

	fun compound(key: String, value: Compound) {
		backingMap[key] = NBTValue.NBTCompound(value)
	}
}