package blue.sparse.minecraft.core.data.nbt

import blue.sparse.minecraft.core.data.DataMap
import blue.sparse.minecraft.core.data.nbt.converter.NBTConverter
import java.io.DataInputStream
import java.io.File

class Compound: DataMap<String> {

	private val backingMap = LinkedHashMap<String, NBTValue<*>>()

	constructor(map: Map<String, Any>) {
		for ((k, v) in map) {
			backingMap[k] = NBTValue.toNBTValue(v)
		}
	}

	constructor(vararg pairs: Pair<String, Any>) {
		for ((k, v) in pairs) {
			backingMap[k] = NBTValue.toNBTValue(v)
		}
	}

	constructor(pairs: Collection<Pair<String, Any>>) {
		for ((k, v) in pairs) {
			backingMap[k] = NBTValue.toNBTValue(v)
		}
	}

	override fun keys(): MutableSet<String> {
		return backingMap.keys
	}

	internal fun raw(key: String): NBTValue<*> {
		return backingMap[key]!!
	}

	fun remove(key: String): Boolean {
		return backingMap.remove(key) != null
	}

	operator fun get(key: String): Any {
		return backingMap[key]!!.value
	}

	fun getOptional(key: String): Any? {
		return backingMap[key]?.value
	}

	operator fun set(key: String, value: Any) {
		backingMap[key] = NBTValue.toNBTValue(value)
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

	fun collection(key: String): Collection<Any> {
		return (backingMap[key] as NBTValue.NBTList<*>).value
	}

	fun convert(key: String): Any {
		return optionalConvert(key)!!
	}

	inline fun <reified T: Any> convertTyped(key: String): T {
		return optionalConvertTyped(key)!!
	}


	fun optionalByte(key: String): Byte? {
		return getOptional(key) as? Byte
	}

	fun optionalShort(key: String): Short? {
		return getOptional(key) as? Short
	}

	fun optionalInt(key: String): Int? {
		return getOptional(key) as? Int
	}

	fun optionalLong(key: String): Long? {
		return getOptional(key) as? Long
	}


	fun optionalFloat(key: String): Float? {
		return getOptional(key) as? Float
	}

	fun optionalDouble(key: String): Double? {
		return getOptional(key) as? Double
	}


	fun optionalString(key: String): String? {
		return getOptional(key) as? String
	}


	fun optionalByteArray(key: String): ByteArray? {
		return getOptional(key) as? ByteArray
	}

	fun optionalShortArray(key: String): ShortArray? {
		return getOptional(key) as? ShortArray
	}

	fun optionalIntArray(key: String): IntArray? {
		return getOptional(key) as? IntArray
	}

	fun optionalLongArray(key: String): LongArray? {
		return getOptional(key) as? LongArray
	}

	fun optionalCompound(key: String): Compound? {
		return getOptional(key) as? Compound
	}

	fun optionalCollection(key: String): Collection<Any>? {
		return (backingMap[key] as? NBTValue.NBTList<*>)?.value
	}

	fun optionalConvert(key: String): Any? {
		return NBTConverter.getValue(this, key)
	}

	inline fun <reified T: Any> optionalConvertTyped(key: String): T? {
		return optionalConvert(key) as? T
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

	inline fun compound(key: String, body: Compound.() -> Unit) {
		compound(key, Compound(body))
	}

	fun collection(key: String, collection: Collection<Any>) {
		if(collection.isNotEmpty()) {
			val first = collection.first()
			if (collection.any { it.javaClass != first.javaClass })
				throw IllegalStateException("All items in NBT collection must have the same type.")
			NBTValue.toNBTValue(first)
		}

		backingMap[key] = NBTValue.NBTList(collection)
	}

	fun convert(key: String, value: Any) {
		NBTConverter.setValue(this, key, value)
	}

	fun write(file: File) {
		file.outputStream().buffered().use {
			NBTSerializer.writeNamed("", NBTValue.NBTCompound(this), it)
		}
	}

	override fun toString(): String {
		return backingMap.toString()
	}

	companion object {

		fun read(file: File): Compound {
			return file.inputStream().buffered().use {
				it.read(ByteArray(3))
				(NBTSerializer.read(DataInputStream(it), 10) as NBTValue.NBTCompound).value
			}
		}

		inline operator fun invoke(body: Compound.() -> Unit): Compound {
			return Compound().apply(body)
		}

	}
}