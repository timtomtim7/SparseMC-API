package blue.sparse.minecraft.nms.chat

import blue.sparse.minecraft.nms.extensions.sendMessage
import org.bukkit.entity.Player
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentLinkedQueue

class ChatLog internal constructor(val player: Player) : MutableIterable<ChatMessage> {
	private val resending = ConcurrentLinkedQueue<Text>()
	internal val list = ArrayList<ChatMessage>()

	override fun iterator(): MutableIterator<ChatMessage> {
		return list.iterator()
	}

	fun insert(index: Int, message: Text) {
		if (index < 0)
			throw IndexOutOfBoundsException("$index < 0")

		if (list.size >= MAX_SIZE)
			list.removeAt(0)

		list.add(index, ChatMessage(player, System.currentTimeMillis(), message, index))
		resend()
	}

	inline fun insert(index: Int, string: String, body: Text.() -> Unit = {}) {
		insert(index, Text(string, body))
	}

	fun remove(index: Int) {
		list.removeAt(index)
		resend()
	}

	fun move(from: Int, to: Int) {
		val value = list.removeAt(from)
		list.add(to, value)
		resend()
	}

	fun add(message: Text) {
		player.sendMessage(message)
	}

	inline fun add(message: String, body: Text.() -> Unit = {}) {
		add(Text(message, body))
	}

	fun clear() {
		list.clear()
		resend()
	}

	internal fun packetReceived(text: Text) {
		if(text in resending) {
			resending.remove(text)
			return
		}

		if (list.size >= MAX_SIZE)
			list.removeAt(0)
		for ((index, chatMessage) in list.withIndex())
			chatMessage._position = index
		list.add(ChatMessage(player, System.currentTimeMillis(), text, 0))
	}

	internal fun resend() {
		for (i in 0..50) {
			val message = list.getOrNull(i)
			if (message == null) {
				player.sendMessage("")
				continue
			}

			message._position = i
			resending.add(message.content)
			player.sendMessage(message.content)
		}
	}

	companion object {
		const val MAX_SIZE = 100

		private val logs = WeakHashMap<Player, ChatLog>()

		operator fun get(player: Player): ChatLog {
			return logs.getOrPut(player) { ChatLog(player) }
		}
	}
}