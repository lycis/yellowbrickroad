package at.deder.ybr.access;

import java.io.File;

/**
 * This interface allows access to the host file system. It can be implemented to redirect file access from the
 * default file system to something completely different.
 * 
 * @author lycis
 *
 */
public interface IFileSystemAccessor {
	
	/**
	 * Check if a given path exists within the target file system.
	 * @param path path to the resource (file, folder, ...)
	 * @return <code>true</code> if the path exists
	 */
	public boolean exists(String path);
	
	/**
	 * 
	 * @param path
	 * @return the requested file or <code>null</code> if the file does not exist
	 */
	public File getFile(String path);
	
}
