package blue.sparse.minecraft.core.extensions.event

import org.bukkit.event.Cancellable

fun Cancellable.cancel() {
	isCancelled = true
}

fun Cancellable.uncancel() {
	isCancelled = false
}