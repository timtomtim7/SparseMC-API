package blue.sparse.minecraft.core.i18n

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.Properties

class MinecraftLocale private constructor(
		val version: String,
		override val lang: String,
		override val region: String
) : Locale {

	private val storage = getLang(version, code)
			?: throw IllegalStateException("Unable to retrieve Minecraft lang file for $version $code")

	operator fun get(key: String, vararg inserts: String): String {
		return (storage[key] as? String ?: return "MISSING_LOCALE_KEY[$key]").format(*inserts)
	}

	companion object {
		private const val LANG_URL = "http://resources.download.minecraft.net/%s/%s"
		private const val ASSET_INDEX_URL = "https://s3.amazonaws.com/Minecraft.Download/indexes/%s.json"
		private const val LANG_ASSET = "minecraft/lang/%s.lang"

		private val assetIndexCache = HashMap<String, JsonObject?>()

		private fun getAssetIndex(version: String): JsonObject? {
			if (version in assetIndexCache)
				return assetIndexCache[version]

			val conn = URL(ASSET_INDEX_URL.format(version)).openConnection() as HttpURLConnection
			if (conn.responseCode !in 200..299)
				return null

			val result = JsonParser().parse(conn.inputStream.reader())
					.asJsonObject
					.getAsJsonObject("objects")

			assetIndexCache[version] = result
			return result
		}

		fun getLang(version: String, code: String): Properties? {
			if (code == "en_us") {
				val resource = ClassLoader.getSystemResourceAsStream("assets/minecraft/lang/en_us.lang")
				return loadProperties(resource)
			}

			val assetIndex = getAssetIndex(version) ?: return null
			val hash = assetIndex.getAsJsonObject(LANG_ASSET.format(code)).get("hash").asString

			val id = hash.substring(0, 2)
			val conn = URL(LANG_URL.format(id, hash)).openConnection() as HttpURLConnection
			if (conn.responseCode !in 200..299)
				return null

			return loadProperties(conn.inputStream)
		}

		private fun loadProperties(resource: InputStream): Properties {
			return resource.use { Properties().apply { load(it.reader()) } }
		}
	}

}