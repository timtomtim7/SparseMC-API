package blue.sparse.minecraft.nms.api

import blue.sparse.minecraft.nms.chat.Text
import org.bukkit.entity.Player

interface ChatNMS: NMSHandler {
	fun send(player: Player, text: Text)
	fun sendTitle(player: Player, title: String, subtitle: String, fadein: Int, stay: Int, fadeout: Int)
}