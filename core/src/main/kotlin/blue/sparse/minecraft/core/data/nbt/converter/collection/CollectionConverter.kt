package blue.sparse.minecraft.core.data.nbt.converter.collection

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.core.data.nbt.converter.NBTConverter

object CollectionConverter : NBTConverter.Class<Collection<*>>(Collection::class) {
	override fun toNBT(value: Collection<*>): Compound {
		return Compound {
			string("type", when (value) {
				is List<*> -> "list"
				is Set<*> -> "set"
				else -> "unknown"
			})
			collection("value", value.mapNotNull {
				NBTConverter.guaranteePrimitive(it ?: return@mapNotNull null)
			})
		}
	}

	override fun fromNBT(value: Compound): Collection<*> {
		val result: MutableCollection<Any> = when(value.string("type")) {
			"list" -> ArrayList()
			"set" -> HashSet()
			else -> ArrayList()
		}

		val nbtCollection = value.collection("value")
		nbtCollection.mapTo(result) {
			if(it is Compound)
				NBTConverter.convertFromNBT(it)!!
			else it
		}

		return result
	}

}