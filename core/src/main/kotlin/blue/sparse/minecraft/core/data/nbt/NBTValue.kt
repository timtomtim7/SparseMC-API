package blue.sparse.minecraft.core.data.nbt

internal sealed class NBTValue<T: Any>(val id: Int) {
	abstract val value: T

	data class NBTByte			(override val value: Byte)							 : NBTValue<Byte>(1)
	data class NBTShort			(override val value: Short)							 : NBTValue<Short>(2)
	data class NBTInt			(override val value: Int)							 : NBTValue<Int>(3)
	data class NBTLong			(override val value: Long)							 : NBTValue<Long>(4)
	data class NBTFloat			(override val value: Float)							 : NBTValue<Float>(5)
	data class NBTDouble		(override val value: Double)						 : NBTValue<Double>(6)
	class NBTByteArray			(override val value: ByteArray)						 : NBTValue<ByteArray>(7)
	data class NBTString		(override val value: String)						 : NBTValue<String>(8)
	data class NBTList<T: Any>	(override val value: Collection<T>)					 : NBTValue<Collection<T>>(9)
	data class NBTCompound		(override val value: Compound)						 : NBTValue<Compound>(10)
	class NBTIntArray			(override val value: IntArray)						 : NBTValue<IntArray>(11)
	class NBTLongArray			(override val value: LongArray)						 : NBTValue<LongArray>(12)

	companion object {
		fun toNBTValue(value: Any): NBTValue<out Any> = when (value) {
			is NBTValue<*> -> value
			is Byte -> NBTByte(value)
			is Short -> NBTShort(value)
			is Int -> NBTInt(value)
			is Long -> NBTLong(value)
			is Float -> NBTFloat(value)
			is Double -> NBTDouble(value)
			is ByteArray -> NBTByteArray(value)
			is String -> NBTString(value)
			is Collection<*> -> {
				if(value.isEmpty())
					NBTList(emptyList())

				val nonNull = value.filterNotNull()
				if(nonNull.size != value.size)
					throw IllegalArgumentException("NBTList may not contain nulls")
				val first = nonNull.first().javaClass
				if(nonNull.any { it.javaClass != first })
					throw IllegalArgumentException("All items in NBTList must be of the same type.")

				NBTList(nonNull)
			}
			is Compound -> NBTCompound(value)
			is IntArray -> NBTIntArray(value)
			is LongArray -> NBTLongArray(value)

			else -> throw IllegalArgumentException("Unsupported value for NBT: ${value.javaClass}")
		}
	}
}