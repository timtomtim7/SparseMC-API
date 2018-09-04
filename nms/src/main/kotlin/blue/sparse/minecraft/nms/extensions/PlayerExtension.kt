package blue.sparse.minecraft.nms.extensions

import blue.sparse.minecraft.nms.NMSModule
import blue.sparse.minecraft.nms.chat.Text
import org.bukkit.entity.Player

fun Player.sendMessage(text: Text) {
	NMSModule.chatNMS.send(this, text)
}

inline fun Player.sendMessage(string: String, body: Text.() -> Unit) {
	sendMessage(Text(string, body))
}