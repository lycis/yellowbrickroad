package at.deder.ybr.server;

import java.util.Map;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import at.deder.ybr.configuration.ServerManifest;
import at.deder.ybr.repository.PackageIndex;
import at.deder.ybr.repository.RepositoryEntry;

/**
 * Connection to a Sonatype Nexus server.
 * @author lycis
 *
 */
public class NexusServer implements IServerGateway {
	private String repository = "";
	private String host = "";
	private int port = 80;
	
	public NexusServer(String host, int port, String repository) {
		this.repository = repository;
		this.host = host;
		this.port = port;
	}

	@Override
	public ServerManifest getManifest() throws ProtocolViolationException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public Banner getBanner() throws ProtocolViolationException {
		Banner nexusBanner = new Banner();
		nexusBanner.setText("** Nexus @ "+host+":"+port+" ["+repository+"]");
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
		
		// TODO parse POM: 
		// https://192.168.41.27/nexus/service/local/artifact/maven/resolve?g=com.automic.ae&a=ucdj&v=LATEST&r=snapshots&p=jar
		// -> get baseVersion
		// create regex from artefact+baseversion+*+extension
		// -> add to index
		throw new NotImplementedException();
	}

}
