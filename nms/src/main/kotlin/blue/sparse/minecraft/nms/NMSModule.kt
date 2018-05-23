package blue.sparse.minecraft.nms

import blue.sparse.minecraft.module.Module
import blue.sparse.minecraft.module.ModuleDefinition
import blue.sparse.minecraft.nms.api.*

@ModuleDefinition
object NMSModule : Module {

	private val nmsHandlers = HashSet<NMSHandler>()

	val itemNMS = create("ItemStackImpl") as ItemStackNMS
	val placeholderNMS = create("PlaceholderImpl") as PlaceholdersNMS

	override fun onEnable() {
		println("NMSModule enabled with ${nmsHandlers.size} NMS handlers.")
		nmsHandlers.forEach(NMSHandler::onEnable)
	}

	override fun onDisable() {
		nmsHandlers.forEach(NMSHandler::onDisable)
	}

	private fun getClass(name: String): Class<*> {
		return Class.forName("${javaClass.`package`.name}.${NMSVersion.current}.$name")
	}

	private fun create(name: String): Any? {
		val instance = getClass(name).constructors.first().newInstance() as NMSHandler
		nmsHandlers.add(instance)
		return instance
	}

}