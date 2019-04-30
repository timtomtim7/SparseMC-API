package blue.sparse.minecraft.nms.v1_7_R4

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.util.getDeclaredFieldValue
import net.minecraft.server.v1_7_R4.*

internal object NBTUtil {

	internal fun nbtBaseValue(base: NBTBase): Any = when (base) {
		is NBTTagByte -> base.f()
		is NBTTagShort -> base.e()
		is NBTTagInt -> base.d()
		is NBTTagLong -> base.c()
		is NBTTagFloat -> base.h()
		is NBTTagDouble -> base.g()
		is NBTTagByteArray -> base.c()
		is NBTTagString -> base.a_()
		is NBTTagList -> {
			val list = base.getDeclaredFieldValue("list") as List<*>
			list.map { nbtBaseValue(it as NBTBase) }
		}//(0 until base.size()).mapNotNull { nbtBaseValue(base.getDeclaredFieldValue("list") as NBTBase) }
		is NBTTagCompound -> Compound(base.c()
				.filterIsInstance<String?>()
				.mapNotNull m@{ Pair(it ?: return@m null, nbtBaseValue(base[it])) })
		is NBTTagIntArray -> base.c()
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
			is List<*> -> NBTTagList().apply { value.filterNotNull().map(::valueToNBTBase).forEach(this::add) }
			is Compound -> NBTTagCompound().apply { value.keys().forEach { this.set(it, valueToNBTBase(value[it])) } }
			is IntArray -> NBTTagIntArray(value)
			else -> throw IllegalArgumentException("Cannot convert type to NBT ${value.javaClass.name}")
		}
	}

}