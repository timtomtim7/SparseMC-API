package blue.sparse.minecraft.nms.v1_12_R1

import blue.sparse.minecraft.nms.api.ChatNMS
import blue.sparse.minecraft.nms.chat.Text
import net.minecraft.server.v1_12_R1.IChatBaseComponent
import net.minecraft.server.v1_12_R1.PacketPlayOutChat
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player

class ChatImpl: ChatNMS {
	override fun send(player: Player, text: Text) {
		val component = IChatBaseComponent.ChatSerializer.a(text.toJsonString())!!
		val connection = (player as CraftPlayer).handle.playerConnection
		connection.sendPacket(PacketPlayOutChat(component))
	}
}