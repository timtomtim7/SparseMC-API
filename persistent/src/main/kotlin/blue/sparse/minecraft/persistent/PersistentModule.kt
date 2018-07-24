package blue.sparse.minecraft.persistent

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.module.*
import java.io.File
import java.util.UUID

@ModuleDefinition
object PersistentModule : Module {

	internal val folder
		get() = File(plugin.dataFolder, "persistent").apply { mkdirs() }

	lateinit var serverPersistent: Persistent
		private set

	private val playerPersistent = HashMap<UUID, PlayerPersistent>()

	override val type = ModuleType.PERSISTENT

	override fun onEnable() {
		loadServerPersistent()
	}

	override fun onDisable() {
		saveServerPersistent()
		playerPersistent.values.forEach(PlayerPersistent::save)
	}

	private fun loadServerPersistent() {
		val file = File(folder, "server.dat")
		serverPersistent = if(file.exists())
			Persistent(Compound.read(file))
		else
			Persistent(Compound())
	}

	private fun saveServerPersistent() {
		serverPersistent.compound.write(File(folder, "server.dat"))
	}

	fun getPlayerPersistent(playerID: UUID): PlayerPersistent {
		return playerPersistent.computeIfAbsent(playerID, PlayerPersistent.Companion::load)
	}
}