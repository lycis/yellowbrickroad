package at.deder.ybr.server;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.http.client.utils.URIBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import at.deder.ybr.configuration.ServerManifest;
import at.deder.ybr.repository.NexusRepositoryEntry;
import at.deder.ybr.repository.PackageHash;
import at.deder.ybr.repository.PackageIndex;
import at.deder.ybr.repository.RepositoryEntry;

/**
 * Connection to a Sonatype Nexus server.
 * @author lycis
 *
 */
public class NexusServer extends SimpleHttpServer implements IServerGateway {
	private String repository = "";
	
	// TODO implement caching to minimise number of roundtrips to server
	
	public NexusServer(String host, String fixedPath, int port, String repository) {
		super(host, fixedPath, port);
		this.repository = repository;
	}
	
	@Override
	public ServerManifest getManifest() throws ProtocolViolationException {
		// TODO crawl through repository: https://<server>/content/repositories/<repository>/
		String pageContent;
		try {
			pageContent = getTextFromServer("/content/repositories/"+repository+"/");
		} catch (IOException e) {
			throw new ProtocolViolationException("repository content is not accessible", e);	
		}
		
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
			      .parse(new InputSource(new StringReader(pageContent)));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new ProtocolViolationException("server returned a malformed repository index", e);
		}
		
		// get all links
		NodeList links = null;  
		try {
			links = (NodeList) XPathFactory.newInstance().newXPath().compile("/html/body//a/@href").evaluate(doc, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			throw new ProtocolViolationException("malformed pom or xpath", e);
		}
		
		// TODO incomplete
		
		throw new NotImplementedException();
	}

	@Override
	public Banner getBanner() throws ProtocolViolationException {
		Banner nexusBanner = new Banner();
		nexusBanner.setText("** Nexus @ "+getHostname()+":"+getPort()+" ["+repository+"]");
		return nexusBanner;
	}

	@Override
	public Map<String, byte[]> getFilesOfPackage(String pkgName) throws ProtocolViolationException {
		
		NexusRepositoryEntry nre = null;
		try {
			nre = new NexusRepositoryEntry(pkgName);
		} catch (IllegalArgumentException ex) {
			throw new ProtocolViolationException("package not accessible", ex);
		}
		
		// resolve package to get some necessary data
		Document doc = resolvePackage(nre);
		String version = "";
		String extension = "";
		String path = "";
		String artefactId = "";
		try {
			version = XPathFactory.newInstance().newXPath().compile("/artifact-resolution/data/baseVersion").evaluate(doc);
			extension = XPathFactory.newInstance().newXPath().compile("/artifact-resolution/data/extension").evaluate(doc);
			path = XPathFactory.newInstance().newXPath().compile("/artifact-resolution/data/repositoryPath").evaluate(doc);
			artefactId = XPathFactory.newInstance().newXPath().compile("/artifact-resolution/data/artifactId").evaluate(doc);
		} catch (XPathExpressionException e) {
			throw new ProtocolViolationException("malformed pom or xpath", e);
		}
		
		// get data
		URIBuilder uri = new URIBuilder();
		uri.setPath("/content/repositories/"+repository+"/"+path);
		byte[] data = null;
		
		try {
			data = getBinaryFromServer(uri.build());
		} catch (IOException | URISyntaxException e) {
			throw new ProtocolViolationException("could not get file content", e);
		}
		
		
		
		Map<String, byte[]> files = new HashMap<>();
		files.put(artefactId+"-"+version+"."+extension, data);
		return files;
	}

	@Override
	public RepositoryEntry getPackage(String name)
			throws ProtocolViolationException {		
		NexusRepositoryEntry nre = null;
		try {
			nre = new NexusRepositoryEntry(name);
		} catch (IllegalArgumentException ex) {
			throw new ProtocolViolationException("package not accessible", ex);
		}
		
		Document sPom = getSerialisedPom(nre);
		String description = "";
		try {
			description = XPathFactory.newInstance().newXPath().compile("/project/description").evaluate(sPom);
		} catch (XPathExpressionException e) {
			throw new ProtocolViolationException("malformed pom or xpath", e);
		}
		
		nre.setParent(null);
		nre.setDescription(description);
		
		// set sha1 hash
		Document resolvedDoc = resolvePackage(nre);
		String hash = "";
		try {
			hash = XPathFactory.newInstance().newXPath().compile("/artifact-resolution/data/sha1").evaluate(resolvedDoc);
		} catch (XPathExpressionException e) {
			throw new ProtocolViolationException("malformed pom or xpath", e);
		}
		
		nre.setPackageHash(new PackageHash(hash));
		
		return nre;
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
		
		Document doc = resolvePackage(nre);
		
		
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

	/**
	 * Call to Nexus resolve API request
	 * 
	 * @param pkgName
	 * @return
	 * @throws ProtocolViolationException
	 */
	private Document resolvePackage(NexusRepositoryEntry nre) throws ProtocolViolationException {
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
		
		return doc;
	}

	private Document getSerialisedPom(NexusRepositoryEntry nre) throws ProtocolViolationException {
		URIBuilder uri = new URIBuilder();
		uri.setPath("/service/local/artifact/maven");
		uri.addParameter("g", nre.getGroupId());
		uri.addParameter("a", nre.getArtefactId());
		uri.addParameter("v", nre.getVersion());
		uri.addParameter("r", repository);
		
		String pomStr = "";
		try {
			pomStr = getTextFromServer(uri.build());
		} catch(IOException | URISyntaxException ex) {
			throw new ProtocolViolationException("serialised pom not acessible", ex);
		}
		
		Document doc = null;
		try {
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder()
			      .parse(new InputSource(new StringReader(pomStr)));
		} catch (SAXException | IOException | ParserConfigurationException e) {
			throw new ProtocolViolationException("server returned a malformed serialised pom", e);
		}
		
		return doc;
	}
}
