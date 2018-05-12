package blue.sparse.maven;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class MavenProject {
	private final String group;
	private final String artifact;
	
	public MavenProject(String group, String artifact) {
		this.group = group;
		this.artifact = artifact;
	}
	
	public String getGroup() {
		return group;
	}
	
	public String getArtifact() {
		return artifact;
	}
	
	public File updateIfNeeded(File original) throws IOException {
		final String name = original.getName();
//		if(!name.startsWith(toString()))
//			throw new IllegalArgumentException("Tried to update with file with different artifact.");
		if(name.length() < artifact.length() + 2)
			throw new IllegalArgumentException("File name did not contain version.");
		
		final String latestFileName = getLatestFileName();
		if(name.equals(latestFileName))
			return original;
		
		original.delete();
		final File latestFile = new File(original.getParentFile(), latestFileName);
		downloadLatest(latestFile);
		return latestFile;
	}
	
	public String getLatestFileName() throws IOException {
		return group + '-' + artifact + '-' + getLatestVersion() + ".jar";
	}
	
	public void downloadLatest(File target) throws IOException {
		MavenDownloader.downloadLatestJar(group, artifact, target);
	}
	
	public String getLatestVersion() throws IOException {
		return MavenDownloader.getLatestVersion(group, artifact);
	}
	
	public int hashCode() {
		return Objects.hash(group, artifact);
	}
	
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof MavenProject)) return false;
		MavenProject that = (MavenProject) o;
		return Objects.equals(group, that.group) &&
				Objects.equals(artifact, that.artifact);
	}
	
	public String toString() {
		return group+'-'+artifact;
	}
}
