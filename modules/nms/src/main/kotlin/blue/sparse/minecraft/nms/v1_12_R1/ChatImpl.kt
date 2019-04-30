package blue.sparse.minecraft.nms.v1_12_R1

import blue.sparse.minecraft.nms.api.ChatNMS
import blue.sparse.minecraft.nms.chat.Text
import net.minecraft.server.v1_12_R1.*
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player

class ChatImpl: ChatNMS {
	override fun send(player: Player, text: Text) {
		val component = IChatBaseComponent.ChatSerializer.a(text.toJsonString())!!
		val connection = (player as CraftPlayer).handle.playerConnection
		connection.sendPacket(PacketPlayOutChat(component))
	}

	override fun sendTitle(player: Player, title: String, subtitle: String, fadein: Int, stay: Int, fadeout: Int) {
		val chatTitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"$title\"}")
		val chatSubtitle = IChatBaseComponent.ChatSerializer.a("{\"text\": \"$subtitle\"}")

		(player as CraftPlayer).handle.playerConnection.sendPacket(PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, chatTitle))
		player.handle.playerConnection.sendPacket(PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, chatSubtitle))
		player.handle.playerConnection.sendPacket(PacketPlayOutTitle(fadein, stay, fadeout))
	}
}