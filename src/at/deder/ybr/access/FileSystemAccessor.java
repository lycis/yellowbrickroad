package at.deder.ybr.access;

import java.io.File;

/**
 * Default implementation of <code>IFileSystemAccessor</code>.
 * 
 * This accessor provides access to the default file system of a host.
 * 
 * @author lycis
 *
 */
public class FileSystemAccessor implements IFileSystemAccessor {

	@Override
	public boolean exists(String path) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public File getFile(String path) {
		// TODO Auto-generated method stub
		return null;
	}

}
