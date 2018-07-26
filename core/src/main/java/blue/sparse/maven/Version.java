package blue.sparse.maven;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public final class Version implements Comparable<Version> {
	
	public final int major;
	public final int minor;
	public final int patch;
	
	public Version(int major, int minor, int patch) {
		this.major = major;
		this.minor = minor;
		this.patch = patch;
	}
	
	public int hashCode() {
		return Objects.hash(major, minor, patch);
	}
	
	public boolean equals(Object o) {
		if(this == o) return true;
		if(!(o instanceof Version)) return false;
		Version version = (Version) o;
		return major == version.major &&
				minor == version.minor &&
				patch == version.patch;
	}
	
	public String toString() {
		return major + "." + minor + "." + patch;
	}
	
	public boolean lessThan(@NotNull Version other) {
		return compareTo(other) < 0;
	}
	
	public int compareTo(@NotNull Version other) {
		int majorCompare = Integer.compare(major, other.major);
		if(majorCompare != 0)
			return majorCompare;
		
		int minorCompare = Integer.compare(minor, other.minor);
		if(minorCompare != 0)
			return minorCompare;
		
		int patchCompare = Integer.compare(patch, other.patch);
		if(patchCompare != 0)
			return patchCompare;
		
		return 0;
	}
	
	public boolean greaterThan(@NotNull Version other) {
		return compareTo(other) > 0;
	}
	
	public static Version fromString(@NotNull String string) {
		int dashIndex = string.lastIndexOf('-');
		final String trimmed;
		if(dashIndex == -1)
			trimmed = string;
		else
			trimmed = string.substring(0, dashIndex);
		
		System.out.println("Version.fromString(\""+trimmed+"\")");
		final String[] parts = trimmed.split("\\.");
		if(parts.length == 2)
			return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), 0);
		
		return new Version(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
	}
}
