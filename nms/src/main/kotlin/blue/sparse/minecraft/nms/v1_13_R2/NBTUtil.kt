package blue.sparse.minecraft.nms.v1_13_R2

import blue.sparse.minecraft.core.data.nbt.Compound
import net.minecraft.server.v1_13_R2.*

internal object NBTUtil {

	internal fun nbtBaseValue(base: NBTBase): Any = when (base) {
		is NBTTagByte -> base.g()
		is NBTTagShort -> base.f()
		is NBTTagInt -> base.e()
		is NBTTagLong -> base.d()
		is NBTTagFloat -> base.i()
		is NBTTagDouble -> base.asDouble()
		is NBTTagByteArray -> base.c()
		is NBTTagString -> base.b_()
		is NBTTagList -> (0 until base.size).mapNotNull { nbtBaseValue(base[it]) }
		is NBTTagCompound -> Compound(base.keys.mapNotNull m@{ Pair(it ?: return@m null, nbtBaseValue(base[it])) })
		is NBTTagIntArray -> base.d()
		is NBTTagLongArray -> base.d()
		else -> throw IllegalArgumentException("Invalid NBTBase object ${base.javaClass.name}")
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