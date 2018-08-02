package blue.sparse.minecraft.nms.character

import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.nms.NMSModule
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import java.io.File
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

data class Skin(
		val value: String,
		val signature: String
) {

	fun save(file: File) {
		file.writeText("$value\n$signature")
	}

	companion object {

		private const val SKIN_DATA_DOWNLOAD_URL = "https://sessionserver.mojang.com/session/minecraft/profile/%s?unsigned=false"

		fun fromFile(file: File) = file.readLines().let { Skin(it[0], it[1]) }

		fun fromPlayer(player: Player) = NMSModule.characterNMS.getSkin(player)

		fun fromOfflinePlayer(player: OfflinePlayer) = fromPlayerID(player.uniqueId)

		fun fromPlayerID(playerID: UUID): Skin? {
			server.getPlayer(playerID)?.let { return fromPlayer(it) }

			val urlString = SKIN_DATA_DOWNLOAD_URL.format(playerID.toString().replace("-", ""))
			val url = URL(urlString)
			val conn = url.openConnection() as HttpURLConnection

			val error = conn.errorStream
			if (error != null)
				throw IllegalStateException(error.reader().use(InputStreamReader::readText))

			val obj = conn.inputStream.reader().use { JSONParser().parse(it) } as JSONObject
			conn.disconnect()

			val properties = obj["properties"] as JSONArray
			val prop = properties[0] as JSONObject

			return Skin(prop["value"] as String, prop["signature"] as String)
		}

	}

}