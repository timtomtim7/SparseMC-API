package blue.sparse.minecraft.scheduler.extensions

import blue.sparse.minecraft.core.PluginProvided
import blue.sparse.minecraft.scheduler.BukkitCoroutine
import kotlinx.coroutines.*

fun PluginProvided<*>.suspendable(body: suspend BukkitCoroutine.() -> Unit): BukkitCoroutine {
	val context = BukkitCoroutine(plugin)
	GlobalScope.launch(Dispatchers.Unconfined) {
		try {
			body(context)
		} finally {
			context.cancel()
		}
	}
	return context
//	launch(Unconfined, start = CoroutineStart.DEFAULT) {
//		try {
//			body(context)
//		} finally {
//			context.cancel()
//		}
//	}
//	return context
}