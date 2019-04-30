package blue.sparse.minecraft.core.extensions

inline fun <T> Boolean.doIf(body: () -> T): T? {
	return if(this) body() else null
}

inline fun <T> Boolean.doIfNot(body: () -> T): T? {
	return if(this) null else body()
}