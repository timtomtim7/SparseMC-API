package blue.sparse.maven;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class MavenDownloader {
	private MavenDownloader() {}
	
	public static void downloadLatestJar(String group, String artifact, File target) throws IOException {
		downloadJar(group, artifact, getLatestVersion(group, artifact), target);
	}
	
	public static void downloadJar(String group, String artifact, String version, File target) throws IOException {
		final String url = buildMavenCentralURL(group, artifact) + version + '/' + artifact + '-' + version + ".jar";
		
		download(url, target);
	}
	
	public static String getLatestVersion(String group, String artifact) throws IOException {
		final String xmlString = downloadText(buildMavenCentralURL(group, artifact) + "maven-metadata.xml");
		final int begin = xmlString.indexOf("<latest>") + 8;
		final int end = xmlString.indexOf("</latest>");
		return xmlString.substring(begin, end);
	}
	
	private static String buildMavenCentralURL(String group, String artifact) {
		return "http://repo1.maven.org/maven2/" + group.replace('.', '/') + '/' + artifact + '/';
	}
	
	private static void download(String url, File target) throws IOException {
		try(final InputStream in = new URL(url).openStream()) {
			Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	private static String downloadText(String url) throws IOException {
		try(final InputStream in = new URL(url).openStream()) {
			return CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
		}
	}
}
