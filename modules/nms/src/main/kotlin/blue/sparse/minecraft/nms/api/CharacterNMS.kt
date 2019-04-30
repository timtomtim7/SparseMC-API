package blue.sparse.minecraft.nms.api

import blue.sparse.minecraft.core.i18n.LocalizedString
import blue.sparse.minecraft.nms.character.Skin
import blue.sparse.minecraft.util.Either
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

interface CharacterNMS : NMSHandler {

	val isSecondaryHandSupported: Boolean
	val isAnimationSwimSupported: Boolean
	val isAnimationElytraSupported: Boolean

	fun spawn(name: Either<String, LocalizedString>, location: Location, skin: Skin? = null): CharacterHandle

	fun getSkin(player: Player): Skin?

	fun setEntityUseCallback(body: (Player, Int, UseAction) -> Unit)

	enum class UseAction {
		INTERACT,
		ATTACK
	}

	interface CharacterHandle {

		var name: Either<String, LocalizedString>
		val id: Int

		val eyeHeight: Double

		fun setVisible(player: Player)
		fun setInvisible(player: Player)

		fun teleport(x: Double, y: Double, z: Double)
		fun teleport(x: Double, y: Double, z: Double, yaw: Float, pitch: Float)
		fun moveTo(x: Double, y: Double, z: Double)
		fun moveTo(x: Double, y: Double, z: Double, yaw: Float, pitch: Float)
		fun look(yaw: Float, pitch: Float)

		fun setItem(slot: EquipmentSlot, item: ItemStack?)

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
		fun setAnimationSwim(value: Boolean)
		fun setAnimationElytra(value: Boolean)
		fun setAnimationSneaking(value: Boolean)
		fun setAnimationSprinting(value: Boolean)
	}

}