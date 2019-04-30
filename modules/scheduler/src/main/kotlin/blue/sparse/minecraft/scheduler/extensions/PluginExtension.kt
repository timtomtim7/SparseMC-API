package blue.sparse.minecraft.scheduler.extensions

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