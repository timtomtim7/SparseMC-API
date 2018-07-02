package blue.sparse.minecraft.nms.particle

import blue.sparse.minecraft.nms.NMSModule
import org.bukkit.Location

sealed class ParticleType<T : ParticleInfo.ParticleInfoPart>(
		val id: Int,
		val createParts: (ParticleInfo) -> T
) {

	class MotionParticle(override val info: ParticleInfo): ParticleInfo.MotionInfo
	class CountOffsetParticle(override val info: ParticleInfo): ParticleInfo.CountOffsetInfo
	class MotionRGBParticle(override val info: ParticleInfo): ParticleInfo.MotionInfo, ParticleInfo.ColorRGBInfo
	class RGBParticle(override val info: ParticleInfo): ParticleInfo.ColorRGBInfo
	class HueParticle(override val info: ParticleInfo): ParticleInfo.ColorHueInfo
	class MotionMaterialParticle(override val info: ParticleInfo): ParticleInfo.MotionInfo, ParticleInfo.MaterialDataInfo

	object ExplosionNormal : ParticleType<MotionParticle>(0, ::MotionParticle)
	object ExplosionLarge : ParticleType<CountOffsetParticle>(1, ::CountOffsetParticle)
	object ExplosionHuge : ParticleType<CountOffsetParticle>(2, ::CountOffsetParticle)
	object FireworksSpark : ParticleType<MotionParticle>(3, ::MotionParticle)
	object WaterBubble : ParticleType<MotionParticle>(4, ::MotionParticle)
	object WaterSplash : ParticleType<MotionParticle>(5, ::MotionParticle)
	object WaterWake : ParticleType<MotionParticle>(6, ::MotionParticle)
	object WaterSuspended : ParticleType<CountOffsetParticle>(7, ::CountOffsetParticle)
	object SuspendedDepth : ParticleType<CountOffsetParticle>(8, ::CountOffsetParticle)
	object Critical : ParticleType<MotionParticle>(9, ::MotionParticle)
	object CriticalEnchant : ParticleType<MotionParticle>(10, ::MotionParticle)
	object SmokeNormal : ParticleType<MotionParticle>(11, ::MotionParticle)
	object SmokeLarge : ParticleType<MotionParticle>(12, ::MotionParticle)
	object Spell : ParticleType<MotionParticle>(13, ::MotionParticle)
	object SpellInstant : ParticleType<MotionParticle>(14, ::MotionParticle)
	object SpellMob : ParticleType<MotionRGBParticle>(15, ::MotionRGBParticle)
	object SpellMobAmbient : ParticleType<MotionRGBParticle>(16, ::MotionRGBParticle)
	object SpellWitch : ParticleType<MotionParticle>(17, ::MotionParticle)
	object DripWater : ParticleType<CountOffsetParticle>(18, ::CountOffsetParticle)
	object DripLava : ParticleType<CountOffsetParticle>(19, ::CountOffsetParticle)
	object VillagerAngry : ParticleType<CountOffsetParticle>(20, ::CountOffsetParticle)
	object VillagerHappy : ParticleType<MotionParticle>(21, ::MotionParticle)
	object Mycelium : ParticleType<MotionParticle>(22, ::MotionParticle)
	object Note : ParticleType<HueParticle>(23, ::HueParticle)
	object Portal : ParticleType<MotionParticle>(24, ::MotionParticle)
	object EnchantmentGlyph : ParticleType<MotionParticle>(25, ::MotionParticle)
	object Flame : ParticleType<MotionParticle>(26, ::MotionParticle)
	object Lava : ParticleType<CountOffsetParticle>(27, ::CountOffsetParticle)
	object Footstep : ParticleType<CountOffsetParticle>(28, ::CountOffsetParticle)
	object Cloud : ParticleType<MotionParticle>(29, ::MotionParticle)
	object Redstone : ParticleType<RGBParticle>(30, ::RGBParticle)
	object Snowball : ParticleType<CountOffsetParticle>(31, ::CountOffsetParticle)
	object SnowShovel : ParticleType<MotionParticle>(32, ::MotionParticle)
	object Slime : ParticleType<CountOffsetParticle>(33, ::CountOffsetParticle)
	object Heart : ParticleType<CountOffsetParticle>(34, ::CountOffsetParticle)
	object Barrier : ParticleType<CountOffsetParticle>(35, ::CountOffsetParticle)
	object ItemCrack : ParticleType<MotionMaterialParticle>(36, ::MotionMaterialParticle)
	object BlockCrack : ParticleType<MotionMaterialParticle>(37, ::MotionMaterialParticle)
	object BlockDust : ParticleType<MotionMaterialParticle>(38, ::MotionMaterialParticle)
	object WaterDrop : ParticleType<CountOffsetParticle>(39, ::CountOffsetParticle)
	object ItemTake : ParticleType<CountOffsetParticle>(40, ::CountOffsetParticle)
	object MobAppearance : ParticleType<CountOffsetParticle>(41, ::CountOffsetParticle)
	object DragonBreath : ParticleType<CountOffsetParticle>(42, ::CountOffsetParticle)
	object EndRod : ParticleType<CountOffsetParticle>(43, ::CountOffsetParticle)
	object DamageIndicator : ParticleType<CountOffsetParticle>(44, ::CountOffsetParticle)
	object SweepAttack : ParticleType<CountOffsetParticle>(45, ::CountOffsetParticle)
	object FallingDust : ParticleType<CountOffsetParticle>(46, ::CountOffsetParticle)
	object Totem : ParticleType<CountOffsetParticle>(47, ::CountOffsetParticle)
	object Spit : ParticleType<CountOffsetParticle>(48, ::CountOffsetParticle)

	fun spawn(location: Location, data: ParticleData = ParticleData()): Boolean {
		val nms = NMSModule.particleNMS
		if (!nms.isAvailable(this))
			return false

		nms.spawn(this, location, data)
		return true
	}

	inline fun spawn(location: Location, body: T.() -> Unit): Boolean {
		return spawn(location, createParts(ParticleInfo()).apply(body).info.toParticleData())
	}
}

fun test() {
	val location = Location(null, 0.0, 0.0, 0.0)
	ParticleType.Redstone.spawn(location) {
		color(0f, 0.333f, 1f)
	}
}