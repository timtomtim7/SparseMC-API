package blue.sparse.minecraft.nms.v1_12_R1

import blue.sparse.minecraft.SparseMCPlugin
import blue.sparse.minecraft.core.extensions.server
import blue.sparse.minecraft.nms.api.PlaceholdersNMS
import io.netty.channel.*
import net.minecraft.server.v1_12_R1.*
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import java.lang.reflect.ParameterizedType

class PlaceholderImpl : PlaceholdersNMS, Listener {

	override fun onEnable() {
		server.pluginManager.registerEvents(this, SparseMCPlugin.getPlugin())
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
		getChannel(e.player).pipeline().addBefore("packet_handler", "sparsemc-${e.player.name}", TextPacketIntercept(e.player))
	}

	@EventHandler
	fun onPlayerQuit(e: PlayerQuitEvent) {
		val channel = getChannel(e.player)
		channel.eventLoop().submit { channel.pipeline().remove("sparsemc-${e.player.name}") }
	}

	private fun getChannel(player: Player): Channel {
		return (player as CraftPlayer).handle.playerConnection.networkManager.channel
	}

	class TextPacketIntercept(val player: Player) : ChannelDuplexHandler() {

		init {
//			println("Initializing TextPacketIntercept for ${player.name}")
		}

		override fun write(context: ChannelHandlerContext, packet: Any, promise: ChannelPromise) {
			when(packet) {
				is PacketPlayOutSetSlot -> {
//					println("PacketPlayOutSetSlot")
				}
				is PacketPlayOutWindowItems -> {
//					println("PacketPlayOutWindowItems")
				}
			}

			super.write(context, packet, promise)
		}

		override fun channelRead(context: ChannelHandlerContext, packet: Any) {
			when(packet) {
				is PacketPlayInSetCreativeSlot -> {
//					println("PacketPlayInSetCreativeSlot")
				}
				is PacketPlayInWindowClick -> {
//					println("PacketPlayInWindowClick")
				}
			}

			super.channelRead(context, packet)
		}

		@Suppress("UNCHECKED_CAST")
		fun getItems(value: Any): Map<String, Collection<ItemStack>> {
			return value.javaClass.fields.mapNotNull {
				if(it.type == ItemStack::class.java)
					return@mapNotNull it.name to listOf(it.get(value) as ItemStack)

				if(it.type.isArray && it.type.componentType == ItemStack::class.java)
					return@mapNotNull it.name to (it.get(value) as Array<ItemStack>).toList()

				if(Collection::class.java.isAssignableFrom(it.type)) {
					val clazz = (it.genericType as ParameterizedType).actualTypeArguments[0] as Class<*>
					if(clazz == ItemStack::class.java) {
						return@mapNotNull it.name to it.get(value) as Collection<ItemStack>
					}
				}

				null
			}.toMap()
		}

	}

}