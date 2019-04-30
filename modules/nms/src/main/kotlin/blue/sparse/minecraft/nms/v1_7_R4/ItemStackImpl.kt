package blue.sparse.minecraft.nms.v1_7_R4

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.nms.api.ItemStackNMS
import net.minecraft.server.v1_7_R4.NBTTagCompound
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

class ItemStackImpl : ItemStackNMS {
	private val nbtLorePrefix = "\u1710"

	private val craftMetaItemClass by lazy { Class.forName("org.bukkit.craftbukkit.v1_7_R4.inventory.CraftMetaItem") }

	override fun getNBT(item: ItemStack): Compound {
		val method = craftMetaItemClass.getDeclaredMethod("applyToItem", NBTTagCompound::class.java)
		method.isAccessible = true

		val nmsTag = NBTTagCompound()
		method(item.itemMeta, nmsTag)

		return NBTUtil.nbtBaseValue(nmsTag) as Compound
	}

	override fun setNBT(item: ItemStack, compound: Compound) {
		val nmsItem = CraftItemStack.asNMSCopy(item)
		val nmsTag = NBTUtil.valueToNBTBase(compound) as NBTTagCompound
		nmsItem.tag = nmsTag
		item.itemMeta = CraftItemStack.getItemMeta(nmsItem)
//		val meta = CraftItemStack.getItemMeta(nmsItem)
//
//		val newLore: MutableList<String>
//
//		val existingLore = meta.lore
//		if(existingLore != null) {
//			newLore = existingLore.toMutableList()
//			newLore.removeAll { it.startsWith(nbtLorePrefix) }
//		}else{
//			newLore = ArrayList()
//		}
//
//		val bytes = NBTCompressedStreamTools.a(nmsTag)
//		val string = Base64.getEncoder().encodeToString(bytes)
//		newLore.add("$nbtLorePrefix$string")
//
//		meta.lore = newLore
//		item.itemMeta = meta
	}

	override fun toNBT(item: ItemStack): Compound {
		return NBTUtil.nbtBaseValue(item.handle.save(NBTTagCompound())) as Compound
	}

	override fun fromNBT(compound: Compound): ItemStack {
		return CraftItemStack.asCraftMirror(nmsFromNBT(compound))
	}

	private fun nmsFromNBT(compound: Compound): net.minecraft.server.v1_7_R4.ItemStack {
		return net.minecraft.server.v1_7_R4.ItemStack.createStack(NBTUtil.valueToNBTBase(compound) as NBTTagCompound)
	}

	internal fun asCraftItemStack(item: ItemStack): CraftItemStack {
		return item as? CraftItemStack ?: CraftItemStack.asCraftCopy(item)
	}

	internal val ItemStack.handle: net.minecraft.server.v1_7_R4.ItemStack
		get() {
			val craft = asCraftItemStack(this)
			val handleField = craft.javaClass.getDeclaredField("handle")
			handleField.isAccessible = true

			val handleValue = handleField.get(craft) as net.minecraft.server.v1_7_R4.ItemStack?
			return handleValue ?: CraftItemStack.asNMSCopy(this)
		}
}