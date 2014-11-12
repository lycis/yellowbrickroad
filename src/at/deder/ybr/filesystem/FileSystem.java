package at.deder.ybr.filesystem;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This central service provides access to the file system.
 *
 * @author lycis
 */
public class FileSystem {

    private static Class<? extends IFileSystemAccessor> accessorClass = null;
    private static IFileSystemAccessor injectedAccessor = null;

    // not instantiable
    private FileSystem() {

    }

    /**
     * Provides the accessor that is used to access the file system.
     */
    synchronized public static IFileSystemAccessor getAccess() {
        if(injectedAccessor != null) {
            return injectedAccessor;
        }
        
        if (accessorClass == null) {
            return null;
        }

        IFileSystemAccessor accessor = null;
        try {
            accessor = accessorClass.newInstance();
        } catch (InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException("could not instantiate file system accessor", ex);
        }

        return accessor;
    }
    
    /**
     * revokes the access granted to the file system.
     */
    synchronized public static void releaseAccess(IFileSystemAccessor a) {
        if(a == injectedAccessor) {
            return;
        }
        
        a.destroy();
    }

    public static void setAccessorClass(Class<? extends IFileSystemAccessor> c) {
        accessorClass = c;
    }
    
    public static void injectAccessor(IFileSystemAccessor a) {
        if(a == null && injectedAccessor != null) {
            injectedAccessor.destroy();
        }
        
        injectedAccessor = a;
    }
}
