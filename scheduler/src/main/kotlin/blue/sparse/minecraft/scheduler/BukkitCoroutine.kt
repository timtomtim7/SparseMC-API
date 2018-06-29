package blue.sparse.minecraft.scheduler

import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.scheduler.extensions.delayed
import org.bukkit.event.*
import org.bukkit.plugin.*
import kotlin.coroutines.experimental.Continuation
import kotlin.coroutines.experimental.suspendCoroutine

class BukkitCoroutine internal constructor(val plugin: Plugin) {

	private var state: CoroutineState = Idle
		set(value) {
			if (field == Cancelled)
				return
			if (field.isRunning)
				plugin.logger.warning("Coroutine changed state while previous state was active. ${field.javaClass.simpleName} -> ${value.javaClass.name}")
			field = value
		}

	val isRunning: Boolean
		get() = state.isRunning

	fun cancel() {
		state.cancel()
		state = Cancelled
	}

	val isAsync: Boolean
		get() = !server.isPrimaryThread

	suspend fun sync() {
		if (isAsync) yield(false)
	}

	suspend fun async() {
		if (!isAsync) yield(true)
	}

	suspend fun yield(async: Boolean = false) = delay(0, async)

	suspend fun delay(ticks: Int, async: Boolean = false) = suspendCoroutine<Unit> { cont ->
		state = Delay(cont, ticks.toLong(), async)
	}

	suspend fun delay(count: Int, timeUnit: java.util.concurrent.TimeUnit, async: Boolean = false) {
		delay((timeUnit.toMillis(count.toLong()) / 50).toInt(), async)
	}

	suspend inline fun <reified T : Event> listen(
			priority: EventPriority = EventPriority.NORMAL,
			ignoreCancelled: Boolean = false,
			noinline filter: T.() -> Boolean = { true }
	): T {
		return listen(T::class.java, priority, ignoreCancelled, filter)
	}

	suspend fun <T : Event> listen(
			eventClass: Class<T>,
			priority: EventPriority = EventPriority.NORMAL,
			ignoreCancelled: Boolean = false,
			filter: T.() -> Boolean = { true }
	): T {
		sync()

		return suspendCoroutine { cont ->
			state = Listen(cont, eventClass, priority, ignoreCancelled, filter)
		}
	}

	suspend inline fun <reified T : Event> listen(
			timeout: Int,
			priority: EventPriority = EventPriority.NORMAL,
			ignoreCancelled: Boolean = false,
			noinline filter: T.() -> Boolean = { true }
	): T? {
		return listen(timeout, T::class.java, priority, ignoreCancelled, filter)
	}

	suspend fun <T : Event> listen(
			timeout: Int,
			eventClass: Class<T>,
			priority: EventPriority = EventPriority.NORMAL,
			ignoreCancelled: Boolean = false,
			filter: T.() -> Boolean = { true }
	): T? {
		sync()

		return suspendCoroutine { cont ->
			state = TimeoutListen(cont, eventClass, priority, ignoreCancelled, filter, timeout.toLong())
		}
	}

	private interface CoroutineState {
		val isRunning: Boolean
		fun cancel()
	}

	private object Idle : CoroutineState {
		override val isRunning = false
		override fun cancel() {}
	}

	private object Cancelled : CoroutineState {
		override val isRunning = false
		override fun cancel() {}
	}

	private inner class Delay(val cont: Continuation<Unit>, ticks: Long, async: Boolean) : CoroutineState {

		val task = server.scheduler.delayed(plugin, ticks, async) { cont.resume(Unit) }

		override val isRunning: Boolean
			get() = !task.isCancelled && server.scheduler.isCurrentlyRunning(task.taskId)

		override fun cancel() = task.cancel()

	}

//	private inner class Repeating(var cont: Continuation<Unit>?, ticks: Long, async: Boolean): CoroutineState {
//
//		val task = server.scheduler.repeating(plugin, ticks, 0, async) {
//			cont?.resume(Unit)
//		}
//
//		override val isRunning: Boolean
//			get() = !task.isCancelled && server.scheduler.isCurrentlyRunning(task.taskId)
//
//		override fun cancel() = task.cancel()
//
//	}

	private inner class Listen<T : Event>(
			val cont: Continuation<T>,
			val eventClass: Class<T>,
			priority: EventPriority,
			val ignoreCancelled: Boolean,
			val filter: T.() -> Boolean
	) : CoroutineState, Listener, EventExecutor {

		private val handlerLists = getHandlerListNullable(eventClass)?.let(::listOf) ?: HandlerList.getHandlerLists()

		override var isRunning = true
			private set

		init {
			val listener = RegisteredListener(this, this, priority, plugin, ignoreCancelled)
			handlerLists.forEach { it.register(listener) }
		}

		override fun execute(listener: Listener, event: Event) {
			if (listener != this)
				return

			if (ignoreCancelled && event is Cancellable && event.isCancelled)
				return

			if (!eventClass.isInstance(event))
				return

			val castedEvent = eventClass.cast(event)
			if (!filter(castedEvent))
				return

			cancel()
			cont.resume(castedEvent)
		}

		override fun cancel() {
			if (!isRunning)
				return
			handlerLists.forEach { it.unregister(this) }
			isRunning = false
		}

	}

	private inner class TimeoutListen<T : Event>(
			val cont: Continuation<T?>,
			val eventClass: Class<T>,
			priority: EventPriority,
			val ignoreCancelled: Boolean,
			val filter: T.() -> Boolean,
			val timeout: Long
	) : CoroutineState, Listener, EventExecutor {

		private val handlerLists = getHandlerListNullable(eventClass)?.let(::listOf) ?: HandlerList.getHandlerLists()

		override var isRunning = true
			private set

		val task = server.scheduler.delayed(plugin, timeout) {
			this@TimeoutListen.cancel()
			cont.resume(null)
		}

		init {
			val listener = RegisteredListener(this, this, priority, plugin, ignoreCancelled)
			handlerLists.forEach { it.register(listener) }
		}

		override fun execute(listener: Listener, event: Event) {
			if (listener != this)
				return

			if (ignoreCancelled && event is Cancellable && event.isCancelled)
				return

			if (!eventClass.isInstance(event))
				return

			val castedEvent = eventClass.cast(event)
			if (!filter(castedEvent))
				return

			cancel()
			cont.resume(castedEvent)
		}

		override fun cancel() {
			if (!isRunning)
				return
			task.cancel()
			handlerLists.forEach { it.unregister(this) }
			isRunning = false
		}

	}

}