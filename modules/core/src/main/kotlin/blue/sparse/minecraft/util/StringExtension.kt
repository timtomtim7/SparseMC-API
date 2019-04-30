package blue.sparse.minecraft.util

fun String.toTitleCase(): String {
	val split = split('_', ' ')
	val corrected = split.map { it.first().toUpperCase() + it.substring(1).toLowerCase() }
	return corrected.joinToString(" ")
}