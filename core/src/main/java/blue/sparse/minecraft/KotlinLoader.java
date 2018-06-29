package blue.sparse.minecraft;

import blue.sparse.maven.DependencyManager;
import blue.sparse.maven.MavenArtifact;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.Collection;

/**
 * Loads Kotlin dependencies
 */
final class KotlinLoader {
	private static final String KOTLIN_GROUP = "org.jetbrains.kotlin";
	
	private static final MavenArtifact JETBRAINS_ANNOTATIONS = new MavenArtifact("org.jetbrains", "annotations");
	
	private static final MavenArtifact KOTLINX_COROUTINES_CORE = new MavenArtifact("org.jetbrains.kotlinx", "kotlinx-coroutines-core");
	
	private static final MavenArtifact KOTLIN_STDLIB      = new MavenArtifact(KOTLIN_GROUP, "kotlin-stdlib");
	private static final MavenArtifact KOTLIN_STDLIB_JDK8 = new MavenArtifact(KOTLIN_GROUP, "kotlin-stdlib-jdk8");
	private static final MavenArtifact KOTLIN_STDLIB_JDK7 = new MavenArtifact(KOTLIN_GROUP, "kotlin-stdlib-jdk7");
	private static final MavenArtifact KOTLIN_REFLECT     = new MavenArtifact(KOTLIN_GROUP, "kotlin-reflect");
	
	private static final Collection<MavenArtifact> dependencies = ImmutableList.of(
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
