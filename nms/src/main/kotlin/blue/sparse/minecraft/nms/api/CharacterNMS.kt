package blue.sparse.minecraft.nms.api

import blue.sparse.minecraft.core.i18n.LocalizedString
import org.bukkit.Location
import org.bukkit.entity.Player

interface CharacterNMS: NMSHandler {

	val isSecondaryHandSupported: Boolean
	val isAnimationSwimSupported: Boolean
	val isAnimationElytraSupported: Boolean

	fun spawn(name: String, location: Location): CharacterHandle
	fun spawn(name: LocalizedString, location: Location): CharacterHandle

	interface CharacterHandle {

		fun setVisible(player: Player)
		fun setInvisible(player: Player)

		fun teleport(x: Double, y: Double, z: Double)
		fun teleport(x: Double, y: Double, z: Double, yaw: Float, pitch: Float)
		fun look(yaw: Float, pitch: Float)

//		fun setItemInPrimaryHand(item: ItemStack?)
//		fun setItemInSecondaryHand(item: ItemStack?)
//
//		fun setPrimaryHand(right: Boolean)
//
//		fun setArmor(armor: Array<out ItemStack?>)
//		fun setArmor(index: Int, item: ItemStack?)
//		fun setHelmet(helmet: ItemStack)
//		fun setChestplate(chestplate: ItemStack)
//		fun setLeggings(leggings: ItemStack)
//		fun setBoots(boots: ItemStack)

		fun animateSwing()
		fun animateDamage()
		fun animateSwim()
		fun animateElytra()

		fun remove()

	}

}