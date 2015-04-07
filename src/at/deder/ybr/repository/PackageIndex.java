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
        index = new ArrayList<>(Arrays.asList(indexContent.split("\n")));
    }
    
    public PackageIndex(File indexFile) throws IOException {
        this(IOUtils.toString(new FileInputStream(indexFile)));
    }
    
    public List<String> getIndex() {
        return index;
    }
    
    /**
     * Adds the given string as entry to the index.
     * @param entry
     */
    public void addEntry(String entry) {
    	index.add(entry);
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
            	for(String entry: getIndex()) {
            		
            		// apply regular expression
            		if(entry.startsWith("r::")) {
            			String rex = entry.substring("r::".length());
            			if(name.matches(rex)) {
            				return true;
            			}
            		}
            		
            		// check for exact match
            		if(entry.equals(name)) {
            			return true;
            		}
            	}
            	
                return false;
            }
        }));
    }
}
