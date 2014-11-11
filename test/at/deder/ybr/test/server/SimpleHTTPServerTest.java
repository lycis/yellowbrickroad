/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.deder.ybr.test.server;

import at.deder.ybr.server.SimpleHTTPServer;
import at.deder.ybr.server.Banner;
import at.deder.ybr.repository.RepositoryEntry;
import at.deder.ybr.configuration.ServerManifest;
import at.deder.ybr.server.ProtocolViolationException;
import static com.googlecode.catchexception.CatchException.caughtException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import org.mockito.Matchers;
import static org.mockito.Mockito.mock;
import static com.googlecode.catchexception.apis.BDDCatchException.when;
import static com.googlecode.catchexception.apis.CatchExceptionAssertJ.then;

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
    public void testGetManifestDefault() throws IOException, ProtocolViolationException{
        
        ServerManifest expectedResult = new ServerManifest();
        expectedResult.initDefaults();
        StringWriter manifestWriter = new StringWriter();
        expectedResult.writeYaml(manifestWriter);
        
        given(mockHttpClient.execute(Matchers.any(HttpGet.class))).willReturn(mockHttpResponse);
        given(mockHttpResponse.getEntity()).willReturn(mockHttpEntity);
        given(mockHttpEntity.getContent())
                .willReturn(new ByteArrayInputStream(manifestWriter.toString().getBytes("utf-8")));
                
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
    public void testGetManifestWithRepository() throws IOException, ProtocolViolationException{
        
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
        
        given(mockHttpClient.execute(Matchers.any(HttpGet.class))).willReturn(mockHttpResponse);
        given(mockHttpResponse.getEntity()).willReturn(mockHttpEntity);
        given(mockHttpEntity.getContent())
                .willReturn(new ByteArrayInputStream(manifestWriter.toString().getBytes("utf-8")));
                
        SimpleHTTPServer instance = new SimpleHTTPServer("none");
        instance.setHttpClient(mockHttpClient);
        ServerManifest result = instance.getManifest();
        assertEquals(expectedResult, result);
    }
    
    @Test
    public void testBanner() throws IOException, ProtocolViolationException {
        Banner expectedBanner = new Banner("banner text");
        given(mockHttpClient.execute(Matchers.any(HttpGet.class))).willReturn(mockHttpResponse);
        given(mockHttpResponse.getEntity()).willReturn(mockHttpEntity);
        given(mockHttpEntity.getContent())
                .willReturn(new ByteArrayInputStream(expectedBanner.getText().getBytes("utf-8")));
        SimpleHTTPServer instance = new SimpleHTTPServer("none");
        instance.setHttpClient(mockHttpClient);
        Banner result = instance.getBanner();
        assertEquals(expectedBanner, result);
    }
    
    @Test
    public void testConnectionError() throws IOException, ProtocolViolationException {
        given(mockHttpClient.execute(Matchers.any(HttpGet.class))).willThrow(new IOException("Unknown host"));
        SimpleHTTPServer instance = new SimpleHTTPServer("none");
        when(instance).getBanner();
        then(caughtException()).isInstanceOf(ProtocolViolationException.class);
    }
}
