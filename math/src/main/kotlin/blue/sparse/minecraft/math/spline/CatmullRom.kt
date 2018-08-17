package blue.sparse.minecraft.math.spline

import blue.sparse.math.vector.floats.*
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

object CatmullRom {
	private fun getOrInterpolate(control: List<Vector3f>, index: Int): Vector3f {
		if(control.size <= 2)
			throw IllegalArgumentException("Cannot interpolate with less than two points")

		val existing = control.getOrNull(index)
		if(existing != null)
			return existing

		val a = if(index < 0)
			getOrInterpolate(control, index + 2)
		else getOrInterpolate(control, index - 2)

		val b = if(index < 0)
			getOrInterpolate(control, index + 1)
		else getOrInterpolate(control, index - 1)

		return lerp(a, b, 2f)
	}

	fun get(control: List<Vector3f>, time: Float): Vector3f {
		if(control.size <= 2)
			throw IllegalArgumentException("Cannot generate curve/spline without at least two points.")

		val index = time.toInt()
		val adjustedTime = time - index

		val p0 = getOrInterpolate(control, index - 1)
		val p1 = getOrInterpolate(control, index + 0)
		val p2 = getOrInterpolate(control, index + 1)
		val p3 = getOrInterpolate(control, index + 2)

		return get(p0, p1, p2, p3, adjustedTime)
	}

	fun get(p0: Vector3f, p1: Vector3f, p2: Vector3f, p3: Vector3f, t: Float): Vector3f {
		val t2 = t * t
		val t3 = t2 * t

		val f0 = -0.5f * t3 + t2 - 0.5f * t
		val f1 = 1.5f * t3 - 2.5f * t2 + 1.0f
		val f2 = -1.5f * t3 + 2.0f * t2 + 0.5f * t
		val f3 = 0.5f * t3 - 0.5f * t2

		return p0 * f0 + p1 * f1 + p2 * f2 + p3 * f3
	}
}

fun main(args: Array<String>) {
	val image = BufferedImage(512, 512, BufferedImage.TYPE_INT_ARGB)
	val gfx = image.createGraphics()
	gfx.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
	gfx.color = Color.BLACK

	val points = ArrayList<Vector3f>()
	for(i in 0..10) {
		val x = (i / 10f) * 512f
		val y = Math.random().toFloat() * 256f + 128f
		points.add(vec3f(x, y, 0f))
	}


	for(i in 0 until (points.size)* 1000) {
		val t = i / 1000f
		val v = CatmullRom.get(points, t)

		gfx.fillOval(v.x.toInt() - 3, v.y.toInt() - 3, 6, 6)
	}

	gfx.color = Color.RED
	for(v in points) {
		gfx.fillOval(v.x.toInt() - 8, v.y.toInt() - 8, 16, 16)
	}

	gfx.dispose()
	ImageIO.write(image, "PNG", File("test.png"))
}