package blue.sparse.minecraft.core.extensions.event

import org.bukkit.event.block.Action

fun Action.isRightClick(): Boolean {
	return this == Action.RIGHT_CLICK_BLOCK || this == Action.RIGHT_CLICK_AIR
}

fun Action.isLeftClick(): Boolean {
	return this == Action.LEFT_CLICK_BLOCK || this == Action.LEFT_CLICK_AIR
}

fun Action.isOnBlock(): Boolean {
	return this == Action.LEFT_CLICK_BLOCK || this == Action.RIGHT_CLICK_BLOCK
}

fun Action.isOnAir(): Boolean {
	return this == Action.LEFT_CLICK_AIR || this == Action.RIGHT_CLICK_AIR
}