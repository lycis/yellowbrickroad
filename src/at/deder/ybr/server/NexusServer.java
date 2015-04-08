package at.deder.ybr.server;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
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
	public Map<String, byte[]> getFilesOfPackage(String pkgName)
			throws ProtocolViolationException {
		throw new NotImplementedException();
	}

	@Override
	public RepositoryEntry getPackage(String name)
			throws ProtocolViolationException {
		ServerManifest manifest = getManifest();
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
		
		String pomStr = resolvePackage(nre);
		
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

	/**
	 * call to nexus resolve API request.
	 * 
	 * @param pkgName
	 * @return
	 * @throws ProtocolViolationException
	 */
	private String resolvePackage(String pkgName) throws ProtocolViolationException {
		NexusRepositoryEntry nre = null;
		try {
			nre = new NexusRepositoryEntry(pkgName);
		} catch (IllegalArgumentException ex) {
			throw new ProtocolViolationException("package index not accessible", ex);
		}
		
		return resolvePackage(nre);
	}

	/**
	 * Call to Nexus resolve API request
	 * 
	 * @param pkgName
	 * @return
	 * @throws ProtocolViolationException
	 */
	private String resolvePackage(NexusRepositoryEntry nre) throws ProtocolViolationException {
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
		
		return pomStr;
	}

}
