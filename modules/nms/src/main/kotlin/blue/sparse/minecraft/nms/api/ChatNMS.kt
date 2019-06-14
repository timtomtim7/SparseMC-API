package blue.sparse.minecraft.nms.api

import blue.sparse.minecraft.nms.chat.Text
import org.bukkit.entity.Player
import org.bukkit.plugin.Plugin

interface ChatNMS: NMSHandler {
	fun send(player: Player, text: Text)
	fun send(player: Player, plugin: Plugin, key: String)
	fun sendTitle(player: Player, title: String, subtitle: String, fadein: Int, stay: Int, fadeout: Int)
}