package at.deder.ybr.server;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.client.utils.URIBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import at.deder.ybr.configuration.ServerManifest;
import at.deder.ybr.repository.NexusRepositoryEntry;
import at.deder.ybr.repository.PackageIndex;
import at.deder.ybr.repository.RepositoryEntry;

/**
 * Connection to a Sonatype Nexus server.
 * @author lycis
 *
 */
public class NexusServer extends SimpleHttpServer implements IServerGateway {
	private String repository = "";
	
	public NexusServer(String host, String fixedPath, int port, String repository) {
		super(host, fixedPath, port);
		this.repository = repository;
	}

	@Override
	public ServerManifest getManifest() throws ProtocolViolationException {
		// TODO crawl through repository: https://<server>/content/repositories/<repository>/
		throw new NotImplementedException();
	}

	@Override
	public Banner getBanner() throws ProtocolViolationException {
		Banner nexusBanner = new Banner();
		nexusBanner.setText("** Nexus @ "+getHostname()+":"+getPort()+" ["+repository+"]");
		return nexusBanner;
	}

	@Override
	public Map<String, byte[]> getFilesOfPackage(String pkgName)
			throws ProtocolViolationException {
		throw new NotImplementedException();
	}

	@Override
	public RepositoryEntry getPackage(String name)
			throws ProtocolViolationException {
		throw new NotImplementedException();
	}

	@Override
	public PackageIndex getPackageIndex(String pkgName)
			throws ProtocolViolationException {
		
		NexusRepositoryEntry nre = null;
		try {
			nre = new NexusRepositoryEntry(pkgName);
		} catch (IllegalArgumentException ex) {
			throw new ProtocolViolationException("package index not accessible", ex);
		}
		
		// TODO parse POM: 
		// https://192.168.41.27/nexus/service/local/artifact/maven/resolve?g=com.automic.ae&a=ucdj&v=LATEST&r=snapshots&p=jar
		// -> get baseVersion
		// create regex from artefact+baseversion+*+extension
		// -> add to index
		
		URIBuilder uri = new URIBuilder();
		uri.setPath("/service/local/artifact/maven/resolve");
		uri.addParameter("g", nre.getGroupId());
		uri.addParameter("a", nre.getArtefactId());
		uri.addParameter("v", nre.getVersion());
		uri.addParameter("p", nre.getPackageType());
		uri.addParameter("r", repository);
		
		String pomStr = "";
		try {
			pomStr = getTextFromServer(uri.build());
		} catch(IOException | URISyntaxException ex) {
			throw new ProtocolViolationException("package index not acessible", ex);
		}
		
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
			      .parse(new InputSource(new StringReader(pomStr)));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new ProtocolViolationException("server returned a malformed POM", e);
		}
		
		
		String baseVersion = "";
		String extension = "";
		try {
			baseVersion = XPathFactory.newInstance().newXPath().compile("/artifact-resolution/data/baseVersion").evaluate(doc);
			extension = XPathFactory.newInstance().newXPath().compile("/artifact-resolution/data/extension").evaluate(doc);
		} catch (XPathExpressionException e) {
			throw new ProtocolViolationException("malformed pom or xpath", e);
		}
		
		
		// create package index
		PackageIndex index = new PackageIndex("");
		String indexRegex = "r::"+nre.getArtefactId()+"-"+baseVersion+".*?\\."+extension;
		index.addEntry(indexRegex);
		return index;
	}

}
