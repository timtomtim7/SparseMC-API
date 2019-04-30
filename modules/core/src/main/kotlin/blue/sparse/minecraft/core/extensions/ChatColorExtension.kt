package blue.sparse.minecraft.core.extensions

import org.bukkit.ChatColor


/** @see ChatColor.translateAlternateColorCodes */
fun color(string: String): String = ChatColor.translateAlternateColorCodes('&', string)

/** @see ChatColor.translateAlternateColorCodes */
fun colour(string: String): String = ChatColor.translateAlternateColorCodes('&', string)

/** Applies [ChatColor.translateAlternateColorCodes] to every String in [strings]*/
fun coloredListOf(vararg strings: String) = strings.map(::color)

/** Applies [ChatColor.translateAlternateColorCodes] to every String in [strings]*/
fun colouredListOf(vararg strings: String) = coloredListOf(*strings)

/** @see ChatColor.translateAlternateColorCodes */
val String.colored get() = color(this)

/** @see ChatColor.translateAlternateColorCodes */
val String.coloured get() = color(this)

/** Applies [ChatColor.translateAlternateColorCodes] to every String in this [Iterable] */
val Iterable<String>.colored get() = map(::color)

/** Applies [ChatColor.translateAlternateColorCodes] to every String in this [Iterable]*/
val Iterable<String>.coloured get() = map(::color)

/** Alias for [ChatColor] */
typealias ChatColour = ChatColor