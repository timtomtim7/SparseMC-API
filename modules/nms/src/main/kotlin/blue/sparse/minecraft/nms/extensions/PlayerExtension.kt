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

fun Player.displayTitle(title: String, subtitle: String = "", fadein: Int = 5, stay: Int = 20, fadeout: Int = 5) {
	NMSModule.chatNMS.sendTitle(this, title, subtitle, fadein, stay, fadeout)
}