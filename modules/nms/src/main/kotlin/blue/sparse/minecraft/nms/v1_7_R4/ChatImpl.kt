package blue.sparse.minecraft.nms.v1_7_R4

import blue.sparse.minecraft.nms.api.ChatNMS
import blue.sparse.minecraft.nms.chat.Text
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

class ChatImpl: ChatNMS {
	override fun send(player: Player, text: Text) {
		TODO("not implemented")
	}

	override fun send(player: Player, plugin: Plugin, key: String) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}

	override fun sendTitle(player: Player, title: String, subtitle: String, fadein: Int, stay: Int, fadeout: Int) {
		TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
	}
}