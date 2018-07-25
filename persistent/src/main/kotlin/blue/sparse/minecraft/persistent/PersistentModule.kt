package blue.sparse.minecraft.persistent

import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.module.*
import blue.sparse.minecraft.persistent.persistents.*
import org.bukkit.Chunk
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.plugin.Plugin
import java.io.File
import java.util.UUID

@ModuleDefinition
object PersistentModule : AbstractModule(ModuleType.PERSISTENT) {

	internal val folder
		get() = File(plugin.dataFolder, "persistent").apply { mkdirs() }

	private val playerPersistent = HashMap<UUID, PlayerPersistent>()
	private val pluginPersistent = HashMap<Plugin, PluginPersistent>()
	private val worldPersistent = HashMap<World, WorldPersistent>()
	private val chunkPersistent = HashMap<Chunk, ChunkPersistent>()
	private val blockPersistent = HashMap<Block, BlockPersistent>()

	override fun onEnable() = startAutoSaving()
	override fun onDisable() = saveAll()

	private fun startAutoSaving() {
		server.scheduler.scheduleSyncRepeatingTask(plugin, {
			saveAll()
			if (PersistentModuleConfig.doDebugMessages)
				println("[Persistent] Persistent data has been autosaved. [Next Autosave: ${PersistentModuleConfig.autosaveDelay}]")
		}, 0, (PersistentModuleConfig.autosaveDelay * 20).toLong())
	}

	private fun saveAll() {
		saveAllPlayerPersistent()
		saveAllPluginPersistent()
		saveAllWorldPersistent()
		saveAllChunkPersistent()
		saveAllBlockPersistent()
	}
	private fun saveAllPlayerPersistent() = playerPersistent.values.forEach(PlayerPersistent::save)
	private fun saveAllPluginPersistent() = pluginPersistent.values.forEach(PluginPersistent::save)
	private fun saveAllWorldPersistent() = worldPersistent.values.forEach(WorldPersistent::save)
	private fun saveAllChunkPersistent() = chunkPersistent.values.forEach(ChunkPersistent::save)
	private fun saveAllBlockPersistent() = blockPersistent.values.forEach(BlockPersistent::save)

	fun getPlayerPersistent(uuid: UUID) = playerPersistent.computeIfAbsent(uuid, PlayerPersistent.Companion::load)
	fun getPluginPersistent(plugin: Plugin) = pluginPersistent.computeIfAbsent(plugin, PluginPersistent.Companion::load)
	fun getWorldPersistent(world: World) = 	worldPersistent.computeIfAbsent(world, WorldPersistent.Companion::load)
	fun getChunkPersistent(chunk: Chunk) = 	chunkPersistent.computeIfAbsent(chunk, ChunkPersistent.Companion::load)
	fun getBlockPersistent(block: Block) = 	blockPersistent.computeIfAbsent(block, BlockPersistent.Companion::load)
}