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
	
	public static Map<MavenProject, File> downloadDependencies(Collection<MavenProject> projects, File folder) {
		folder.mkdirs();
		
		final Map<MavenProject, File> result = new HashMap<>();
		
		for(MavenProject project : projects) {
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
				if(closest.isPresent()) {
					final File file = project.updateIfNeeded(closest.get());
					result.put(project, file);
				}else {
					final File file = new File(folder, project.getLatestFileName());
					project.downloadLatest(file);
					result.put(project, file);
				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	public static void updateAndLoadDependencies(Collection<MavenProject> projects, File folder) {
		final Collection<File> dependencies = downloadDependencies(projects, folder).values();
		for(File dependency : dependencies) {
			try {
				load(dependency);
			}catch(MalformedURLException | ReflectiveOperationException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void load(File jar) throws MalformedURLException, ReflectiveOperationException {
		addURL(getClassLoader(), jar.toURI().toURL());
	}
	
	private static void addURL(URLClassLoader loader, URL url) throws ReflectiveOperationException {
		final Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
		addURL.setAccessible(true);
		addURL.invoke(loader, url);
	}
	
	private static URLClassLoader getClassLoader() {
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
}
