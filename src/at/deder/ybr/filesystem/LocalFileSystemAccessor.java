package at.deder.ybr.filesystem;

import java.io.File;
import java.io.IOException;

/**
 * Default implementation of <code>IFileSystemAccessor</code>.
 *
 * This accessor provides access to the default file system of a host.
 *
 * @author lycis
 *
 */
public class LocalFileSystemAccessor implements IFileSystemAccessor {

    @Override
    public boolean exists(String path) {
        File f = new File(path);
        return f.exists();
    }

    @Override
    public File getFile(String path) {
        if (!exists(path)) {
            return null;
        }

        return new File(path);
    }

    @Override
    public File createFile(File parent, String name, boolean isFolder) throws IOException {
        File reqFile = new File(parent.getAbsolutePath() + File.separator + name);
        try {
            if (isFolder) {
                reqFile.mkdir();
            } else {
                reqFile.createNewFile();
            }
        } catch (IOException e) {
            throw e;
        }

        return reqFile;
    }

    @Override
    public File getWorkingDirectory() {
        return new File(".");
    }

    @Override
    public File getFileInDir(File dir, String name) {
        if(!dir.isDirectory()) {
            return null;
        }
        
        File[] list = dir.listFiles();
        for(File f: list) {
            if(f.getName().equals(name)) {
                return f;
            }
        }
        
        return null;
    }

    @Override
    public File getRoot() {
        return new File("/");
    }

    @Override
    public void destroy() {
        return; // not necessary
    }

}
