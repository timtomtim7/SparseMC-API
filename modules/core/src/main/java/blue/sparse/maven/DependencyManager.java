package blue.sparse.maven;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public final class DependencyManager {
	
	private DependencyManager() {}
	
	public static void updateAndLoadDependencies(Collection<MavenArtifact> projects, File folder) {
		updateAndLoadDependencies(projects, new HashMap<>(), folder, getHighestClassLoader());
	}
	
	public static void updateAndLoadDependencies(Collection<MavenArtifact> projects, Map<MavenArtifact, String> forceVersion, File folder, URLClassLoader classLoader) {
		final Collection<File> dependencies = downloadDependencies(projects, forceVersion, folder).values();
		for(File dependency : dependencies) {
			try {
				load(classLoader, dependency);
			}catch(MalformedURLException | ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static URLClassLoader getHighestClassLoader() {
		final ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
		if(systemClassLoader instanceof URLClassLoader)
			return (URLClassLoader) systemClassLoader;
		
		final ClassLoader classLoader = DependencyManager.class.getClassLoader();
		final ClassLoader parent = classLoader.getParent();
		if(parent instanceof URLClassLoader)
			return (URLClassLoader) parent;
		
		if(classLoader instanceof URLClassLoader)
			return (URLClassLoader) classLoader;
		
		return null;
	}
	
	public static Map<MavenArtifact, File> downloadDependencies(Collection<MavenArtifact> projects, Map<MavenArtifact, String> forceVersion, File folder) {
		folder.mkdirs();
		
		final Map<MavenArtifact, File> result = new HashMap<>();
		
		for(MavenArtifact project : projects) {
			final File[] files = folder.listFiles((dir, name) -> name.contains(project.toString()));
			if(files == null) {
				try {
					final File file = new File(folder, project.getLatestFileName());
					project.downloadLatest(file);
					result.put(project, file);
				}catch(IOException e) {
					e.printStackTrace();
				}
				continue;
			}
			
			final Optional<File> closest = Arrays.stream(files).min(Comparator.comparingInt(v -> v.getName().compareTo(project.toString())));
			
			try {
				final String forced = forceVersion.get(project);
				if(closest.isPresent()) {
					final File file = project.updateIfNeeded(closest.get(), forced);
					result.put(project, file);
				}else {
					File file;
					if(forced != null) {
						file = new File(folder, project.getFileName(forced));
						project.download(file, forced);
					}else {
						file = new File(folder, project.getLatestFileName());
						project.downloadLatest(file);
					}
					result.put(project, file);
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static void load(URLClassLoader classLoader, File jar) throws MalformedURLException, ReflectiveOperationException {
		addURL(classLoader, jar.toURI().toURL());
	}
	
	private static void addURL(URLClassLoader loader, URL url) throws ReflectiveOperationException {
		final Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
		addURL.setAccessible(true);
		addURL.invoke(loader, url);
	}
	
	public static void load(File jar) throws MalformedURLException, ReflectiveOperationException {
		load(getHighestClassLoader(), jar);
	}
}
