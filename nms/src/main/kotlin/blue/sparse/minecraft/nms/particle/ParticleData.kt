package blue.sparse.minecraft.nms.particle

import java.util.Arrays

class ParticleData(
		val offsetX: Float = 0f,
		val offsetY: Float = 0f,
		val offsetZ: Float = 0f,
		val data: Float = 0f,
		val count: Int = 0,
		val extra: IntArray = IntArray(0)
) {

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is ParticleData) return false

		if (offsetX != other.offsetX) return false
		if (offsetY != other.offsetY) return false
		if (offsetZ != other.offsetZ) return false
		if (data != other.data) return false
		if (count != other.count) return false
		if (!Arrays.equals(extra, other.extra)) return false

		return true
	}

	override fun hashCode(): Int {
		var result = offsetX.hashCode()
		result = 31 * result + offsetY.hashCode()
		result = 31 * result + offsetZ.hashCode()
		result = 31 * result + data.hashCode()
		result = 31 * result + count
		result = 31 * result + Arrays.hashCode(extra)
		return result
	}

	override fun toString(): String {
		return "ParticleData(offsetX=$offsetX, offsetY=$offsetY, offsetZ=$offsetZ, data=$data, count=$count, extra=${Arrays.toString(extra)})"
	}
}