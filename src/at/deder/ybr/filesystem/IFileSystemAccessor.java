package at.deder.ybr.filesystem;

import java.io.File;
import java.io.IOException;

/**
 * This interface allows access to the host file system. It can be implemented
 * to redirect file access from the default file system to something completely
 * different.
 *
 * @author lycis
 *
 */
public interface IFileSystemAccessor {

    /**
     * Check if a given path exists within the target file system.
     *
     * @param path path to the resource (file, folder, ...)
     * @return <code>true</code> if the path exists
     */
    public boolean exists(String path);

    /**
     * Provides the requested file or <code>null</code>.
     *
     * @param path
     * @return the requested file or <code>null</code> if the file does not
     * exist
     */
    public File getFile(String path);

    /**
     * Create a file in the connected file system.
     *
     * @param path path of the file
     * @param isFilder </code>true</code> if the created node is a directory
     * @return
     */
    public File createFile(File parent, String name, boolean isFolder) throws IOException;

    /**
     *
     * @return the current workdir as file
     */
    public File getWorkingDirectory();
    
    /**
     * Returns a file from the given directory.
     * 
     * @param dir directory that should contain the file
     * @param name name of the file you want to get (POSIX path anotation)
     * @return <code>null</code> if the file does not exist
     */
    public File getFileInDir(File dir, String name);
    
    /**
     * Returns the root of the file system.
     * @return 
     */
    public File getRoot();
    
    /**
     * Do any necessary clean ups.
     */
    public void destroy();
}
