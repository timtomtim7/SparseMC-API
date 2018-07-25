package blue.sparse.minecraft.core.data.nbt

import blue.sparse.minecraft.core.data.nbt.converter.NBTConverter
import java.io.DataInputStream
import java.io.File

@Suppress("UNCHECKED_CAST")
class Compound/*: AbstractMutableMap<String, Any>*/ {

//	override val entries: MutableSet<MutableMap.MutableEntry<String, Any>>
//		get() = HashSet()

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

	fun keys(): MutableSet<String> {
		return backingMap.keys
	}

	internal fun raw(key: String): NBTValue<*> {
		return backingMap[key]!!
	}

	/*override*/ fun remove(key: String): Boolean {
		return backingMap.remove(key) != null
	}

	/*override*/ fun put(key: String, value: Any): Any? {
		val previous = getOptional(key)
		convert(key, value)

		return previous
	}

	/*override*/ operator fun get(key: String): Any {
		return backingMap[key]!!.value
	}

	operator fun set(key: String, value: Any) {
		backingMap[key] = NBTValue.toNBTValue(value)
	}

	fun getOptional(key: String): Any? {
		return backingMap[key]?.value
	}

	fun default(key: String, value: Any): Any {
		return backingMap.getOrPut(key) { NBTValue.toNBTValue(value) }.value
	}

	inline fun default(key: String, valueSupplier: () -> Any): Any {
		val result = getOptional(key)
		if(result != null)
			return result

		val value = valueSupplier()
		set(key, value)
		return value
	}

	fun byte(key: String): Byte = (backingMap[key] as NBTValue.NBTByte).value
	fun short(key: String): Short = (backingMap[key] as NBTValue.NBTShort).value
	fun int(key: String): Int = (backingMap[key] as NBTValue.NBTInt).value
	fun long(key: String): Long = (backingMap[key] as NBTValue.NBTLong).value
	fun float(key: String): Float = (backingMap[key] as NBTValue.NBTFloat).value
	fun double(key: String): Double = (backingMap[key] as NBTValue.NBTDouble).value
	fun string(key: String): String = (backingMap[key] as NBTValue.NBTString).value
	fun byteArray(key: String): ByteArray = (backingMap[key] as NBTValue.NBTByteArray).value
	fun shortArray(key: String): ShortArray = (backingMap[key] as NBTValue.NBTIntArray).value.map(Int::toShort).toShortArray()
	fun intArray(key: String): IntArray = (backingMap[key] as NBTValue.NBTIntArray).value
	fun longArray(key: String): LongArray = (backingMap[key] as NBTValue.NBTLongArray).value
	fun compound(key: String): Compound = (backingMap[key] as NBTValue.NBTCompound).value
	fun collection(key: String): Collection<Any> = (backingMap[key] as NBTValue.NBTList<*>).value
	fun <T: Any> collectionTyped(key: String): Collection<T> = (backingMap[key] as NBTValue.NBTList<T>).value
	fun convert(key: String): Any = optionalConvert(key)!!

	inline fun <reified T: Any> convertTyped(key: String): T = optionalConvertTyped(key)!!
	fun optionalByte(key: String): Byte? = getOptional(key) as? Byte
	fun optionalShort(key: String): Short? = getOptional(key) as? Short
	fun optionalInt(key: String): Int? = getOptional(key) as? Int
	fun optionalLong(key: String): Long? = getOptional(key) as? Long
	fun optionalFloat(key: String): Float? = getOptional(key) as? Float
	fun optionalDouble(key: String): Double? = getOptional(key) as? Double
	fun optionalString(key: String): String? = getOptional(key) as? String
	fun optionalByteArray(key: String): ByteArray? = getOptional(key) as? ByteArray
	fun optionalShortArray(key: String): ShortArray? = getOptional(key) as? ShortArray
	fun optionalIntArray(key: String): IntArray? = getOptional(key) as? IntArray
	fun optionalLongArray(key: String): LongArray? = getOptional(key) as? LongArray
	fun optionalCompound(key: String): Compound? = getOptional(key) as? Compound
	fun optionalCollection(key: String): Collection<Any>? = (backingMap[key] as? NBTValue.NBTList<*>)?.value
	fun <T: Any> optionalCollectionTyped(key: String): Collection<T>? = (backingMap[key] as? NBTValue.NBTList<T>)?.value
	fun optionalConvert(key: String): Any? = NBTConverter.getValue(this, key)
	inline fun <reified T: Any> optionalConvertTyped(key: String): T? = optionalConvert(key) as? T

	fun byte(key: String, value: Byte) { backingMap[key] = NBTValue.NBTByte(value) }
	fun short(key: String, value: Short) { backingMap[key] = NBTValue.NBTShort(value) }
	fun int(key: String, value: Int) { backingMap[key] = NBTValue.NBTInt(value) }
	fun long(key: String, value: Long) { backingMap[key] = NBTValue.NBTLong(value) }
	fun float(key: String, value: Float) { backingMap[key] = NBTValue.NBTFloat(value) }
	fun double(key: String, value: Double) { backingMap[key] = NBTValue.NBTDouble(value) }
	fun string(key: String, value: String) { backingMap[key] = NBTValue.NBTString(value) }
	fun byteArray(key: String, value: ByteArray) { backingMap[key] = NBTValue.NBTByteArray(value) }
	fun shortArray(key: String, value: ShortArray) { backingMap[key] = NBTValue.NBTIntArray(value.map(Short::toInt).toIntArray()) }
	fun intArray(key: String, value: IntArray) { backingMap[key] = NBTValue.NBTIntArray(value) }
	fun longArray(key: String, value: LongArray) { backingMap[key] = NBTValue.NBTLongArray(value) }
	fun compound(key: String, value: Compound) { backingMap[key] = NBTValue.NBTCompound(value) }
	inline fun compound(key: String, body: Compound.() -> Unit) { compound(key, Compound(body)) }

	fun defaultByte(key: String, value: Byte) = default(key, value) as Byte
	fun defaultShort(key: String, value: Short) = default(key, value) as Short
	fun defaultInt(key: String, value: Int) = default(key, value) as Int
	fun defaultLong(key: String, value: Long) = default(key, value) as Long
	fun defaultFloat(key: String, value: Float) = default(key, value) as Float
	fun defaultDouble(key: String, value: Double) = default(key, value) as Double
	fun defaultString(key: String, value: String) = default(key, value) as String
	fun defaultByteArray(key: String, value: ByteArray) = default(key, value) as ByteArray
	fun defaultShortArray(key: String, value: ShortArray) = default(key, value) as ShortArray
	fun defaultIntArray(key: String, value: IntArray) = default(key, value) as IntArray
	fun defaultLongArray(key: String, value: LongArray) = default(key, value) as LongArray
	fun defaultCompound(key: String, value: Compound) = default(key, value) as Compound
	inline fun defaultByte(key: String, supplier: () -> Long) = default(key, supplier) as Long
	inline fun defaultShort(key: String, supplier: () -> Short) = default(key, supplier) as Short
	inline fun defaultInt(key: String, supplier: () -> Int) = default(key, supplier) as Int
	inline fun defaultLong(key: String, supplier: () -> Long) = default(key, supplier) as Long
	inline fun defaultFloat(key: String, supplier: () -> Float) = default(key, supplier) as Float
	inline fun defaultDouble(key: String, supplier: () -> Double) = default(key, supplier) as Double
	inline fun defaultString(key: String, supplier: () -> String) = default(key, supplier) as String
	inline fun defaultByteArray(key: String, supplier: () -> ByteArray) = default(key, supplier) as ByteArray
	inline fun defaultShortArray(key: String, supplier: () -> ShortArray) = default(key, supplier) as ShortArray
	inline fun defaultIntArray(key: String, supplier: () -> IntArray) = default(key, supplier) as IntArray
	inline fun defaultLongArray(key: String, supplier: () -> LongArray) = default(key, supplier) as LongArray
//	inline fun defaultCompound(key: String, supplier: () -> Compound) = default(key, supplier) as Compound
	inline fun defaultCompound(key: String, body: Compound.() -> Unit) = default(key) { Compound(body) } as Compound

	inline fun editCompound(key: String, body: Compound.() -> Unit) {
		defaultCompound(key) {}.apply(body)
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

	fun convert(key: String, value: Any) { NBTConverter.setValue(this, key, value) }

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