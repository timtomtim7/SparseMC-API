package blue.sparse.minecraft.core.data.nbt.converter.collection

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.core.data.nbt.converter.NBTConverter

object CollectionConverter: NBTConverter.Class<Collection<*>>(Collection::class) {
	override fun toNBT(value: Collection<*>): Compound {
		return Compound {
			collection("value", value.mapNotNull {
				NBTConverter.garunteePrimitive(it ?: return@mapNotNull null)
			})
		}
	}

	override fun fromNBT(value: Compound): Collection<*> {
		TODO()
	}

}