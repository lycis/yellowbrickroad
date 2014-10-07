/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.deder.ybr.test;

import at.deder.ybr.access.SimpleHTTPServer;
import at.deder.ybr.beans.Banner;
import at.deder.ybr.beans.RepositoryEntry;
import at.deder.ybr.beans.ServerManifest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;
import static org.junit.Assert.*;
import org.mockito.Matchers;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author ederda
 */
public class SimpleHTTPServerTest {
    private HttpClient mockHttpClient = mock(HttpClient.class);
    private HttpGet mockHttpGet       = mock (HttpGet.class);
    private HttpResponse mockHttpResponse = mock(HttpResponse.class);
    private HttpEntity mockHttpEntity = mock(HttpEntity.class);
    
   /**
     * A simple test to check if reading the manifest is working. It creates 
     * a manifest that contains the default values and then replies that manifest
     * from a mocked and injected client.
     */
    @Test
    public void testGetManifestDefault() throws IOException{
        
        ServerManifest expectedResult = new ServerManifest();
        expectedResult.initDefaults();
        StringWriter manifestWriter = new StringWriter();
        expectedResult.writeYaml(manifestWriter);
        
        when(mockHttpClient.execute(Matchers.any(HttpGet.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpEntity.getContent())
                .thenReturn(new ByteArrayInputStream(manifestWriter.toString().getBytes("utf-8")));
                
        SimpleHTTPServer instance = new SimpleHTTPServer("none");
        instance.setHttpClient(mockHttpClient);
        ServerManifest result = instance.getManifest();
        assertEquals(expectedResult, result);
    }
    
    /**
     * Checks if getting a manifest with attached repository entries is working.
     * @throws IOException 
     */
    @Test
    public void testGetManifestWithRepository() throws IOException{
        
        ServerManifest expectedResult = new ServerManifest();
        expectedResult.initDefaults();
        RepositoryEntry rootEntry = new RepositoryEntry();
        rootEntry.setName("repository");
        RepositoryEntry comEntry = new RepositoryEntry();
        comEntry.setName("com");
        comEntry.setDescription("commercial libraries");
        RepositoryEntry orgEntry = new RepositoryEntry();
        comEntry.setName("org");
        comEntry.setDescription("open source libraries");
        StringWriter manifestWriter = new StringWriter();
        expectedResult.writeYaml(manifestWriter);
        
        when(mockHttpClient.execute(Matchers.any(HttpGet.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpEntity.getContent())
                .thenReturn(new ByteArrayInputStream(manifestWriter.toString().getBytes("utf-8")));
                
        SimpleHTTPServer instance = new SimpleHTTPServer("none");
        instance.setHttpClient(mockHttpClient);
        ServerManifest result = instance.getManifest();
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void testBanner() throws IOException {
        Banner expectedBanner = new Banner("banner text");
        when(mockHttpClient.execute(Matchers.any(HttpGet.class))).thenReturn(mockHttpResponse);
        when(mockHttpResponse.getEntity()).thenReturn(mockHttpEntity);
        when(mockHttpEntity.getContent())
                .thenReturn(new ByteArrayInputStream(expectedBanner.getText().getBytes("utf-8")));
        SimpleHTTPServer instance = new SimpleHTTPServer("none");
        instance.setHttpClient(mockHttpClient);
        Banner result = instance.getBanner();
        assertEquals(expectedBanner, result);
    }
}
