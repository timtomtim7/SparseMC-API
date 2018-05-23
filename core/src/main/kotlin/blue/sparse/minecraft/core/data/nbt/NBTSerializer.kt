package blue.sparse.minecraft.core.data.nbt

import java.io.DataOutputStream
import java.io.OutputStream
import blue.sparse.minecraft.core.data.nbt.NBTValue.*

internal object NBTSerializer {

	fun writeNamed(key: String, value: NBTValue<*>, target: OutputStream) {
		writeNamed(key, value, target as? DataOutputStream ?: DataOutputStream(target))
	}

	fun write(value: NBTValue<*>, target: OutputStream) {
		write(value, target as? DataOutputStream ?: DataOutputStream(target))
	}

	fun writeNamed(name: String, value: NBTValue<*>, target: DataOutputStream) {
		target.write(value.id)
		target.writeUTF(name)
		write(value, target)
	}

	fun write(value: NBTValue<*>, target: DataOutputStream) {
		when(value) {
			is NBTByte -> target.write(value.value.toInt() and 0xFF)
			is NBTShort -> target.writeShort(value.value.toInt() and 0xFFFF)
			is NBTInt -> target.writeInt(value.value)
			is NBTLong -> target.writeLong(value.value)
			is NBTFloat -> target.writeFloat(value.value)
			is NBTDouble -> target.writeDouble(value.value)
			is NBTByteArray -> {
				target.writeInt(value.value.size)
				target.write(value.value)
			}
			is NBTString -> target.writeUTF(value.value)
			is NBTList<*> -> {
				val type = value.value.firstOrNull()?.id ?: 0
				target.write(type)
				target.writeInt(value.value.size)
				value.value.forEach { write(it, target) }
			}
			is NBTCompound -> {
				for (key in value.value.keys()) {
					val it = value.value.raw(key)
					writeNamed(key, it, target)
				}
				target.write(0)
			}
			is NBTIntArray -> {
				target.writeInt(value.value.size)
				value.value.forEach(target::writeInt)
			}
			is NBTLongArray -> {
				target.writeInt(value.value.size)
				value.value.forEach(target::writeLong)
			}
		}
	}

}