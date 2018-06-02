package blue.sparse.minecraft.nms.v1_12_R1

import blue.sparse.minecraft.core.data.nbt.Compound
import blue.sparse.minecraft.nms.api.ItemStackNMS
import net.minecraft.server.v1_12_R1.NBTTagCompound
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.inventory.ItemStack

class ItemStackImpl : ItemStackNMS {
	private val craftMetaItemClass by lazy { Class.forName("org.bukkit.craftbukkit.v1_12_R1.inventory.CraftMetaItem") }

	override fun getNBT(item: ItemStack): Compound {
		val method = craftMetaItemClass.getDeclaredMethod("applyToItem", NBTTagCompound::class.java)
		method.isAccessible = true

		val nmsTag = NBTTagCompound()
		method(item.itemMeta, nmsTag)

		return NBTUtil.nbtBaseValue(nmsTag) as Compound
	}

	override fun setNBT(item: ItemStack, compound: Compound) {
		val nmsItem = CraftItemStack.asNMSCopy(item)
		nmsItem.tag = NBTUtil.valueToNBTBase(compound) as NBTTagCompound
		item.itemMeta = CraftItemStack.getItemMeta(nmsItem)
	}

	override fun toNBT(item: ItemStack): Compound {
		return NBTUtil.nbtBaseValue(item.handle.save(NBTTagCompound())) as Compound
	}

	override fun fromNBT(compound: Compound): ItemStack {
		return CraftItemStack.asCraftMirror(nmsFromNBT(compound))
	}

	private fun nmsFromNBT(compound: Compound): net.minecraft.server.v1_12_R1.ItemStack {
		return net.minecraft.server.v1_12_R1.ItemStack(NBTUtil.valueToNBTBase(compound) as NBTTagCompound)
	}

	internal fun asCraftItemStack(item: ItemStack): CraftItemStack {
		return item as? CraftItemStack ?: CraftItemStack.asCraftCopy(item)
	}

	internal val ItemStack.handle: net.minecraft.server.v1_12_R1.ItemStack
		get() {
			val craft = asCraftItemStack(this)
			val handleField = craft.javaClass.getDeclaredField("handle")
			handleField.isAccessible = true

			val handleValue = handleField.get(craft) as net.minecraft.server.v1_12_R1.ItemStack?
			return handleValue ?: CraftItemStack.asNMSCopy(this)
		}
}