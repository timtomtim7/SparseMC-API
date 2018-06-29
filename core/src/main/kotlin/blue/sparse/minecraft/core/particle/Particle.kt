package blue.sparse.minecraft.core.particle

import blue.sparse.minecraft.core.extensions.server
import org.bukkit.*
import org.bukkit.inventory.ItemStack
import org.bukkit.material.MaterialData
import org.bukkit.util.Vector
import kotlin.math.nextUp

sealed class ParticleData {
	var location: Location = Location(server.worlds.first(), 0.0, 0.0, 0.0)

	protected open fun getCount(): Int = 0
	protected open fun getOffset(): Vector = Vector(0.0, 0.0, 0.0)
	protected open fun getExtra(): Double = 0.0
	protected open fun getData(): Any? = null

	internal fun spawn(bukkit: Particle) {
		val offset = getOffset()
		location.world.spawnParticle(bukkit, location, getCount(), offset.x, offset.y, offset.z, getExtra(), getData())
	}
}

class ParticleVelocity : ParticleData() {
	var velocity: Vector = Vector(0.0, 0.0, 0.0)

	override fun getCount() = 0
	override fun getOffset() = velocity
}

class ParticleColor : ParticleData() {
	var color: Color = Color.BLACK

	override fun getCount() = 0
	override fun getOffset() = Vector(
			color.red / 255.0 + 0f.nextUp(),
			color.green / 255.0,
			color.blue / 255.0
	)
}

class ParticleBlock : ParticleData() {
	var block: MaterialData = MaterialData(Material.STONE)

	override fun getData() = block
}

class ParticleItem : ParticleData() {
	var item: ItemStack = ItemStack(Material.STONE)

	override fun getData() = item
}


sealed class KParticle<T : ParticleData>(val bukkit: Particle) {

	object Redstone : KParticle<ParticleColor>(Particle.REDSTONE)

	fun spawn(data: T) {
		data.spawn(bukkit)
	}

}

fun test() {
	KParticle.Redstone.spawn(ParticleColor().apply {
		color = Color.fromRGB(0x0055FF)
	})
}

//fun Particle.invoke(body: ParticleData.() -> Unit) {
//
//}