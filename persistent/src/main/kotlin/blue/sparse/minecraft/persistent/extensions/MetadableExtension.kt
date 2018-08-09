package blue.sparse.minecraft.persistent.extensions

import blue.sparse.minecraft.core.data.nbt.Compound
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.metadata.Metadatable
import org.bukkit.plugin.Plugin

fun Metadatable.metadata(plugin: Plugin): Compound {
	val existing = getMetadata("sparseMetadata").firstOrNull { it.owningPlugin == plugin }
	if(existing == null) {
		val new = Compound()
		setMetadata("sparseMetadata", FixedMetadataValue(plugin, new))
		return new
	}

	return existing.value() as Compound
}

inline fun Metadatable.metadata(
		plugin: Plugin,
		body: Compound.() -> Unit
) {
	metadata(plugin).apply(body)
}