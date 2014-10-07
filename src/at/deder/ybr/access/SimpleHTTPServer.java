package at.deder.ybr.access;

import at.deder.ybr.beans.Banner;
import at.deder.ybr.beans.ServerManifest;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
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
public class SimpleHTTPServer implements IServerGateway {

    private static final String SCHEME = "http";
    private static final int DEFAULT_PORT = 80;

    private HttpClient serverConnection = null;
    private String hostname = "";
    private int port = DEFAULT_PORT;

    public SimpleHTTPServer(String hostname) {
        this(hostname, DEFAULT_PORT);
    }

    public SimpleHTTPServer(String hostname, int port) {
        serverConnection = HttpClients.createDefault();
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public ServerManifest getManifest() {
        String manifest = getTextFromServer("manifest.yml");
        ServerManifest sm = ServerManifest.readYaml(new StringReader(manifest));
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
    public Banner getBanner() {
        String text = getTextFromServer("index.html");
        Banner banner = new Banner(text);
        return banner;
    }
    
    /**
     * Fetches a resource as text from the server by using a GET request.
     * @param path
     * @return 
     */
    private String getTextFromServer(String path) {
        URI uri;
        try {
            uri = new URIBuilder()
                    .setScheme(SCHEME)
                    .setHost(hostname)
                    .setPort(port)
                    .setPath("path").build();
        } catch (URISyntaxException ex) {
            return null;
        }
        
        HttpGet request = new HttpGet(uri);
        HttpResponse response;
        try {
            response = serverConnection.execute(request);
        } catch (IOException ex) {
            return null;
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
}
