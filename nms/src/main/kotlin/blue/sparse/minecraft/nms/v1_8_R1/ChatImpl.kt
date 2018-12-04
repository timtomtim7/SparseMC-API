package blue.sparse.minecraft.nms.v1_8_R1

import blue.sparse.minecraft.nms.api.ChatNMS
import blue.sparse.minecraft.nms.chat.Text
import net.minecraft.server.v1_8_R1.ChatSerializer
import net.minecraft.server.v1_8_R1.EnumTitleAction
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer
import org.bukkit.entity.Player

class ChatImpl: ChatNMS {
	override fun send(player: Player, text: Text) {
		TODO("not implemented")
	}

	override fun sendTitle(player: Player, title: String, subtitle: String, fadein: Int, stay: Int, fadeout: Int) {
		val chatTitle = ChatSerializer.a("{\"text\": \"$title}\"")
		val chatSubtitle = ChatSerializer.a("{\"text\": \"$subtitle}\"")

		(player as CraftPlayer).handle.playerConnection.sendPacket(PacketPlayOutTitle(EnumTitleAction.TITLE, chatTitle))
		player.handle.playerConnection.sendPacket(PacketPlayOutTitle(EnumTitleAction.SUBTITLE, chatSubtitle))
		player.handle.playerConnection.sendPacket(PacketPlayOutTitle(fadein, stay, fadeout))
	}
}