package blue.sparse.minecraft.plugin

import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.PluginBase
import org.bukkit.plugin.PluginDescriptionFile
import java.io.File
import java.io.InputStream
import java.util.logging.Logger
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmName

abstract class KotlinPlugin : PluginBase() {

	private val description: PluginDescriptionFile

	//	private val _logger by lazy { PluginLogger(this) }
	private val _logger: Logger by lazy { Logger.getLogger(name) }

	private var _enabled = false
	private var _naggable = true

	private lateinit var _server: Server
	private lateinit var _dataFolder: File
//	private lateinit var _pluginLoader: KotlinPluginLoader
//	private lateinit var _classLoader: KotlinPluginClassLoader

	protected var enabled: Boolean
		get() = _enabled
		set(value) {
			if (value == _enabled) return

			_enabled = value
			if (value) onEnable()
			else onDisable()
		}

	init {
		val clazz = this::class
		val annotation = clazz.findAnnotation<PluginDescription>()
				?: throw IllegalStateException("KotlinPlugin must have the PluginDescription annotation.")

		description = PluginDescriptionFile(annotation.name, annotation.version, clazz.jvmName)
		val descClazz = description.javaClass
		val dependField = descClazz.getDeclaredField("depend")
		val softDependField = descClazz.getDeclaredField("softDepend")
		val loadBeforeField = descClazz.getDeclaredField("loadBefore")

		dependField.isAccessible = true
		softDependField.isAccessible = true
		loadBeforeField.isAccessible = true

		dependField.set(description, annotation.depend.toList())
		softDependField.set(description, annotation.softDepend.toList())
		loadBeforeField.set(description, annotation.loadBefore.toList())
	}

	override fun onEnable() {}

	override fun onLoad() {}

	override fun onDisable() {}

	override fun getDefaultWorldGenerator(p0: String?, p1: String?): ChunkGenerator? = null

	final override fun isEnabled() = _enabled

	final override fun getDataFolder() = _dataFolder.apply { mkdirs() }

	final override fun getDescription(): PluginDescriptionFile {
		return description
	}

	final override fun onCommand(p0: CommandSender?, p1: Command?, p2: String?, p3: Array<out String>?) = false

	final override fun onTabComplete(p0: CommandSender?, p1: Command?, p2: String?, p3: Array<out String>?) = null

	final override fun getResource(name: String): InputStream? {
//		return _classLoader.getResource(name)?.let {
//			val conn = it.openConnection()
//			conn.useCaches = true
//			conn.getInputStream()
//		}
		TODO("not implemented")
	}

	final override fun saveResource(name: String, replace: Boolean) {
		TODO("not implemented")
	}

	final override fun getConfig(): FileConfiguration {
		TODO("not implemented")
	}

	final override fun saveConfig() {
		TODO("not implemented")
	}

	final override fun saveDefaultConfig() {
		TODO("not implemented")
	}

	final override fun reloadConfig() {
		TODO("not implemented")
	}

	final override fun getPluginLoader() = TODO("not implemented")

	final override fun isNaggable() = _naggable

	final override fun setNaggable(naggable: Boolean) {
		_naggable = naggable
	}

	final override fun getLogger() = _logger

	final override fun getServer() = _server

//	internal fun init(dataFolder: File, pluginLoader: KotlinPluginLoader, classLoader: KotlinPluginClassLoader, server: Server) {
//		_dataFolder = dataFolder
//		_pluginLoader = pluginLoader
//		_classLoader = classLoader
//		_server = server
//	}

	internal fun setEnabled(value: Boolean) {
		enabled = value
	}

	companion object {

//		fun getProvidingPlugin(clazz: Class<*>): KotlinPlugin? {
//			return (clazz.classLoader as? KotlinPluginClassLoader ?: return null).plugin
//		}

	}
}