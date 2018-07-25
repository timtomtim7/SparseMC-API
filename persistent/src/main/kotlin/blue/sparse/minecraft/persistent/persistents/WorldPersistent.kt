package blue.sparse.minecraft.persistent.persistents

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.persistent.Persistent
import blue.sparse.minecraft.persistent.PersistentModule
import org.bukkit.World
import java.io.File

class WorldPersistent internal constructor(
		val world: World,
		compound: Compound
) : Persistent(compound) {

	fun save() = compound.write(File(folder, "${world.uid}/data.dat"))

	companion object {
		val folder get() = File(PersistentModule.folder, "worlds").apply { mkdirs() }

		internal fun load(world: World): WorldPersistent {
			val file = File(folder, "${world.uid}/data.dat")
			val compound = if (file.exists())
				Compound.read(file)
			else Compound()

			return WorldPersistent(world, compound)
		}
	}
}