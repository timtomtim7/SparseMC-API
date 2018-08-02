package blue.sparse.minecraft.nms.v1_9_R1

import blue.sparse.minecraft.core.i18n.LocalizedString
import blue.sparse.minecraft.nms.api.CharacterNMS
import blue.sparse.minecraft.nms.character.Skin
import blue.sparse.minecraft.util.Either
import org.bukkit.Location
import org.bukkit.entity.Player

class CharacterImpl: CharacterNMS {
	override val isSecondaryHandSupported: Boolean
		get() = TODO("not implemented")
	override val isAnimationSwimSupported: Boolean
		get() = TODO("not implemented")
	override val isAnimationElytraSupported: Boolean
		get() = TODO("not implemented")

	override fun spawn(name: Either<String, LocalizedString>, location: Location, skin: Skin?): CharacterNMS.CharacterHandle {
		TODO("not implemented")
	}

	override fun getSkin(player: Player): Skin? {
		TODO("not implemented")
	}

}