package blue.sparse.minecraft.scheduler.extensions

import blue.sparse.minecraft.scheduler.BukkitCoroutine
import kotlinx.coroutines.experimental.*
import org.bukkit.plugin.Plugin

//fun Plugin.suspendable(body: suspend BukkitCoroutine.() -> Unit): BukkitCoroutine {
//	val context = BukkitCoroutine(this)
//	launch(Unconfined, start = CoroutineStart.DEFAULT) {
//		try {
//			body(context)
//		} finally {
//			context.cancel()
//		}
//	}
//	return context
//}