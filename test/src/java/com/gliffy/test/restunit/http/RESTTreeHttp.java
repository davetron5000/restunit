package com.gliffy.test.restunit.http;

import com.gliffy.restunit.http.*;
import com.gliffy.restunit.*;

/** A base implementation of Http for the RESTTree.  These
 * methods all return "method not allowed".
 * HEAD returns whatever GET returns, but omits the body
 */
public class RESTTreeHttp implements Http
{
    public HttpResponse get(HttpRequest request)
    {
        return createHttpResponse(405);
    }
    public HttpResponse head(HttpRequest request)
    {
        HttpResponse response = get(request);
        response.setBody(null);
        return response;
    }
    public HttpResponse put(HttpRequest request)
    {
        return createHttpResponse(405);
    }
    public HttpResponse post(HttpRequest request)
    {
        return createHttpResponse(405);
    }
    public HttpResponse delete(HttpRequest request)
    {
        return createHttpResponse(405);
    }

    protected HttpResponse createHttpResponse(int statusCode)
    {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(statusCode);
        return response;
    }

    protected HttpResponse createStringGetResponse(String string)
    {
        return createBytesGetResponse(string.getBytes());
    }

    protected HttpResponse createBytesGetResponse(byte bytes[])
    {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(200);
        response.setBody(bytes);
        if ( ( (bytes != null) && (bytes.length > 0) ) && bytes[0] == '<' )
            response.getHeaders().put("Content-Type","text/xml");
        else
            response.getHeaders().put("Content-Type","text/plain");
        return response;
    }
}
