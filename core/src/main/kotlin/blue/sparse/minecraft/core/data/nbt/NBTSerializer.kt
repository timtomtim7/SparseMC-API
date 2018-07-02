package blue.sparse.minecraft.core.data.nbt

import blue.sparse.minecraft.core.data.nbt.NBTValue.*
import blue.sparse.minecraft.core.data.nbt.NBTValue.Companion.toNBTValue
import java.io.*

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
				val type = value.value.firstOrNull()?.let(::toNBTValue)?.id ?: 0
				target.write(type)
				target.writeInt(value.value.size)
				value.value.forEach { write(toNBTValue(it), target) }
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

	fun read(input: InputStream): NBTValue<*> {
		return read(DataInputStream(input))
	}

	fun read(input: DataInputStream, type: Int = input.read()): NBTValue<*> {
		return when(type) {
			1 -> NBTByte(input.readByte())
			2 -> NBTShort(input.readShort())
			3 -> NBTInt(input.readInt())
			4 -> NBTLong(input.readLong())
			5 -> NBTFloat(input.readFloat())
			6 -> NBTDouble(input.readDouble())
			7 -> {
				val size = input.readInt()
				val array = ByteArray(size)
				input.read(array)
				NBTByteArray(array)
			}
			8 -> NBTString(input.readUTF())
			9 -> {
				val listType = input.read()
				val size = input.readInt()
				val result = ArrayList<Any>()
				for(i in 1..size)
					result.add(read(input, listType).value)

				NBTList(result)
			}
			10 -> {
				val result = Compound()
				while(true) {
					val id = input.read()
					if(id == 0)
						break

					val name = input.readUTF()
					result[name] = read(input, id)
				}

				NBTCompound(result)
			}
			11 -> {
				val size = input.readInt()
//				println("Reading $size int array")
				val array = IntArray(size) { input.readInt() }
				NBTIntArray(array)
			}
			12 -> {
				val size = input.readInt()
				val array = LongArray(size) { input.readLong() }
				NBTLongArray(array)
			}
			else -> throw IllegalArgumentException("Unexpected NBT ID")
		}
	}

}