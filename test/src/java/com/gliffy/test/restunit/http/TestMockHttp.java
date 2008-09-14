package com.gliffy.test.restunit.http;

import java.net.*;
import java.util.*;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

import org.testng.*;
import org.testng.annotations.*;

import static org.easymock.classextension.EasyMock.*;

public class TestMockHttp
{
    @Test
    public void testSimpleGet()
        throws Exception
    {
        Map<String,String> expectedMimeTypes = new HashMap<String,String>();
        Map<String,String> service = new HashMap<String,String>();

        service.put("/foo","RESULT");
        expectedMimeTypes.put("/foo","text/plain");

        service.put("/foo/bar","<result />");
        expectedMimeTypes.put("/foo/bar","text/xml");

        MockHttp http = new MockHttp(service);

        HttpRequest request = new HttpRequest();
        request.setURL(new URL("http://www.google.com/foo"));
        HttpResponse response = http.get(request);

        assertGet(response,200,service.get("/foo"),expectedMimeTypes.get("/foo"));
        
        request = new HttpRequest();
        request.setURL(new URL("http://www.google.com/foo/bar"));
        response = http.get(request);
        
        assertGet(response,200,service.get("/foo/bar"),expectedMimeTypes.get("/foo/bar"));
    }

    private void assertGet(HttpResponse response, int sc, String body, String mimeType)
    {
        assert response.getStatusCode() == sc : "Got " + response.getStatusCode() + ", expected " + sc;
        if (body != null)
        {
            assert response.getBody() != null : "Expected a body, but got null";
            String got = new String(response.getBody());
            assert got.equals(body) : "Expected " + body + ", but got '" + got + "'";
        }
        if (mimeType != null)
        {
            assert response.getHeaders().containsKey("Content-Type") : "Expected Content-Type header";
            String gotType = response.getHeaders().get("Content-Type");
            assert mimeType.equals(gotType) : "Expected Content-Type header to be " + mimeType + ", but got " + gotType;
        }
    }

    @Test
    public void testPutDelete()
        throws Exception
    {
        testPutPostDelete(false,false);
    }

    @Test
    public void testPostDelete()
        throws Exception
    {
        testPutPostDelete(true,false);
    }

    @Test
    public void testPutDeleteTunnel()
        throws Exception
    {
        testPutPostDelete(false,true);
    }

    @Test
    public void testPostDeleteTunnel()
        throws Exception
    {
        testPutPostDelete(true,true);
    }

    private void testPutPostDelete(boolean post, boolean tunnel)
        throws Exception
    {
        Map<String,String> expectedMimeTypes = new HashMap<String,String>();
        Map<String,String> service = new HashMap<String,String>();

        service.put("/foo/bar/baz",null);

        MockHttp http = new MockHttp(service);

        HttpRequest request = new HttpRequest();
        request.setURL(new URL("http://www.google.com/foo/bar/baz"));
        String body = "This is the test body that I'm using";
        request.setBody(body.getBytes());
        HttpResponse response = null;
        if (post)
        {
            response = http.post(request);
        }
        else
        {
            if (tunnel)
            {
                request.getHeaders().put("X-HTTP-Method-Override","PUT");
                response = http.post(request);
            }
            else
            {
                response = http.put(request);
            }
        }

        assert response.getStatusCode() == 201 : "Got " + response.getStatusCode() + ", expected 201";
        assert response.getBody() == null : "Expected a null return body";

        request = new HttpRequest();
        request.setURL(new URL("http://www.google.com/foo/bar/baz"));
        response = http.get(request);

        assertGet(response,200,body,"text/plain");

        if (tunnel)
        {
            request.getHeaders().put("X-HTTP-Method-Override","DELETE");
            response = http.post(request);
        }
        else
        {
            response = http.delete(request);
        }

        assert response.getStatusCode() == 200 : "Got " + response.getStatusCode() + ", expected 200";

        request = new HttpRequest();
        request.setURL(new URL("http://www.google.com/foo/bar/baz"));
        response = http.get(request);

        assertGet(response,404,null,null);
    }
}
