package blue.sparse.maven;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public final class MavenDownloader {
	
	public static final String MAVEN_CENTRAL = "http://repo1.maven.org/maven2/";
	
	private MavenDownloader() {}
	
	public static void downloadLatestJar(String repo, String group, String artifact, File target) throws IOException {
		downloadJar(repo, group, artifact, getLatestVersion(repo, group, artifact), target);
	}
	
	public static void downloadJar(String repo, String group, String artifact, String version, File target) throws IOException {
		final String url = buildMavenCentralURL(repo, group, artifact) + version + '/' + artifact + '-' + version + ".jar";

		download(url, target);
	}
	
	public static String getLatestVersion(String repo, String group, String artifact) throws IOException {
		final String xmlString = downloadText(buildMavenCentralURL(repo, group, artifact) + "maven-metadata.xml");
		final int begin;
		final int end;
		
		final int latestIndex = xmlString.indexOf("<latest>");
		if(latestIndex == -1) {
			begin = xmlString.indexOf("<release>") + 9;
			end = xmlString.indexOf("</release>");
		}else{
			begin = latestIndex + 8;
			end = xmlString.indexOf("</latest>");
		}
		
		return xmlString.substring(begin, end);
	}
	
	private static String buildMavenCentralURL(String repo, String group, String artifact) {
		return repo + group.replace('.', '/') + '/' + artifact + '/';
	}
	
	private static void download(String urlString, File target) throws IOException {
		final URL url = new URL(urlString);
		final URLConnection conn = url.openConnection();
		conn.setRequestProperty("User-Agent", "sparse-mc-api");
		try(final InputStream in = conn.getInputStream()) {
			Files.copy(in, target.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	private static String downloadText(String urlString) throws IOException {
		final URL url = new URL(urlString);
		final URLConnection conn = url.openConnection();
		conn.setRequestProperty("User-Agent", "sparse-mc-api");
		try(final InputStream in = conn.getInputStream()) {
			return CharStreams.toString(new InputStreamReader(in, Charsets.UTF_8));
		}
	}
}
