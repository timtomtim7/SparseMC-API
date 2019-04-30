package blue.sparse.minecraft.scripting.kotlin

interface ScriptNameProvider {

	fun get(source: String): String

	class Sequential(val base: String = "Script"): ScriptNameProvider {
		private var i = 0

		override fun get(source: String) = "$base${i++}"
	}

}