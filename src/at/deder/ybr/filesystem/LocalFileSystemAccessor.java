package at.deder.ybr.filesystem;

import at.deder.ybr.channels.IOutputChannel;
import at.deder.ybr.channels.OutputChannelFactory;
import at.deder.ybr.configuration.ClientConfiguration;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

    @Override
    public File getClientConfigFile(String dirPath){
        File dir = getFile(dirPath);
        if(dir == null) {
            return dir;
        }
        
        IOutputChannel output = OutputChannelFactory.getOutputChannel();
        
        File[] list = dir.listFiles();
        for(File f: list) {
            BufferedReader br = null;
            try{
                br = new BufferedReader(new FileReader(f));
                String firstLine = br.readLine();
                if(("!"+ClientConfiguration.YAML_TAG).equals(firstLine)) {
                    return f;
                }
            } catch (IOException ex) {
                output.println("warning: could not check file "+f.getAbsolutePath()+ "(reason: "+ex.getMessage()+")");
                continue; // when file is not accessible try next one
            } finally {
                if(br != null) {
                    try{
                        br.close();
                    } catch(IOException ex){
                       output.println("warning: leaked resource");
                    }
                }
            }
        }
        
        return null;
    }

}
