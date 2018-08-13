package blue.sparse.minecraft.nms.particle

import blue.sparse.minecraft.nms.NMSModule
import org.bukkit.Location
import kotlin.reflect.full.isSubclassOf

sealed class ParticleType<T : ParticleInfo.ParticleInfoPart>(
		val numericID: Int,
		val stringID: String,
		val createParts: (ParticleInfo) -> T
) {

	class MotionParticle(override val info: ParticleInfo) : ParticleInfo.MotionInfo
	class CountOffsetParticle(override val info: ParticleInfo) : ParticleInfo.CountOffsetInfo
	class MotionRGBParticle(override val info: ParticleInfo) : ParticleInfo.MotionInfo, ParticleInfo.ColorRGBInfo
	class RGBParticle(override val info: ParticleInfo) : ParticleInfo.ColorRGBInfo
	class HueParticle(override val info: ParticleInfo) : ParticleInfo.ColorHueInfo
	class MotionMaterialParticle(override val info: ParticleInfo) : ParticleInfo.MotionInfo, ParticleInfo.MaterialDataInfo {
		override val shiftData = true
	}

	object ExplosionNormal : ParticleType<MotionParticle>(0, "explode", ::MotionParticle)
	object ExplosionLarge : ParticleType<CountOffsetParticle>(1, "largeexplode", ::CountOffsetParticle)
	object ExplosionHuge : ParticleType<CountOffsetParticle>(2, "hugeexplosion", ::CountOffsetParticle)
	object FireworksSpark : ParticleType<MotionParticle>(3, "fireworksSpark", ::MotionParticle)
	object WaterBubble : ParticleType<MotionParticle>(4, "bubble", ::MotionParticle)
	object WaterSplash : ParticleType<MotionParticle>(5, "splash", ::MotionParticle)
	object WaterWake : ParticleType<MotionParticle>(6, "wake", ::MotionParticle)
	object WaterSuspended : ParticleType<CountOffsetParticle>(7, "suspended", ::CountOffsetParticle)
	object SuspendedDepth : ParticleType<CountOffsetParticle>(8, "depthsuspend", ::CountOffsetParticle)
	object Critical : ParticleType<MotionParticle>(9, "crit", ::MotionParticle)
	object CriticalEnchant : ParticleType<MotionParticle>(10, "magicCrit", ::MotionParticle)
	object SmokeNormal : ParticleType<MotionParticle>(11, "smoke", ::MotionParticle)
	object SmokeLarge : ParticleType<MotionParticle>(12, "largesmoke", ::MotionParticle)
	object Spell : ParticleType<MotionParticle>(13, "spell", ::MotionParticle)
	object SpellInstant : ParticleType<MotionParticle>(14, "instantSpell", ::MotionParticle)
	object SpellMob : ParticleType<MotionRGBParticle>(15, "mobSpell", ::MotionRGBParticle)
	object SpellMobAmbient : ParticleType<MotionRGBParticle>(16, "mobSpellAmbient", ::MotionRGBParticle)
	object SpellWitch : ParticleType<MotionParticle>(17, "witchMagic", ::MotionParticle)
	object DripWater : ParticleType<CountOffsetParticle>(18, "dripWater", ::CountOffsetParticle)
	object DripLava : ParticleType<CountOffsetParticle>(19, "dripLava", ::CountOffsetParticle)
	object VillagerAngry : ParticleType<CountOffsetParticle>(20, "angryVillager", ::CountOffsetParticle)
	object VillagerHappy : ParticleType<MotionParticle>(21, "happyVillager", ::MotionParticle)
	object Mycelium : ParticleType<MotionParticle>(22, "townaura", ::MotionParticle)
	object Note : ParticleType<HueParticle>(23, "note", ::HueParticle)
	object Portal : ParticleType<MotionParticle>(24, "portal", ::MotionParticle)
	object EnchantmentGlyph : ParticleType<MotionParticle>(25, "enchantmenttable", ::MotionParticle)
	object Flame : ParticleType<MotionParticle>(26, "flame", ::MotionParticle)
	object Lava : ParticleType<CountOffsetParticle>(27, "lava", ::CountOffsetParticle)
	object Footstep : ParticleType<CountOffsetParticle>(28, "footstep", ::CountOffsetParticle)
	object Cloud : ParticleType<MotionParticle>(29, "cloud", ::MotionParticle)
	object Redstone : ParticleType<RGBParticle>(30, "reddust", ::RGBParticle)
	object Snowball : ParticleType<CountOffsetParticle>(31, "snowballpoof", ::CountOffsetParticle)
	object SnowShovel : ParticleType<MotionParticle>(32, "snowshovel", ::MotionParticle)
	object Slime : ParticleType<CountOffsetParticle>(33, "slime", ::CountOffsetParticle)
	object Heart : ParticleType<CountOffsetParticle>(34, "heart", ::CountOffsetParticle)
	object Barrier : ParticleType<CountOffsetParticle>(35, "barrier", ::CountOffsetParticle)
	object ItemCrack : ParticleType<MotionMaterialParticle>(36, "iconcrack", ::MotionMaterialParticle)
	object BlockCrack : ParticleType<MotionMaterialParticle>(37, "blockcrack", ::MotionMaterialParticle)
	object BlockDust : ParticleType<MotionMaterialParticle>(38, "blockdust", ::MotionMaterialParticle)
	object WaterDrop : ParticleType<CountOffsetParticle>(39, "droplet", ::CountOffsetParticle)
	object ItemTake : ParticleType<CountOffsetParticle>(40, "take", ::CountOffsetParticle)
	object MobAppearance : ParticleType<CountOffsetParticle>(41, "mobappearance", ::CountOffsetParticle)
	object DragonBreath : ParticleType<CountOffsetParticle>(42, "dragonbreath", ::CountOffsetParticle)
	object EndRod : ParticleType<CountOffsetParticle>(43, "endRod", ::CountOffsetParticle)
	object DamageIndicator : ParticleType<CountOffsetParticle>(44, "damageIndicator", ::CountOffsetParticle)
	object SweepAttack : ParticleType<CountOffsetParticle>(45, "sweepAttack", ::CountOffsetParticle)
	object FallingDust : ParticleType<CountOffsetParticle>(46, "fallingdust", ::CountOffsetParticle)
	object Totem : ParticleType<CountOffsetParticle>(47, "totem", ::CountOffsetParticle)
	object Spit : ParticleType<CountOffsetParticle>(48, "spit", ::CountOffsetParticle)

	fun byName(name: String): ParticleType<*>? {
		return this::class.nestedClasses
				.filter { it.isSubclassOf(ParticleType::class) }
				.first { it.simpleName == name } as? ParticleType<*>
	}

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