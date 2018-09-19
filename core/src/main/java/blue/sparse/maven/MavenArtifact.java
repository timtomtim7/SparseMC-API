package blue.sparse.maven;

import blue.sparse.minecraft.SparseMCAPIPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MavenArtifact {
	private final String repo;
	private final String group;
	private final String artifact;
	
	public MavenArtifact(String repo, String group, String artifact) {
		this.repo = repo;
		this.group = group;
		this.artifact = artifact;
	}
	
	public MavenArtifact(String group, String artifact) {
		this(MavenDownloader.MAVEN_CENTRAL, group, artifact);
	}
	
	public String getGroup() {
		return group;
	}
	
	public String getArtifact() {
		return artifact;
	}
	
	public File updateIfNeeded(File original, String forceVersion) throws IOException {
		final String name = original.getName();
		if(name.length() < artifact.length() + 2)
			throw new IllegalArgumentException("File name did not contain version.");
		
		if(!name.contains("$")) {
			original.delete();
			final File latestFile = new File(original.getParentFile(), getLatestFileName());
			downloadLatest(latestFile);
			return latestFile;
		}
		
		String currentVersionString = name.substring(name.lastIndexOf('$') + 1);
		currentVersionString = currentVersionString.substring(0, currentVersionString.lastIndexOf('.'));
		final String latestVersionString = getLatestVersion();
		
		System.out.println(toString()+" (current "+currentVersionString+") (latest "+latestVersionString+")");
		
		Version currentVersion = Version.fromString(currentVersionString);
		Version latestVersion = Version.fromString(latestVersionString);
		
		if(forceVersion == null && currentVersion.compareTo(latestVersion) >= 0)
			return original;
		
		original.delete();
		if(forceVersion != null) {
			final File file = new File(original.getParentFile(), getFileName(forceVersion));
			download(file, forceVersion);
			return file;
		}else{
			final File latestFile = new File(original.getParentFile(), getLatestFileName());
			downloadLatest(latestFile);
			return latestFile;
		}
	}
	
	public String getLatestFileName() throws IOException {
		return getFileName(getLatestVersion());
	}
	
	public String getFileName(String version) {
		return group + '-' + artifact + '$' + version + ".jar";
	}
	
	public void downloadLatest(File target) throws IOException {
		SparseMCAPIPlugin.getPlugin().getLogger().info("Downloading dependency -> "+group+":"+artifact);
		MavenDownloader.downloadLatestJar(repo, group, artifact, target);
	}
	
	public void download(File target, String version) throws IOException {
		SparseMCAPIPlugin.getPlugin().getLogger().info("Downloading dependency -> "+group+":"+artifact);
		MavenDownloader.downloadJar(repo, group, artifact, version, target);
	}
	
	public String getLatestVersion() throws IOException {
		return MavenDownloader.getLatestVersion(repo, group, artifact);
	}
	
	public int hashCode() {
		return Objects.hash(group, artifact);
	}
	
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof MavenArtifact)) return false;
		MavenArtifact that = (MavenArtifact) o;
		return Objects.equals(group, that.group) &&
				Objects.equals(artifact, that.artifact);
	}
	
	public String toString() {
		return group+'-'+artifact;
	}
}
