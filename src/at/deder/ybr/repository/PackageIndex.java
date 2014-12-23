package at.deder.ybr.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.io.IOUtils;

/**
 * This bean provides information about the file index of a package on the server.
 * @author lycis
 */
public class PackageIndex {
    private List<String> index = new ArrayList<>();
    
    public PackageIndex(String indexContent) {
        index = Arrays.asList(indexContent.split("\n"));
    }
    
    public PackageIndex(File indexFile) throws IOException {
        this(IOUtils.toString(new FileInputStream(indexFile)));
    }
    
    public List<String> getIndex() {
        return index;
    }
    
    /**
     * get all files in a directory that match the index
     * @param dir
     * @return 
     */
    public List<File> applyTo(File dir) {
        if(!dir.isDirectory()) {
            return null;
        }
        
        return Arrays.asList(dir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return getIndex().contains(name);
            }
        }));
    }
}
