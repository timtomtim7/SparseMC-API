package blue.sparse.minecraft.module

import kotlin.reflect.KClass

enum class ModuleType(val className: String) {
	CORE("blue.sparse.minecraft.core.CoreModule"),
	NMS("blue.sparse.minecraft.nms.NMSModule"),
	COMPATIBILITY("blue.sparse.minecraft.compatibility.CompatibilityModule"),
	COMMANDS("blue.sparse.minecraft.commands.CommandsModule"),
	INVENTORY("blue.sparse.minecraft.inventory.InventoryModule"),
	MATH("blue.sparse.minecraft.math.MathModule"),
	PERSISTENT("blue.sparse.minecraft.persistent.PersistentModule"),
	SCHEDULER("blue.sparse.minecraft.scheduler.SchedulerModule"),
	SCRIPTING("blue.sparse.minecraft.scripting.ScriptingModule");

	@Suppress("UNCHECKED_CAST")
	val clazz: KClass<out Module>
		get() = Class.forName(className).kotlin as KClass<out Module>
}
