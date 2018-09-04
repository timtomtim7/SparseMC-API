package blue.sparse.minecraft.nms.chat

import com.google.gson.*
import org.bukkit.ChatColor
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

sealed class Text {

	private var extra: MutableList<Text>? = null

	var bold: Boolean? = null
	var italic: Boolean? = null
	var underlined: Boolean? = null
	var strikethrough: Boolean? = null
	var obfuscated: Boolean? = null

	var color: ChatColor? = null
		set(value) {
			if (value?.isFormat == true)
				throw IllegalArgumentException("Color cannot be format.")
			field = value
		}

	var clickInsert: String? = null

	var click: Click? = null
	var hover: Hover? = null

	fun add(other: Text) {
		if (other == this)
			throw IllegalArgumentException("Cannot add text to itself.")
		if (extra == null)
			extra = ArrayList()
		extra!!.add(other)
	}

	inline fun add(string: String, body: Text.() -> Unit = {}) {
		add(Text(string, body))
	}

	fun removeFirst() {
		extra?.removeAt(0)
	}

	fun removeLast() {
		extra?.removeAt(extra!!.size - 1)
	}

	fun toJson(player: Player? = null, parent: Text? = null): JsonObject {
		val json = JsonObject()

		if(parent?.bold != bold && bold != null)
			json.addProperty("bold", bold)

		if(parent?.italic != italic && italic != null)
			json.addProperty("italic", italic)

		if(parent?.underlined != underlined && underlined != null)
			json.addProperty("underlined", underlined)

		if(parent?.strikethrough != strikethrough && strikethrough != null)
			json.addProperty("strikethrough", strikethrough)

		if(parent?.obfuscated != obfuscated && obfuscated != null)
			json.addProperty("obfuscated", obfuscated)

		color?.let { json.addProperty("color", it.name.toLowerCase()) }
		clickInsert?.let { json.addProperty("insertion", it) }
		click?.let { json.add("clickEvent", it.toJson()) }
		hover?.let { json.add("hoverEvent", it.toJson()) }
		abstractJson(player, parent, json)

		val extra = (extra ?: return json).map { it.toJson(player, this) }
		json.add("extra", JsonArray().apply { extra.forEach(::add) })

		return json
	}

	fun toJsonString(): String {
		return toJson().toString()
	}

	protected abstract fun abstractJson(player: Player? = null, parent: Text? = null, json: JsonObject)

	override fun equals(other: Any?): Boolean {
		if (this === other) return true
		if (other !is Text) return false

		if (extra != other.extra) return false
		if (bold != other.bold) return false
		if (italic != other.italic) return false
		if (underlined != other.underlined) return false
		if (strikethrough != other.strikethrough) return false
		if (obfuscated != other.obfuscated) return false
		if (color != other.color) return false
		if (clickInsert != other.clickInsert) return false
		if (click != other.click) return false
		if (hover != other.hover) return false

		return true
	}

	override fun hashCode(): Int {
		var result = extra?.hashCode() ?: 0
		result = 31 * result + (bold?.hashCode() ?: 0)
		result = 31 * result + (italic?.hashCode() ?: 0)
		result = 31 * result + (underlined?.hashCode() ?: 0)
		result = 31 * result + (strikethrough?.hashCode() ?: 0)
		result = 31 * result + (obfuscated?.hashCode() ?: 0)
		result = 31 * result + (color?.hashCode() ?: 0)
		result = 31 * result + (clickInsert?.hashCode() ?: 0)
		result = 31 * result + (click?.hashCode() ?: 0)
		result = 31 * result + (hover?.hashCode() ?: 0)
		return result
	}


	sealed class Click {
		abstract fun toJson(): JsonObject

		data class OpenURL(val url: String) : Click() {
			override fun toJson() = JsonObject().apply {
				addProperty("action", "open_url")
				addProperty("value", url)
			}
		}

		data class RunCommand(val command: String) : Click() {
			override fun toJson() = JsonObject().apply {
				addProperty("action", "run_command")
				addProperty("value", command)
			}
		}

		data class SuggestCommand(val command: String) : Click() {
			override fun toJson() = JsonObject().apply {
				addProperty("action", "suggest_command")
				addProperty("value", command)
			}
		}

		data class Custom(val body: (Player) -> Unit) : Click() { //Experimental
			override fun toJson() = JsonObject().apply {
				addProperty("action", "run_command")
				addProperty("value", "(tried to use incomplete feature)")
			}
		}
	}

	sealed class Hover {
		abstract fun toJson(): JsonObject

		data class ShowText(val text: Text) : Hover() {
			override fun toJson() = JsonObject().apply {
				addProperty("action", "show_text")
				add("value", text.toJson())
			}
		}

		data class ShowItem(val item: ItemStack) : Hover() {
			override fun toJson() = JsonObject().apply {
				addProperty("action", "show_item")
				TODO("convert item to json")
//				addProperty("value", TODO())
			}
		}

		data class ShowEntity(val entity: Entity) : Hover() {
			override fun toJson() = JsonObject().apply {
				addProperty("action", "show_item")
				TODO("convert entity to json")
//				addProperty("value", TODO())
			}
		}
	}

	class Raw(val text: String) : Text() {
		override fun abstractJson(player: Player?, parent: Text?, json: JsonObject) {
			json.addProperty("text", text)
		}

		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (other !is Raw) return false
			if (!super.equals(other)) return false

			if (text != other.text) return false

			return true
		}

		override fun hashCode(): Int {
			var result = super.hashCode()
			result = 31 * result + text.hashCode()
			return result
		}
	}

	class ClientLocalized(val key: String, val with: List<Text>? = null) : Text() {
		constructor(key: String, vararg with: Text) : this(key, with.takeIf { it.isNotEmpty() }?.toList())

		override fun abstractJson(player: Player?, parent: Text?, json: JsonObject) {
			json.addProperty("translate", key)
			if (with != null) {
				json.add("with", JsonArray().apply {
					with.map { it.toJson() }.forEach(::add)
				})
			}
		}

		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (other !is ClientLocalized) return false
			if (!super.equals(other)) return false

			if (key != other.key) return false
			if (with != other.with) return false

			return true
		}

		override fun hashCode(): Int {
			var result = super.hashCode()
			result = 31 * result + key.hashCode()
			result = 31 * result + (with?.hashCode() ?: 0)
			return result
		}
	}

	class Keybind(val key: String) : Text() {
		override fun abstractJson(player: Player?, parent: Text?, json: JsonObject) {
			json.addProperty("keybind", key)
		}

		override fun equals(other: Any?): Boolean {
			if (this === other) return true
			if (other !is Keybind) return false
			if (!super.equals(other)) return false

			if (key != other.key) return false

			return true
		}

		override fun hashCode(): Int {
			var result = super.hashCode()
			result = 31 * result + key.hashCode()
			return result
		}
	}

	companion object {
		inline operator fun invoke(string: String, body: Text.() -> Unit = {}): Raw {
			return Text.Raw(string).apply(body)
		}

		fun fromJson(string: String): Text {
			val json = JsonParser().parse(string) as JsonObject


			TODO()
//			if(json.has())
		}
	}
}