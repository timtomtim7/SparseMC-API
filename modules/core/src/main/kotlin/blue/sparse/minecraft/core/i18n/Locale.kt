package blue.sparse.minecraft.core.i18n

interface Locale {
	val code: String
		get() = "${lang}_$region"

	val lang: String
	val region: String
}