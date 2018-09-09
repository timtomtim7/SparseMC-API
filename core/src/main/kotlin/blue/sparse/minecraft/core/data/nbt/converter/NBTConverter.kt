package blue.sparse.minecraft.core.data.nbt.converter

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.core.data.nbt.NBTValue
import blue.sparse.minecraft.core.data.nbt.converter.collection.CollectionConverter
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.jvm.jvmErasure

interface NBTConverter<T : Any> {

	val id: String
		get() = javaClass.name

	fun canConvert(type: KType) = canConvert(type.jvmErasure)
	fun canConvert(type: KClass<*>): Boolean

	fun toNBT(value: T): Compound
	fun fromNBT(value: Compound): T

	abstract class Class<T : Any>(val clazz: KClass<T>) : NBTConverter<T> {
		override fun canConvert(type: KClass<*>) = type.isSubclassOf(clazz)
	}

	@Suppress("UNCHECKED_CAST")
	companion object {

		private val registered = LinkedHashSet<NBTConverter<*>>()

		init {
			register(ReflectConverter)
			register(uuidConverter)
			register(offlinePlayerConverter)
			register(worldConverter)
			register(locationConverter)
			register(EnumConverter)
			register(CollectionConverter)
		}

		fun register(converter: NBTConverter<*>): Boolean {
			return registered.add(converter)
		}

		fun <T : Any> getConverter(clazz: KClass<T>): NBTConverter<T>? {
			return registered.lastOrNull {
				it.canConvert(clazz)
			} as NBTConverter<T>
		}

		fun <T : Any> getConverter(value: T): NBTConverter<T>? {
			return getConverter(value::class as KClass<T>) // This seems wrong
		}

		fun <T : Any> convertToNBT(value: T): Compound? {
			val converter = getConverter(value) ?: return null
			val converted = converter.toNBT(value)
			converted.string("^", converter.id)
			return converted
		}

		fun <T : Any> convertFromNBT(value: Compound): T? {
			val converterID = value.optionalString("^") ?: return null

			val converter = registered
					.find { it.id == converterID } ?: return null

			return converter.fromNBT(value) as T
		}

		fun guaranteePrimitive(value: Any): Any? {
			return NBTValue.toNBTValueOrNull(value) ?: convertToNBT(value)
		}

		fun setValue(target: Compound, key: String, value: Any): Boolean {
			target[key] = guaranteePrimitive(value) ?: return false
			return true
		}

		fun getValue(target: Compound, key: String): Any? {
			val raw = target.getOptional(key) ?: return null
			if (raw is Compound && "^" in raw)
				return convertFromNBT(raw)
			return raw
		}

		inline fun <reified T : Any> of(crossinline to: (value: T) -> Compound, crossinline from: (value: Compound) -> T): NBTConverter<T> {
			return object : Class<T>(T::class) {
				override fun toNBT(value: T) = to(value)

				override fun fromNBT(value: Compound) = from(value)
			}
		}
	}
}