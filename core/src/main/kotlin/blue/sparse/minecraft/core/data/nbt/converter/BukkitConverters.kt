package blue.sparse.minecraft.core.data.nbt.converter

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.core.extensions.server
import org.bukkit.Location
import java.util.UUID

val uuidConverter = NBTConverter.of(
		{
			Compound {
				long("most", it.mostSignificantBits)
				long("least", it.leastSignificantBits)
			}
		},
		{ UUID(it.long("most"), it.long("least")) }
)

val offlinePlayerConverter = NBTConverter.of(
		{ uuidConverter.toNBT(it.uniqueId) },
		{ server.getOfflinePlayer(uuidConverter.fromNBT(it)) }
)

val worldConverter = NBTConverter.of(
		{ uuidConverter.toNBT(it.uid) },
		{ server.getWorld(uuidConverter.fromNBT(it)) }
)

val locationConverter = NBTConverter.of(
		{
			Compound {
				compound("world", worldConverter.toNBT(it.world))
				double("x", it.x)
				double("y", it.y)
				double("z", it.z)
				float("pitch", it.pitch)
				float("yaw", it.yaw)
			}
		},
		{
			Location(
					worldConverter.fromNBT(it.compound("world")),
					it.double("x"),
					it.double("y"),
					it.double("z"),
					it.float("pitch"),
					it.float("yaw")
			)
		}
)
