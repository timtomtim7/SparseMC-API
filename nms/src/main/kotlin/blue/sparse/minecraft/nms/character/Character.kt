package blue.sparse.minecraft.nms.character

import org.bukkit.Location

interface Character {
	var location: Location
	var name: String?
}