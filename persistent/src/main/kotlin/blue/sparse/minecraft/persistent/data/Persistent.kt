package blue.sparse.minecraft.persistent.data

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.utils.getValue
import java.lang.ref.WeakReference

abstract class Persistent<T>(
		val manager: PersistentManager<T>,
		instance: T
) {

	private val ref = WeakReference(instance)

	val instance by ref

	val available: Boolean
		get() = ref.get() != null

	abstract val compound: Compound

	abstract fun save()

}