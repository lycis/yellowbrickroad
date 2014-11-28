/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package at.deder.ybr.test.cukes;

import at.deder.ybr.test.mocks.MockUtils;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.message.BasicHttpResponse;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

/**
 *
 * @author ederda
 */
public class SimpleServerResponseAnswer implements Answer {

    Map<String, Map<String, VirtualResource>> resourceMap = new HashMap<>();

    @Override
    public Object answer(InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK");

        if (args.length != 1 && !(args[0] instanceof HttpGet)) {
            response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_INTERNAL_SERVER_ERROR, "wrong arguments");
            return response;
        }

        HttpGet request = (HttpGet) args[0];
        BasicHttpEntity entity = new BasicHttpEntity();

        // TODO return content for each package (index + files)
        String requestedPath = request.getURI().getPath();
        VirtualResource vr = getResource(requestedPath);
        if (vr == null) {
            response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_NOT_FOUND, "not found");
        } else {
            entity.setContent(vr.getContentStream());
            entity.setContentType(vr.contentType);
        }

        response.setEntity(entity);
        return response;
    }

    private VirtualResource getResource(String path) {
        if (!path.endsWith("/")) {
            String resource = path.substring(path.lastIndexOf("/"));
            path = path.substring(0, path.lastIndexOf("/"));

            Map<String, VirtualResource> pathContent = resourceMap.get(path);
            if (path != null) {
                if (pathContent.containsKey(resource)) {
                    return pathContent.get(resource);
                }
            }
        }

        return null;
    }

    public void configureDefaultRepository() {
        // root level
        addResource("/", "manifest.yml", ContentType.TEXT_PLAIN, MockUtils.getMockManifest().toString());
        
        // TODO complete
    }
    
    public void addResource(String path, String resource, ContentType contentType, String content) {
        Map<String, VirtualResource> resMap = new HashMap<>();
        VirtualResource vr = new VirtualResource(contentType.toString(), MockUtils.getMockManifest().toString());
        resMap.put(resource, vr);
        resourceMap.put(path, resMap);
    }

    private class VirtualResource {

        public String content;
        public String contentType;
        
        public VirtualResource(String cType, String content) {
            this.content = content;
            this.contentType = cType;
        }

        public ByteArrayInputStream getContentStream() {
            return new ByteArrayInputStream(content.toString().getBytes(StandardCharsets.UTF_8));
        }
    }

}
