package at.deder.ybr.repository;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This bean provides information about the file index of a package on the server.
 * @author lycis
 */
public class PackageIndex {
    private List<String> index = new ArrayList<>();
    
    public PackageIndex(String indexContent) {
        index = Arrays.asList(indexContent.split("\n"));
    }
    
    public List<String> getIndex() {
        return index;
    }
}
