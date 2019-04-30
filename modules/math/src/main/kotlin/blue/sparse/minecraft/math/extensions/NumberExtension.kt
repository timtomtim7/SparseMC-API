package blue.sparse.minecraft.math.extensions

import java.text.NumberFormat

val Number.formatted: String get() = NumberFormat.getNumberInstance().format(this)