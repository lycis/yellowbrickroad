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
public class HttpServerSimulator implements Answer {

    Map<String, Map<String, VirtualResource>> resourceMap = new HashMap<>();

    @Override
    public Object answer(InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        HttpResponse response = null;

        if (args.length != 1 && !(args[0] instanceof HttpGet)) {
            response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_INTERNAL_SERVER_ERROR, "wrong arguments");
            return response;
        }

        HttpGet request = (HttpGet) args[0];
        BasicHttpEntity entity = new BasicHttpEntity();

        String requestedPath = request.getURI().getPath();
        VirtualResource vr = getResource(requestedPath);
        if (vr == null) {
            response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.SC_NOT_FOUND, "not found");
        } else {
            entity.setContent(vr.content);
            entity.setContentType(vr.contentType);
            response = new BasicHttpResponse(HttpVersion.HTTP_1_1, vr.httpStatus, vr.httpStatusText);
            response.setEntity(entity);
        }

        return response;
    }

    private VirtualResource getResource(String path) {
        if (!path.endsWith("/")) {
            String resource = path.substring(path.lastIndexOf("/") + 1);
            path = path.substring(0, path.lastIndexOf("/"));

            Map<String, VirtualResource> pathContent = resourceMap.get(path);
            if (pathContent != null) {
                if (pathContent.containsKey(resource)) {
                    return pathContent.get(resource);
                }
            }
        }

        return null;
    }
    
    public void addResource(String path, String resource,ContentType contentType, String content) {
        addResource(path, resource, HttpStatus.SC_OK, "OK", contentType, content);
    }
    
    public void addResource(String path, String resource, int respCode, String respText, ContentType contentType, String content) {
        addResource(path, resource, respCode, respText, contentType, new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)));
    }

    public void addResource(String path, String resource, int respCode, String respText, ContentType contentType, ByteArrayInputStream content) {
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }

        Map<String, VirtualResource> resMap;
        if (resourceMap.containsKey(path)) {
            resMap = resourceMap.get(path);
        } else {
            resMap = new HashMap<>();
        }

        VirtualResource vr = new VirtualResource(contentType.toString(), content);
        vr.httpStatus = respCode;
        vr.httpStatusText = respText;
        resMap.put(resource, vr);

        if (!resourceMap.containsKey(path)) {
            resourceMap.put(path, resMap);
        }
    }

    private class VirtualResource {

        public ByteArrayInputStream content;
        public String contentType;
        public int httpStatus;
        public String httpStatusText;

        public VirtualResource(String cType, ByteArrayInputStream content) {
            this.content = content;
            this.contentType = cType;
        }
    }

}
