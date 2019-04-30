package blue.sparse.minecraft.scheduler.extensions

import blue.sparse.minecraft.core.extensions.server
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.*

val scheduler: BukkitScheduler
	get() = server.scheduler

fun BukkitScheduler.delayed(plugin: Plugin, delay: Long = 0L, async: Boolean = false, body: BukkitRunnable.() -> Unit): BukkitTask {
	val runnable = object: BukkitRunnable() {
		override fun run() = body(this)
	}
	return if(async)
		runnable.runTaskLaterAsynchronously(plugin, delay)
	else
		runnable.runTaskLater(plugin, delay)
}

fun BukkitScheduler.repeating(plugin: Plugin, repeat: Long = 1L, initial: Long = 0L, async: Boolean = false, body: BukkitRunnable.() -> Unit): BukkitTask {
	val runnable = object: BukkitRunnable() {
		override fun run() = body(this)
	}
	return if(async)
		runnable.runTaskTimerAsynchronously(plugin, initial, repeat)
	else
		runnable.runTaskTimer(plugin, initial, repeat)
}