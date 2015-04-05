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
	private String repositoryName = "";
	
	public NexusServer(String repositoryName) {
		this.repositoryName = repositoryName;
	}

	@Override
	public ServerManifest getManifest() throws ProtocolViolationException {
		// TODO Auto-generated method stub
		throw new NotImplementedException();
	}

	@Override
	public Banner getBanner() throws ProtocolViolationException {
		throw new NotImplementedException();
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
		throw new NotImplementedException();
	}

}
