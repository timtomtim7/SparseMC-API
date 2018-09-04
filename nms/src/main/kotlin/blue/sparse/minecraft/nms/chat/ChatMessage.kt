package blue.sparse.minecraft.nms.chat

import org.bukkit.entity.Player

class ChatMessage internal constructor(
		val sentTo: Player,
		val timeSent: Long,
		content: Text,
		position: Int
) {

	val log: ChatLog
		get() = ChatLog[sentTo]

	internal var _position: Int = position

	var position: Int
		get() = _position
		set(value) {
			val old = _position
			_position = value
			log.move(old, value)
		}

	var content: Text = content
		set(value) {
			field = value
			log.resend()
		}

	fun remove() {
		log.remove(position)
	}

}