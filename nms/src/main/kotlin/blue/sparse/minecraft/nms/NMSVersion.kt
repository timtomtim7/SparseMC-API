package blue.sparse.minecraft.nms

import org.bukkit.Bukkit

@Suppress("EnumEntryName")
enum class NMSVersion {

	UNKNOWN,
	v1_8_R1,
	v1_8_R2,
	v1_8_R3,
	v1_9_R1,
	v1_9_R2,
	v1_10_R1,
	v1_11_R1,
	v1_12_R1;

	companion object {

		private val thisVersionString = Bukkit.getServer().javaClass.`package`.name.split(".")[3]

		val current = fromString(thisVersionString)
		val latest = values().last()

		fun fromString(name: String) = values().find { it.name == name } ?: UNKNOWN

	}

}