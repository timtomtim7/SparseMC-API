package blue.sparse.minecraft.core.extensions

fun <A, B> Pair<A?, B?>.noneNullOrNull(): Pair<A, B>? {
	val first = first
	val second = second
	if (first == null || second == null)
		return null
	return first to second
}

fun <A : C, B : C, C> Pair<A, B>.iterator(): Iterator<C> {
	return toList().iterator()
}