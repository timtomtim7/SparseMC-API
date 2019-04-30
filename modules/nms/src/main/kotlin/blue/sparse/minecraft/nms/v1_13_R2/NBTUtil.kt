package blue.sparse.minecraft.nms.v1_13_R2

import blue.sparse.minecraft.core.data.nbt.Compound
import net.minecraft.server.v1_13_R2.*

internal object NBTUtil {

	internal fun nbtBaseValue(base: NBTBase): Any = when (base) {
		is NBTTagByte -> getData(base) as Byte
		is NBTTagShort -> getData(base) as Short
		is NBTTagInt -> getData(base) as Int
		is NBTTagLong -> getData(base) as Long
		is NBTTagFloat -> getData(base) as Float
		is NBTTagDouble -> base.asDouble()
		is NBTTagByteArray -> getData(base) as ByteArray
		is NBTTagString -> getData(base) as String
		is NBTTagList -> (0 until base.size).mapNotNull { nbtBaseValue(base[it]) }
		is NBTTagCompound -> Compound(base.keys.mapNotNull m@{ Pair(it ?: return@m null, nbtBaseValue(base[it])) })
		is NBTTagIntArray -> getData(base) as IntArray
		is NBTTagLongArray -> getData(base, "d") as String
		else -> throw IllegalArgumentException("Invalid NBTBase object ${base.javaClass.name}")
	}

	private fun getData(base: NBTBase, fieldName: String = "data"): Any? {
		return base.javaClass.getDeclaredField(fieldName).run {
			isAccessible = true
			get(base)
		}
	}

	internal fun valueToNBTBase(value: Any): NBTBase {
		return when (value) {
			is Byte -> NBTTagByte(value)
			is Short -> NBTTagShort(value)
			is Int -> NBTTagInt(value)
			is Long -> NBTTagLong(value)
			is Float -> NBTTagFloat(value)
			is Double -> NBTTagDouble(value)
			is ByteArray -> NBTTagByteArray(value)
			is String -> NBTTagString(value)
			is List<*> -> NBTTagList().apply { value.filterNotNull().map(::valueToNBTBase).forEach { add(it) } }
			is Compound -> NBTTagCompound().apply { value.keys().forEach { this.set(it, valueToNBTBase(value[it])) } }
			is IntArray -> NBTTagIntArray(value)
			is LongArray -> NBTTagLongArray(value)
			else -> throw IllegalArgumentException("Cannot convert type to NBT ${value.javaClass.name}")
		}
	}

}