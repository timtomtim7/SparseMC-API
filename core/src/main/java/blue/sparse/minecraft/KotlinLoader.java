package blue.sparse.minecraft;

import blue.sparse.maven.DependencyManager;
import blue.sparse.maven.MavenProject;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.Collection;

/**
 * Loads Kotlin dependencies
 */
final class KotlinLoader {
	private static final String KOTLIN_GROUP = "org.jetbrains.kotlin";
	
	private static final MavenProject JETBRAINS_ANNOTATIONS = new MavenProject("org.jetbrains", "annotations");
	
	private static final MavenProject KOTLINX_COROUTINES_CORE = new MavenProject("org.jetbrains.kotlinx", "kotlinx-coroutines-core");
	
	private static final MavenProject KOTLIN_STDLIB      = new MavenProject(KOTLIN_GROUP, "kotlin-stdlib");
	private static final MavenProject KOTLIN_STDLIB_JDK8 = new MavenProject(KOTLIN_GROUP, "kotlin-stdlib-jdk8");
	private static final MavenProject KOTLIN_STDLIB_JDK7 = new MavenProject(KOTLIN_GROUP, "kotlin-stdlib-jdk7");
	private static final MavenProject KOTLIN_REFLECT     = new MavenProject(KOTLIN_GROUP, "kotlin-reflect");
	
	private static final Collection<MavenProject> dependencies = ImmutableList.of(
			JETBRAINS_ANNOTATIONS,
			KOTLIN_STDLIB,
			KOTLIN_STDLIB_JDK8,
			KOTLIN_STDLIB_JDK7,
			KOTLIN_REFLECT,
			KOTLINX_COROUTINES_CORE
	);
	
	private KotlinLoader() {}
	
	/**
	 * Update and load the Kotlin dependencies in the provided folder
	 * @param folder location of dependency jars
	 */
	static void load(File folder) {
		DependencyManager.updateAndLoadDependencies(dependencies, folder);
	}
}
