package blue.sparse.minecraft.plugin

import blue.sparse.minecraft.core.PluginProvided
import blue.sparse.minecraft.core.i18n.PluginLocale
import org.bukkit.Server
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.generator.ChunkGenerator
import org.bukkit.plugin.*
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.nio.file.StandardCopyOption
import java.util.logging.Logger
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.jvm.jvmName

abstract class SparsePlugin : PluginBase(), PluginProvided<SparsePlugin> {

	private val description: PluginDescriptionFile

	private val _logger: Logger by lazy { Logger.getLogger(name) }
	private var _enabled = false
	private var _naggable = true

	private lateinit var _server: Server
	private lateinit var _dataFolder: File
	private lateinit var _pluginLoader: SparsePluginLoader
	private lateinit var classLoader: SparsePluginClassLoader

	val locale get() = PluginLocale.default(this)

	protected var enabled: Boolean
		get() = _enabled
		set(value) {
			if (value == _enabled) return

			_enabled = value
			if (value) onEnable()
			else onDisable()
		}

	override val plugin get() = this

	init {
		val clazz = this::class
		val annotation = clazz.findAnnotation<PluginDescription>()
//				?: throw IllegalStateException("SparsePlugin must have the PluginDescription annotation.")
		if(annotation != null) {
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

			try {
				val field = descClazz.getDeclaredField("apiVersion")
				field.isAccessible = true
				field.set(description, "1.13")
			}catch(e: NoSuchFieldException) {

			}
		}else{
			description = PluginDescriptionFile(clazz.simpleName!!.removeSuffix("Plugin"), "1.0", clazz.jvmName)
		}
	}

	override fun onEnable() {}

	override fun onLoad() {}

	override fun onDisable() {}

	override fun getDefaultWorldGenerator(worldName: String, id: String?): ChunkGenerator? = null

	final override fun isEnabled() = _enabled

	final override fun getDataFolder() = _dataFolder.apply { mkdirs() }

	final override fun getDescription(): PluginDescriptionFile {
		return description
	}

	final override fun onCommand(p0: CommandSender?, p1: Command?, p2: String?, p3: Array<out String>?) = false

	final override fun onTabComplete(p0: CommandSender?, p1: Command?, p2: String?, p3: Array<out String>?) = null

	final override fun getResource(name: String): InputStream? {
		return classLoader.getResource(name)?.let {
			val conn = it.openConnection()
			conn.useCaches = true
			conn.getInputStream()
		}
	}

	final override fun saveResource(name: String, replace: Boolean) {
		val resource = getResource(name) ?: throw IllegalArgumentException("Plugin resource not found \"$name\"")

		val file = File(dataFolder, name)
		file.parentFile.mkdirs()

		if (replace || !file.exists())
			Files.copy(resource, file.toPath(), StandardCopyOption.REPLACE_EXISTING)
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

	final override fun getPluginLoader(): PluginLoader = _pluginLoader

	final override fun isNaggable() = _naggable

	final override fun setNaggable(naggable: Boolean) {
		_naggable = naggable
	}

	final override fun getLogger() = _logger

	final override fun getServer() = _server

	internal fun init(dataFolder: File, pluginLoader: SparsePluginLoader, classLoader: SparsePluginClassLoader, server: Server) {
		_dataFolder = dataFolder
		_pluginLoader = pluginLoader
		this.classLoader = classLoader
		_server = server


	}

	internal fun setEnabled(value: Boolean) {
		enabled = value
	}

	fun getDatabase(): Nothing = throw UnsupportedOperationException()

	companion object {

		fun getProvidingPlugin(clazz: Class<*>): SparsePlugin? {
			return (clazz.classLoader as? SparsePluginClassLoader ?: return null).plugin
		}

	}
}