package blue.sparse.minecraft.nms.extensions

import blue.sparse.minecraft.nms.NMSModule
import org.bukkit.inventory.ItemStack

var ItemStack.nbt
	get() = NMSModule.itemNMS.getNBT(this)
	set(value) = NMSModule.itemNMS.setNBT(this, value)