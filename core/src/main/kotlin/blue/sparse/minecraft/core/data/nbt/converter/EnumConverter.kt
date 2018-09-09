package blue.sparse.minecraft.core.data.nbt.converter

import blue.sparse.minecraft.core.data.nbt.Compound
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

object EnumConverter: NBTConverter<Enum<*>> {
	override fun canConvert(type: KClass<*>): Boolean {
		return type.isSubclassOf(Enum::class)
	}

	override fun toNBT(value: Enum<*>): Compound {
		return Compound {
			string("enumClass", value.javaClass.name)
			string("enumValue", value.name)
		}
	}

	@Suppress("UNCHECKED_CAST")
	override fun fromNBT(value: Compound): Enum<*> {
		val clazz = Class.forName(value.string("enumClass"))
		val valueName = value.string("enumValue")
		return clazz.enumConstants.first {
			(it as Enum<*>).name == valueName
		} as Enum<*>
	}

}