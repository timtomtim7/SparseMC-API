package blue.sparse.minecraft;

import blue.sparse.minecraft.module.ModuleManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Plugin class for the SparseMC API
 */
public final class SparseMCAPIPlugin extends JavaPlugin {
	
	private static final List<Runnable>    disableHooks = new ArrayList<>();
	private static       SparseMCAPIPlugin plugin;
	
	/**
	 * @return the instance of SparseMCAPIPlugin
	 */
	public static SparseMCAPIPlugin getPlugin() {
		return plugin;
	}
	
	public static void addDisableHook(Runnable runnable) {
		disableHooks.add(runnable);
	}
	
	public void onLoad() {
		plugin = this;
		KotlinLoader.load(new File(getDataFolder(), "dependencies"));
	}
	
	public void onDisable() {
		ModuleManager.INSTANCE.onPluginDisable();
		disableHooks.forEach(Runnable::run);
		System.gc();
	}
	
	public void onEnable() {
		ModuleManager.INSTANCE.onPluginEnable();
	}
	
}
