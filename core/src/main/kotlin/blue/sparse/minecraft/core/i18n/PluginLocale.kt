package blue.sparse.minecraft.core.i18n

import org.bukkit.ChatColor
import org.bukkit.configuration.Configuration
import org.bukkit.configuration.file.YamlConfiguration
import org.bukkit.plugin.Plugin
import java.io.*
import java.util.Properties

class PluginLocale private constructor(
		val plugin: Plugin,
		override val lang: String,
		override val region: String
): Locale {

	private val storage: Properties

	init {
		val resourcePath = "lang/$code.lang"
		val file = File(plugin.dataFolder, resourcePath)
		val default = plugin.getResource(resourcePath)

		if (!file.exists())
			plugin.saveResource(resourcePath, false)

		val sources = ArrayList<InputStream>()
		if (default != null)
			sources.add(default)
		if (file.exists())
			sources.add(file.inputStream())

		val storage = Properties()

		if (code != DEFAULT_CODE)
			storage.putAll(default(plugin).storage)

		for (i in sources.indices)
			storage.load(sources[i].bufferedReader())

		sources.forEach(Closeable::close)

		this.storage = storage
	}

	operator fun get(key: String, placeholders: Map<String, Any>): String {
		val localized = storage[key] as? String ?: return "MISSING_LOCALE_KEY[$key]"

		println("Localizing \"$localized\"")
		println("Placeholders:")
		placeholders.forEach { k, v ->
			println("\t$k = \"$v\"")
		}

		val result = StringBuilder()

		var i = 0
		while (i < localized.length) {
			val c = localized[i]
			if (c == '{') {
				val closeIndex = localized.indexOf('}', i)
				val name = localized.substring(i + 1, closeIndex)

				if (name.isNotBlank()) {
					result.append(getPlaceholderValue(name, placeholders))
					i = closeIndex + 1
					continue
				}
			}

			result.append(c)
			i++
		}

		return result.toString()
	}

	private fun getPlaceholderValue(key: String, placeholders: Map<String, Any>): String {
		return (placeholders[key] ?: getDefaultPlaceholders(plugin)[key] ?: get(key, placeholders) ?: key).toString()
	}

	companion object {
		const val DEFAULT_LANGUAGE = "en"
		const val DEFAULT_REGION = "us"
		const val DEFAULT_CODE = "${DEFAULT_LANGUAGE}_$DEFAULT_REGION"

		private val map = HashMap<Pair<Plugin, String>, PluginLocale>()
		private val pluginDefaultPlaceholders = HashMap<Plugin, Map<String, Any>>()

		private val globalPlaceholders = ChatColor.values().map { it.name to it.toString() }.toMap()

		operator fun get(plugin: Plugin, lang: String, region: String): PluginLocale {
			val code = "${lang}_$region".toLowerCase()
			val key = Pair(plugin, code)

			return map.getOrPut(key) { PluginLocale(plugin, lang.toLowerCase(), region.toLowerCase()) }
		}

		operator fun get(plugin: Plugin, locale: String): PluginLocale {
			return get(plugin, locale.split("_")[0], locale.split("_")[1])
		}

		fun default(plugin: Plugin): PluginLocale {
			return get(plugin, DEFAULT_LANGUAGE, DEFAULT_REGION)
		}

		fun getDefaultPlaceholders(plugin: Plugin): Map<String, Any?> {
			return pluginDefaultPlaceholders[plugin] ?: globalPlaceholders.toMutableMap()
		}

		fun addDefaultPlaceholders(plugin: Plugin, placeholders: Map<String, Any>) {
			(pluginDefaultPlaceholders.getOrPut(plugin, ::HashMap) as MutableMap).putAll(placeholders)
		}
	}

}