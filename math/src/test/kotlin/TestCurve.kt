import blue.sparse.math.spline.CatmullRom
import blue.sparse.math.vector.floats.Vector3f
import blue.sparse.math.vector.floats.vec3f
import java.awt.Color
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO

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