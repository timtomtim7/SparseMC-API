package blue.sparse.minecraft.core.data.nbt.converter

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.core.util.PluginClasses
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmName

object ReflectConverter : NBTConverter<Any> {
	override val id = "$"

	override fun canConvert(type: KClass<*>): Boolean {
		return type.primaryConstructor != null
	}

	override fun toNBT(value: Any): Compound {
		val clazz = value::class
		val constructor = clazz.primaryConstructor
				?: error("Class must have primary constructor")

		val params = constructor.parameters
		val properties = clazz.memberProperties

		val paramProperties = params.mapNotNull { param ->
			val property = properties.find {
				it.returnType == param.type
						&& it.name == param.name
			}
			if(property == null && !param.isOptional && !param.type.isMarkedNullable)
				error("Missing property for constructor")
			property
		}

		return Compound {
			string("&", clazz.jvmName)
			for (it in paramProperties) {
				val propertyValue = it.getter.call(value) ?: continue
				NBTConverter.setValue(this, it.name, propertyValue)
			}
		}
	}

	override fun fromNBT(value: Compound): Any {
		val className = value.string("&")
		val clazz = PluginClasses.forName(className).kotlin

		val constructor = clazz.primaryConstructor
				?: error("Class must have primary constructor")

		val params = constructor.parameters
		val args = params.map {
			it to NBTConverter.getValue(value, it.name!!)
		}.toMap()

		return constructor.callBy(args)
	}


}