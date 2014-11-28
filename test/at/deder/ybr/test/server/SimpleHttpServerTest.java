/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.deder.ybr.test.server;

import at.deder.ybr.server.SimpleHttpServer;
import at.deder.ybr.server.Banner;
import at.deder.ybr.repository.RepositoryEntry;
import at.deder.ybr.configuration.ServerManifest;
import at.deder.ybr.server.ProtocolViolationException;
import at.deder.ybr.test.cukes.HttpServerSimulator;
import at.deder.ybr.test.mocks.MockUtils;
import static com.googlecode.catchexception.CatchException.caughtException;
import static com.googlecode.catchexception.apis.BDDCatchException.when;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import java.util.Random;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicStatusLine;
import org.junit.Test;
import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import org.mockito.Matchers;
import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

/**
 *
 * @author ederda
 */
public class SimpleHttpServerTest {

    private HttpClient mockHttpClient = mock(HttpClient.class);
    private HttpGet mockHttpGet = mock(HttpGet.class);
    private HttpResponse mockHttpResponse = mock(HttpResponse.class);
    private HttpEntity mockHttpEntity = mock(HttpEntity.class);

    /**
     * A simple test to check if reading the manifest is working. It creates a
     * manifest that contains the default values and then replies that manifest
     * from a mocked and injected client.
     */
    @Test
    public void testGetManifestDefault() throws IOException, ProtocolViolationException {

        ServerManifest expectedResult = new ServerManifest();
        expectedResult.initDefaults();
        StringWriter manifestWriter = new StringWriter();
        expectedResult.writeYaml(manifestWriter);

        given(mockHttpClient.execute(Matchers.any(HttpGet.class))).willReturn(mockHttpResponse);
        given(mockHttpResponse.getEntity()).willReturn(mockHttpEntity);
        given(mockHttpResponse.getStatusLine()).willReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        given(mockHttpEntity.getContent())
                .willReturn(new ByteArrayInputStream(manifestWriter.toString().getBytes("utf-8")));

        SimpleHttpServer instance = new SimpleHttpServer("none");
        instance.setHttpClient(mockHttpClient);
        ServerManifest result = instance.getManifest();
        assertEquals(expectedResult, result);
    }

    /**
     * Checks if getting a manifest with attached repository entries is working.
     *
     * @throws IOException
     */
    @Test
    public void testGetManifestWithRepository() throws IOException, ProtocolViolationException {

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
        given(mockHttpResponse.getStatusLine()).willReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        given(mockHttpEntity.getContent())
                .willReturn(new ByteArrayInputStream(manifestWriter.toString().getBytes("utf-8")));

        SimpleHttpServer instance = new SimpleHttpServer("none");
        instance.setHttpClient(mockHttpClient);
        ServerManifest result = instance.getManifest();
        assertEquals(expectedResult, result);
    }

    @Test
    public void testBanner() throws IOException, ProtocolViolationException {
        Banner expectedBanner = new Banner("banner text");
        given(mockHttpClient.execute(Matchers.any(HttpGet.class))).willReturn(mockHttpResponse);
        given(mockHttpResponse.getEntity()).willReturn(mockHttpEntity);
        given(mockHttpResponse.getStatusLine()).willReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        given(mockHttpEntity.getContent())
                .willReturn(new ByteArrayInputStream(expectedBanner.getText().getBytes("utf-8")));
        SimpleHttpServer instance = new SimpleHttpServer("none");
        instance.setHttpClient(mockHttpClient);
        Banner result = instance.getBanner();
        assertEquals(expectedBanner, result);
    }

    @Test
    public void testConnectionError() throws IOException, ProtocolViolationException {
        given(mockHttpClient.execute(Matchers.any(HttpGet.class))).willThrow(new IOException("Unknown host"));
        SimpleHttpServer instance = new SimpleHttpServer("none");
        when(instance).getBanner();
        //then(caughtException()).isInstanceOf(ProtocolViolationException.class);
        then((Throwable) caughtException()).isInstanceOf(ProtocolViolationException.class);
    }

    @Test
    public void testGetPackageTopLevel() throws ProtocolViolationException, IOException {

        // given
        ServerManifest dummyManifest = MockUtils.getMockManifest();

        given(mockHttpClient.execute(Matchers.any(HttpGet.class))).willReturn(mockHttpResponse);
        given(mockHttpResponse.getEntity()).willReturn(mockHttpEntity);
        given(mockHttpResponse.getStatusLine()).willReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        given(mockHttpEntity.getContent())
                .willReturn(new ByteArrayInputStream(dummyManifest.toString().getBytes("utf-8")));
        SimpleHttpServer instance = new SimpleHttpServer("none");
        instance.setHttpClient(mockHttpClient);

        // when
        RepositoryEntry result = instance.getPackage("com");

        // then
        RepositoryEntry root = dummyManifest.getRepository();
        RepositoryEntry expResult = root.getChildByName("com");
        then(result).isEqualTo(expResult);
    }

    @Test
    public void testGetPackageDeep() throws ProtocolViolationException, IOException {
        // given
        ServerManifest dummyManifest = MockUtils.getMockManifest();

        given(mockHttpClient.execute(Matchers.any(HttpGet.class))).willReturn(mockHttpResponse);
        given(mockHttpResponse.getEntity()).willReturn(mockHttpEntity);
        given(mockHttpResponse.getStatusLine()).willReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        given(mockHttpEntity.getContent())
                .willReturn(new ByteArrayInputStream(dummyManifest.toString().getBytes("utf-8")));
        SimpleHttpServer instance = new SimpleHttpServer("none");
        instance.setHttpClient(mockHttpClient);

        // when
        RepositoryEntry result = instance.getPackage(".com.java.io.file");

        // then
        RepositoryEntry root = dummyManifest.getRepository();
        RepositoryEntry expResult = root.getChildByName("com")
                .getChildByName("java")
                .getChildByName("io")
                .getChildByName("file");
        then(result).isEqualTo(expResult);
    }

    @Test
    public void testGetPackageNoLeadingDot() throws ProtocolViolationException, IOException {
        // given
        ServerManifest dummyManifest = MockUtils.getMockManifest();

        given(mockHttpClient.execute(Matchers.any(HttpGet.class))).willReturn(mockHttpResponse);
        given(mockHttpResponse.getEntity()).willReturn(mockHttpEntity);
        given(mockHttpResponse.getStatusLine()).willReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        given(mockHttpEntity.getContent())
                .willReturn(new ByteArrayInputStream(dummyManifest.toString().getBytes("utf-8")));
        SimpleHttpServer instance = new SimpleHttpServer("none");
        instance.setHttpClient(mockHttpClient);

        // when
        RepositoryEntry result = instance.getPackage("com.java.io.file");

        // then
        RepositoryEntry root = dummyManifest.getRepository();
        RepositoryEntry expResult = root.getChildByName("com")
                .getChildByName("java")
                .getChildByName("io")
                .getChildByName("file");
        then(result).isEqualTo(expResult);
    }

    @Test
    public void testGetPackageNotExisting() throws ProtocolViolationException, IOException {
        // given
        ServerManifest dummyManifest = MockUtils.getMockManifest();

        given(mockHttpClient.execute(Matchers.any(HttpGet.class))).willReturn(mockHttpResponse);
        given(mockHttpResponse.getEntity()).willReturn(mockHttpEntity);
        given(mockHttpResponse.getStatusLine()).willReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        given(mockHttpEntity.getContent())
                .willReturn(new ByteArrayInputStream(dummyManifest.toString().getBytes("utf-8")));
        SimpleHttpServer instance = new SimpleHttpServer("none");
        instance.setHttpClient(mockHttpClient);

        // when
        RepositoryEntry result = instance.getPackage("com.doesnotexist");

        // then
        then(result).isEqualTo(null);
    }

    /**
     * check access to a file resource
     *
     * @throws ProtocolViolationException
     * @throws IOException
     */
    @Test
    public void test_get_package_file_simple_content() throws ProtocolViolationException, IOException {
        //given        
        String testDatContent = "content of test.dat";
        SimpleHttpServer instance = spy(new SimpleHttpServer("none"));
        willReturn(MockUtils.getMockManifest()).given(instance).getManifest();
        // return different response depending on called path
        HttpServerSimulator simulatedServer = new HttpServerSimulator();
        simulatedServer.addResource("/org/junit/", "index", ContentType.TEXT_PLAIN, "test.dat");
        simulatedServer.addResource("/org/junit/", "test.dat", ContentType.TEXT_PLAIN, testDatContent);
        given(mockHttpClient.execute(Matchers.any(HttpGet.class))).willAnswer(simulatedServer);
        instance.setHttpClient(mockHttpClient);

        // when
        Map<String, byte[]> files = instance.getFilesOfPackage(".org.junit");

        // then
        then(files.get("test.dat")).isEqualTo(IOUtils.toByteArray(new StringReader(testDatContent)));
    }

    /**
     * access to requested file returns not status code 200 OK but 403
     * Forbidden.
     *
     * @throws ProtocolViolationException
     * @throws IOException
     */
    @Test
    public void test_get_package_file_403_forbidden_data() throws ProtocolViolationException, IOException {
        //given        
        SimpleHttpServer instance = new SimpleHttpServer("none");

        // return different response depending on called path
        HttpServerSimulator simulatedServer = new HttpServerSimulator();
        simulatedServer.addResource("/org/junit/", "index", ContentType.TEXT_PLAIN, "test.dat");
        simulatedServer.addResource("/org/junit/", "test.dat", HttpStatus.SC_FORBIDDEN, "Forbidden", ContentType.TEXT_PLAIN, "");
        given(mockHttpClient.execute(Matchers.any(HttpGet.class))).willAnswer(simulatedServer);
        instance.setHttpClient(mockHttpClient);

        // when
        when(instance).getFilesOfPackage(".org.junit");

        // then
        then((Throwable) caughtException()).isInstanceOf(ProtocolViolationException.class);
        then((Throwable) caughtException()).hasMessage("access to resource '/org/junit/test.dat' not allowed (403 - Forbidden)");
    }

    /**
     * check access to files of a package with more than one file
     *
     * @throws ProtocolViolationException
     * @throws IOException
     */
    @Test
    public void test_get_package_file_multiple() throws ProtocolViolationException, IOException {
        //given        
        String contentOneDat = "content of one.dat";
        String contentTwoDat = "content of one.dat";
        byte[] contentThreeDat = new byte[20];
        new Random().nextBytes(contentThreeDat);
        SimpleHttpServer instance = spy(new SimpleHttpServer("none"));
        willReturn(MockUtils.getMockManifest()).given(instance).getManifest();
        // return different response depending on called path
        HttpServerSimulator simulatedServer = new HttpServerSimulator();
        simulatedServer.addResource("/org/junit", "index", ContentType.TEXT_PLAIN, "one.dat\ntwo.dat\nthree.dat");
        simulatedServer.addResource("/org/junit", "one.dat", ContentType.TEXT_PLAIN, contentOneDat);
        simulatedServer.addResource("/org/junit", "two.dat", ContentType.TEXT_PLAIN, contentTwoDat);
        simulatedServer.addResource("/org/junit", "three.dat", HttpStatus.SC_OK, "OK", ContentType.TEXT_HTML, new ByteArrayInputStream(contentThreeDat));
        given(mockHttpClient.execute(Matchers.any(HttpGet.class))).willAnswer(simulatedServer);
        instance.setHttpClient(mockHttpClient);

        // when
        Map<String, byte[]> files = instance.getFilesOfPackage(".org.junit");

        // then
        then(files.get("one.dat")).isEqualTo(IOUtils.toByteArray(new StringReader(contentOneDat)));
        then(files.get("two.dat")).isEqualTo(IOUtils.toByteArray(new StringReader(contentTwoDat)));
        then(files.get("three.dat")).isEqualTo(contentThreeDat);
    }
}
