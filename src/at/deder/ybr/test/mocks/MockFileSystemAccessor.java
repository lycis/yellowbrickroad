package at.deder.ybr.test.mocks;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.apache.commons.io.FileUtils;

import at.deder.ybr.access.IFileSystemAccessor;

/**
 * Mocks access to the file system. Use POSIX paths to access files.
 *
 * @author lycis
 *
 */
public class MockFileSystemAccessor implements IFileSystemAccessor {

    private File rootFolder = null;

    /**
     * initis a new temporary file structure
     */
    public MockFileSystemAccessor() {
        String uid = UUID.randomUUID().toString();
        rootFolder = new File(uid);
        rootFolder.mkdir();
    }

    /**
     * clean up
     */
    public void destroy() {
        try {
            FileUtils.deleteDirectory(rootFolder);
        } catch (IOException e) {
            // TODO unclean
            e.printStackTrace();
        }
    }

    @Override
    public boolean exists(String path) {
        if (!path.startsWith("/")) {
            path = "/" + path;
        }

        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        ArrayList<String> remainingNodes = splitFilePath(path);
        File current = rootFolder;

        return (recursiveGetFile(current, remainingNodes) != null);
    }

    private File recursiveGetFile(File currentNode, ArrayList<String> remainingNodes) {
        String nextNode = remainingNodes.remove(0);
        
        if(currentNode == null)
            return null;

        for (File child : currentNode.listFiles()) {
            if (child.getName().equals(nextNode)) {
                if (remainingNodes.size() > 0) {
                    return recursiveGetFile(child, remainingNodes);
                } else {
                    return child;
                }
            }
        }

        return null;
    }

    @Override
    public File getFile(String path) {
        if ("/".equals(path)) {
            // return root
            return rootFolder;
        }

        ArrayList<String> remainingNodes = splitFilePath(path);
        File current = rootFolder;

        return recursiveGetFile(current, remainingNodes);
    }

    @Override
    public File createFile(File parent, String name, boolean isFolder) {

        File reqFile = new File(parent.getAbsolutePath() + File.separator + name);
        try {
            if (isFolder) {
                reqFile.mkdir();
            } else {
                reqFile.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return reqFile;
    }

    private ArrayList<String> splitFilePath(String path) {
        String[] parts = path.split("/");
        ArrayList<String> remainingNodes = new ArrayList<String>();
        // remove empty elements
        for (String s : parts) {
            if (!s.isEmpty()) {
                remainingNodes.add(s);
            }
        }
        return remainingNodes;
    }

    @Override
    public File getWorkingDirectory() {
        return rootFolder; // simulate that the current root is unser workdir
    }

}
