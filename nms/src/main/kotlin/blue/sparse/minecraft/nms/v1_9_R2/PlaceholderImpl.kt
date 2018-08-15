package blue.sparse.minecraft.nms.v1_9_R2

import blue.sparse.minecraft.SparseMCAPIPlugin
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.nms.api.PlaceholderNMS
import blue.sparse.minecraft.nms.extensions.*
import blue.sparse.minecraft.nms.placeholders.ItemReplacer
import io.netty.channel.*
import net.minecraft.server.v1_9_R2.*
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer
import org.bukkit.craftbukkit.v1_9_R2.inventory.CraftItemStack
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.server.PluginDisableEvent
import org.bukkit.plugin.Plugin
import java.lang.reflect.ParameterizedType

class PlaceholderImpl : PlaceholderNMS, Listener {

	private val pluginItemReplacers = HashMap<Plugin, MutableSet<ItemReplacer>>()
	private val itemReplacers = HashSet<ItemReplacer>()

	override fun registerItemReplacer(plugin: Plugin, replacer: ItemReplacer): Boolean {
		val pluginSet = pluginItemReplacers.getOrPut(plugin, ::HashSet)

		if (!pluginSet.add(replacer))
			return false

		if (!itemReplacers.add(replacer)) {
			pluginSet.remove(replacer)
			return false
		}

		return true
	}

	override fun onEnable() {
		server.pluginManager.registerEvents(this, SparseMCAPIPlugin.getPlugin())
		server.onlinePlayers.forEach { onPlayerJoin(PlayerJoinEvent(it, null)) }

//		PacketPlayOutSetSlot
//		PacketPlayOutWindowItems

//		PacketPlayInSetCreativeSlot
//		PacketPlayInWindowClick
	}

	override fun onDisable() {
		server.onlinePlayers.forEach { onPlayerQuit(PlayerQuitEvent(it, null)) }
	}

	@EventHandler
	fun onPlayerJoin(e: PlayerJoinEvent) {
		val pipeline = getChannel(e.player).pipeline()
		if (pipeline["sparsemc-${e.player.name}"] == null)
			pipeline.addBefore("packet_handler", "sparsemc-${e.player.name}", ItemPacketIntercept(e.player))
	}

	@EventHandler
	fun onPlayerQuit(e: PlayerQuitEvent) {
		val channel = getChannel(e.player)
		channel.eventLoop().submit { channel.pipeline().remove("sparsemc-${e.player.name}") }
	}

	@EventHandler
	fun onPluginDisable(e: PluginDisableEvent) {
		itemReplacers.removeAll(pluginItemReplacers.remove(e.plugin) ?: return)
	}

	private fun getChannel(player: Player): Channel {
		return (player as CraftPlayer).handle.playerConnection.networkManager.channel
	}

	private inner class ItemPacketIntercept(val player: Player) : ChannelDuplexHandler() {

		init {
//			println("Initializing ItemPacketIntercept for ${player.name}")
		}

		override fun write(context: ChannelHandlerContext, packet: Any, promise: ChannelPromise) {
			try {
				if (packet is PacketPlayOutSetSlot
						|| packet is PacketPlayOutWindowItems
						|| packet is PacketPlayOutEntityEquipment
				) {
					val items = getItems(packet)
					val replaced = replace(items)
					setItems(packet, replaced)
				}
			} catch (t: Throwable) {
				t.printStackTrace()
			}

			super.write(context, packet, promise)
		}

		override fun channelRead(context: ChannelHandlerContext, packet: Any) {
			try {
				if (packet is PacketPlayInSetCreativeSlot || packet is PacketPlayInWindowClick) {
					val items = getItems(packet)
					val reverted = revert(items)
					setItems(packet, reverted)
				}
			} catch (t: Throwable) {
				t.printStackTrace()
			}

			super.channelRead(context, packet)
		}

		fun replace(fieldMap: Map<String, Collection<ItemStack?>>): Map<String, Collection<ItemStack?>> {
			val result = HashMap<String, Collection<ItemStack?>>()

			for ((name, items) in fieldMap) {
				result[name] = items.map {
					if (it == null || it.item == null || it.item == Item.getItemOf(Blocks.AIR))
						return@map it

					val bukkit: org.bukkit.inventory.ItemStack = CraftItemStack.asBukkitCopy(it)
					val hash = bukkit.hashCode()
					val original = bukkit.toNBT()

					val replaced = itemReplacers.fold(bukkit) { item, replacer ->
						replacer.replace(player, item) ?: item
					}

					if (hash != replaced.hashCode()) {
						replaced.editNBT { compound("__original", original) }
						CraftItemStack.asNMSCopy(replaced)
					} else {
						it
					}
				}
			}

			return result
		}

		fun revert(fieldMap: Map<String, Collection<ItemStack?>>): Map<String, Collection<ItemStack?>> {
			val result = HashMap<String, Collection<ItemStack?>>()

			for ((name, items) in fieldMap) {
				result[name] = items.map {
					if (it == null || it.item == null || it.item == Item.getItemOf(Blocks.AIR))
						return@map it

					val bukkit: org.bukkit.inventory.ItemStack = CraftItemStack.asBukkitCopy(it)
					val nbt = bukkit.nbt
					if ("__original" in nbt)
						CraftItemStack.asNMSCopy(nbt.compound("__original").toItemStack())
					else
						it
				}
			}

			return result
		}

		@Suppress("UNCHECKED_CAST")
		fun getItems(value: Any): Map<String, Collection<ItemStack?>> {
			return value.javaClass.declaredFields.mapNotNull {
				if (it.type == ItemStack::class.java) {
					it.isAccessible = true
					val item = it.get(value) as ItemStack? ?: return@mapNotNull null
					return@mapNotNull it.name to listOf(item)
				}

//				if(it.type.isArray && it.type.componentType == ItemStack::class.java)
//					return@mapNotNull it.name to (it.get(value) as Array<ItemStack>).toList()

				if (Collection::class.java.isAssignableFrom(it.type)) {
					val clazz = (it.genericType as ParameterizedType).actualTypeArguments[0] as Class<*>
					if (clazz == ItemStack::class.java) {
						it.isAccessible = true
						return@mapNotNull it.name to it.get(value) as Collection<ItemStack?>
					}
				}

				if (Array<out ItemStack?>::class.java.isAssignableFrom(it.type)) {
					it.isAccessible = true
					return@mapNotNull it.name to (it.get(value) as Array<out ItemStack?>).toList()
				}

				null
			}.toMap()
		}

		fun setItems(value: Any, fieldMap: Map<String, Collection<ItemStack?>>) {

			val clazz = value.javaClass

			for ((name, items) in fieldMap) {
				val field = clazz.getDeclaredField(name)

				if (field.type == ItemStack::class.java) {
					field.isAccessible = true
					field.set(value, items.first())
					continue
				}

				if (List::class.java.isAssignableFrom(field.type)) {
					field.isAccessible = true
					field.set(value, items as? List ?: items.toList())
					continue
				}

				if (Array<out ItemStack?>::class.java.isAssignableFrom(field.type)) {
					field.isAccessible = true
					field.set(value, items.toTypedArray())
					continue
				}

				throw IllegalArgumentException("Unexpected field name or type $name: ${field.type.name}")
			}
		}
	}

}