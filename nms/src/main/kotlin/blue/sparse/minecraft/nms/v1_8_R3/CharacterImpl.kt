package blue.sparse.minecraft.nms.v1_8_R3

import blue.sparse.minecraft.core.i18n.LocalizedString
import blue.sparse.minecraft.nms.api.CharacterNMS
import org.bukkit.Location

class CharacterImpl: CharacterNMS {
	override val isSecondaryHandSupported: Boolean
		get() = TODO("not implemented")
	override val isAnimationSwimSupported: Boolean
		get() = TODO("not implemented")
	override val isAnimationElytraSupported: Boolean
		get() = TODO("not implemented")

	override fun spawn(name: String, location: Location): CharacterNMS.CharacterHandle {
		TODO("not implemented")
	}

	override fun spawn(name: LocalizedString, location: Location): CharacterNMS.CharacterHandle {
		TODO("not implemented")
	}

}