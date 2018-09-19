package blue.sparse.minecraft;

import blue.sparse.maven.DependencyManager;
import blue.sparse.maven.MavenArtifact;
import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;

/**
 * Loads Kotlin dependencies
 */
public final class KotlinLoader {
	public static final  String KOTLIN_EAP_REPO = "http://dl.bintray.com/kotlin/kotlin-eap/";
	private static final String KOTLIN_GROUP    = "org.jetbrains.kotlin";
	private static final MavenArtifact JETBRAINS_ANNOTATIONS = new MavenArtifact("org.jetbrains", "annotations");
	
	private static final MavenArtifact KOTLINX_COROUTINES_CORE = new MavenArtifact("org.jetbrains.kotlinx", "kotlinx-coroutines-core");
	
	private static final MavenArtifact KOTLIN_STDLIB      = new MavenArtifact(KOTLIN_EAP_REPO, KOTLIN_GROUP, "kotlin-stdlib");
	private static final MavenArtifact KOTLIN_STDLIB_JDK8 = new MavenArtifact(KOTLIN_EAP_REPO, KOTLIN_GROUP, "kotlin-stdlib-jdk8");
	private static final MavenArtifact KOTLIN_STDLIB_JDK7 = new MavenArtifact(KOTLIN_EAP_REPO, KOTLIN_GROUP, "kotlin-stdlib-jdk7");
	private static final MavenArtifact KOTLIN_REFLECT     = new MavenArtifact(KOTLIN_EAP_REPO, KOTLIN_GROUP, "kotlin-reflect");
	
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
	 *
	 * @param folder location of dependency jars
	 */
	static void load(File folder) {
		final HashMap<MavenArtifact, String> forceVersions = new HashMap<>();
		forceVersions.put(KOTLINX_COROUTINES_CORE, "0.26.0-eap13");
		forceVersions.put(KOTLIN_STDLIB, "1.3-M2");
		forceVersions.put(KOTLIN_STDLIB_JDK8, "1.3-M2");
		forceVersions.put(KOTLIN_STDLIB_JDK7, "1.3-M2");
		forceVersions.put(KOTLIN_REFLECT, "1.3-M2");
		DependencyManager.updateAndLoadDependencies(dependencies, forceVersions, folder, DependencyManager.getHighestClassLoader());
	}
}
