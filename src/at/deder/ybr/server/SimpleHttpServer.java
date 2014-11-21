package at.deder.ybr.server;

import at.deder.ybr.configuration.ServerManifest;
import at.deder.ybr.repository.PackageIndex;
import at.deder.ybr.repository.RepositoryEntry;
import com.esotericsoftware.yamlbeans.YamlException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.HttpClients;

/**
 * This implementation of the IServerGateway interface gives access to a remote
 * server of type 'simple'. This means that the remote server is serving only
 * and does not execute any actions on its own.
 *
 * @author lycis
 */
public class SimpleHttpServer implements IServerGateway {

    private static final String SCHEME = "http";
    private static final int DEFAULT_PORT = 80;

    private HttpClient serverConnection = null;
    private String hostname = "";
    private int port = DEFAULT_PORT;

    public SimpleHttpServer(String hostname) {
        this(hostname, DEFAULT_PORT);
    }

    public SimpleHttpServer(String hostname, int port) {
        serverConnection = HttpClients.createDefault();
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public ServerManifest getManifest() throws ProtocolViolationException {
        String manifest;
        try {
            manifest = getTextFromServer("manifest.yml");
        } catch (IOException ex) {
            throw new ProtocolViolationException("server communication failed",
                                                 ex);
        }
        ServerManifest sm;
        try {
            sm = ServerManifest.readYaml(new StringReader(manifest));
        } catch (YamlException ex) {
            throw new ProtocolViolationException("invalid server manifest", ex);
        }
        return sm;
    }

    /**
     * Allows you to overwrite the used HttpClient. Currently this is for
     * testing only but may be used in a factory later on.
     * 
     * @param client 
     */
    public void setHttpClient(HttpClient client) {
        serverConnection = client;
    }

    @Override
    public Banner getBanner() throws ProtocolViolationException{
        String text;
        try {
            text = getTextFromServer("index.html");
        } catch (IOException ex) {
            throw new ProtocolViolationException("server communication failed",
                                                 ex);
        }
        Banner banner = new Banner(text);
        return banner;
    }
    
    /**
     * Fetches a resource as text from the server by using a GET request.
     * @param path
     * @return 
     */
    private String getTextFromServer(String path) throws IOException, ProtocolViolationException {
        URI uri;
        try {
            uri = new URIBuilder()
                    .setScheme(SCHEME)
                    .setHost(hostname)
                    .setPort(port)
                    .setPath(path).build();
        } catch (URISyntaxException ex) {
            return null;
        }
        
        HttpGet request = new HttpGet(uri);
        HttpResponse response;
        try {
            response = serverConnection.execute(request);
        } catch (IOException ex) {
            throw ex;
        }
        
        // check response code
        if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new ProtocolViolationException("access to resource '"+path+"' not allowed ("
                    +response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase()+")");
        }
        
        InputStream contentStream;
        try {
            contentStream = response.getEntity().getContent();
        } catch (IOException | IllegalStateException ex) {
            return null;
        }
        
        StringWriter strWriter = new StringWriter();
        try {
            IOUtils.copy(contentStream, strWriter, Charset.forName("utf-8"));
        } catch (IOException ex) {
            return null;
        }
        
        return strWriter.toString();
    }

    @Override
    public Map<String, byte[]> getFilesOfPackage(String pkgName) throws ProtocolViolationException {
        String requestBasePath = packageNameToRequestPath(pkgName);
        
        // get index from server
        PackageIndex index;
        try{
            index = new PackageIndex(getTextFromServer(requestBasePath+"index"));
        } catch (IOException ex) {
            throw new ProtocolViolationException("package index not acessible", ex);
        }
        
        // select files according to index & download
        Map<String, byte[]> fileMap = new HashMap<>();
       for(String file: index.getIndex()) {
            byte[] data = null;
            try {
                data = getBinaryFromServer(requestBasePath+file);
            } catch (IOException ex) {
                throw new ProtocolViolationException("file '"+requestBasePath+file+"' not accessible", ex);
            }
            
            fileMap.put(file, data);
        }
        
        return fileMap;
    }

    @Override
    public RepositoryEntry getPackage(String name) throws ProtocolViolationException {
         if (name.startsWith(".")) {
            name = name.substring(1);
        }
        
        // get repository information from manifest
        ServerManifest manifest = getManifest();
        if(manifest == null)
            return null;

        return getPackageRecursion(manifest.getRepository(), name);
    }

     /**
     * Recursively walk through the repository tree and resolve a given path.
     *
     * @param root
     * @param name
     * @return null if not found
     */
    private RepositoryEntry getPackageRecursion(RepositoryEntry root, String name) {
        if(root == null) {
            return null;
        }
        
        List<String> path = Arrays.asList(name.split("\\."));
        if (path.size() < 1) {
            return null;
        }

        String target = path.get(0);
        RepositoryEntry nextEntry;
        try {
            nextEntry = (RepositoryEntry) root.getChildren().stream()
                    .filter(entry -> target.equals(((RepositoryEntry) entry).getName()))
                    .findFirst().get();
        } catch (NoSuchElementException e) {
            nextEntry = null;
        }

        if (!name.contains(".")) {
            return nextEntry;
        } else {
            String remainingPath = name.substring(name.indexOf(".") + 1);
            if (nextEntry != null && remainingPath.length() > 1) {
                return getPackageRecursion(nextEntry, remainingPath);
            }
        }
        return nextEntry;
    }
    
    /**
     * Translates package ID into the request path to query in the server.
     * @param pkgName
     * @return 
     */
    private String packageNameToRequestPath(String pkgName) {
        if(!pkgName.startsWith(".")) {
            pkgName = "." + pkgName;
        }
        
        return StringUtils.join(pkgName.split("\\."), "/") + "/";
    }

    /**
     * Fetches a resource as text from the server by using a GET request.
     * @param path
     * @return 
     */
    private byte[] getBinaryFromServer(String path) throws IOException, ProtocolViolationException {
        URI uri;
        try {
            uri = new URIBuilder()
                    .setScheme(SCHEME)
                    .setHost(hostname)
                    .setPort(port)
                    .setPath(path).build();
        } catch (URISyntaxException ex) {
            return null;
        }
        
        HttpGet request = new HttpGet(uri);
        HttpResponse response;
        try {
            response = serverConnection.execute(request);
        } catch (IOException ex) {
            throw ex;
        }
        
        // check response code
        if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
            throw new ProtocolViolationException("access to resource '"+path+"' not allowed ("
                    +response.getStatusLine().getStatusCode()+" - "+response.getStatusLine().getReasonPhrase()+")");
        }
        
        InputStream contentStream;
        try {
            contentStream = response.getEntity().getContent();
        } catch (IOException | IllegalStateException ex) {
            return null;
        }
        
        return IOUtils.toByteArray(contentStream);
    }
}
