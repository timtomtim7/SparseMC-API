package blue.sparse.minecraft.nms.particle

import org.bukkit.Color
import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import kotlin.math.nextUp

class ParticleInfo {

	protected var offsetX: Float = 0f
	protected var offsetY: Float = 0f
	protected var offsetZ: Float = 0f

	protected var data: Float = 0f

	protected var count: Int = 0

	protected var extra: IntArray = IntArray(0)

	protected fun offset(x: Float, y: Float, z: Float) {
		this.offsetX = x
		this.offsetY = y
		this.offsetZ = z
	}

	fun toParticleData(): ParticleData {
		return ParticleData(offsetX, offsetY, offsetZ, data, count, extra)
	}

	interface ParticleInfoPart {
		val info: ParticleInfo
	}

	interface UnknownInfo: ParticleInfoPart {
		fun offset(x: Float, y: Float, z: Float) = info.offset(x, y, z)
		fun offset(x: Double, y: Double, z: Double) = offset(x.toFloat(), y.toFloat(), z.toFloat())
		fun offset(v: Vector) = offset(v.x, v.y, v.z)

		fun data(data: Float) {
			info.data = data
		}

		fun count(count: Int) {
			info.count = count
		}

		fun extra(vararg extra: Int) {
			info.extra = extra
		}
	}

	interface CountOffsetInfo: ParticleInfoPart {
		fun offset(x: Float, y: Float, z: Float) = info.offset(x, y, z)
		fun offset(x: Double, y: Double, z: Double) = offset(x.toFloat(), y.toFloat(), z.toFloat())
		fun offset(v: Vector) = offset(v.x, v.y, v.z)

		fun count(count: Int) {
			info.count = count
		}
	}

	interface MotionInfo: ParticleInfoPart {

		fun motion(x: Float, y: Float, z: Float) {
			info.data = 1.0f
			info.offset(x, y, z)
		}
		fun motion(x: Double, y: Double, z: Double) = motion(x.toFloat(), y.toFloat(), z.toFloat())
		fun motion(v: Vector) = motion(v.x, v.y, v.z)

	}

	interface MaterialDataInfo: ParticleInfoPart {

		val shiftData: Boolean get() = false

		//TODO: 1.13 will probably kill this

		fun material(material: Material, data: Byte = 0) {
			info.extra = if(shiftData) {
				intArrayOf(material.id + (data.toInt() shl 12))
			}else{
				intArrayOf(material.id, data.toInt())
			}
		}

		fun material(item: ItemStack) {
			material(item.type, item.durability.toByte())
		}

	}

	interface ColorRGBInfo: ParticleInfoPart {

		// Added 0f.nextUp() to red for redstone (defaults to 1 if R is 0)
		fun color(r: Float, g: Float, b: Float) {
			info.data = 1.0f
			info.offset(r + 0f.nextUp(), g, b)
		}
		fun color(r: Double, g: Double, b: Double) = color(r.toFloat(), g.toFloat(), b.toFloat())
		fun color(v: Vector) = color(v.x, v.y, v.z)
		fun color(v: Color) = color(v.red / 255f, v.green / 255f, v.blue / 255f)

	}

	interface ColorHueInfo: ParticleInfoPart {

		fun color(hue: Float) = info.offset(hue, 0f, 0f)

	}



}