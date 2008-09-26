package com.gliffy.test.restunit.http;

import java.util.*;

import com.gliffy.restunit.http.*;
import com.gliffy.restunit.*;

import org.apache.commons.logging.*;

/** This is an implementation of HTTP that does no actual network connectivity, but takes a configured
 * data set and responds to rest calls
 */
public class MockHttp implements Http
{
    public static final String EXCEPTION_PATH = "/noone/may/go/here";

    private Log itsLogger = LogFactory.getLog(getClass());
    private RESTTree itsTree;
    /** Create a MockHttp service that exposes the given urls.
     * @param urls a map of relative url to content.  This content is returned by a GET request.
     * null values mean that a PUT (and DELETE) are possible.  POST does the same thing as PUT.
     */
    public MockHttp(Map<String,String> urls)
    {
        itsTree = new RESTTree();
        for (String url: urls.keySet())
        {
            final String value = urls.get(url);
            RESTTree node = ensureNode(url);
            if (value != null)
            {
                node.setHttp(new RESTTreeHttp() {
                    protected HttpResponse get(HttpRequest request,boolean clientDataOld) 
                    { 
                        itsLogger.debug("Get of " + request.getURL() + " is " + (clientDataOld ? "expired on client" : "up to date on client"));
                        if (clientDataOld)
                            return createStringGetResponse(value); 
                        else
                            return createHttpResponse(304);
                    }
                });
            }
            else
            {
                node.setHttp(new RESTTreeHttp() {
                    private byte value[];
                    protected HttpResponse get(HttpRequest request, boolean clientDataOld) 
                    { 
                        itsLogger.debug("Get of " + request.getURL() + " is " + (clientDataOld ? "expired on client" : "up to date on client"));
                        if (value == null)
                            return createHttpResponse(404);
                        else if (clientDataOld)
                            return createBytesGetResponse(value); 
                        else
                            return createHttpResponse(304);
                    }
                    public HttpResponse put(HttpRequest request) 
                    { 
                        value = request.getBody(); 
                        if (value == null)
                            return createHttpResponse(400); 
                        else
                            return createHttpResponse(201); 
                    }
                    public HttpResponse post(HttpRequest request) { return put(request); }
                    public HttpResponse delete(HttpRequest request) { value = null; return createHttpResponse(200); }
                });
            }
        }
    }

    public HttpResponse get(HttpRequest request)
        throws Exception
    {
        String path = request.getURL().getPath();
        if (path.equals(EXCEPTION_PATH))
            throw new RuntimeException("Tubez is clogged");
        RESTTree node = findNode(path);
        if (node == null)
        {
            return createHttpResponse(404);
        }
        else
        {
            return node.getHttp().get(request);
        }
    }
    public HttpResponse head(HttpRequest request)
        throws Exception
    {
        String path = request.getURL().getPath();
        RESTTree node = findNode(path);
        if (node == null)
        {
            return createHttpResponse(404);
        }
        else
        {
            return node.getHttp().head(request);
        }
    }
    public HttpResponse put(HttpRequest request)
        throws Exception
    {
        String path = request.getURL().getPath();
        RESTTree node = findNode(path);
        if (node == null)
        {
            return createHttpResponse(404);
        }
        else
        {
            return node.getHttp().put(request);
        }
    }

    public final HttpResponse post(HttpRequest request)
        throws Exception
    {
        String tunnel = request.getHeaders().get("X-HTTP-Method-Override");
        if ("PUT".equals(tunnel))
            return put(request);
        else if ("DELETE".equals(tunnel))
            return delete(request);
        else if ("HEAD".equals(tunnel))
            return head(request);
        else if ("GET".equals(tunnel))
            return get(request);
        else
            return doPost(request);
    }

    protected HttpResponse doPost(HttpRequest request)
        throws Exception
    {
        String path = request.getURL().getPath();
        RESTTree node = findNode(path);
        if (node == null)
        {
            return createHttpResponse(404);
        }
        else
        {
            return node.getHttp().post(request);
        }
    }

    public HttpResponse delete(HttpRequest request)
        throws Exception
    {
        String path = request.getURL().getPath();
        RESTTree node = findNode(path);
        if (node == null)
        {
            return createHttpResponse(404);
        }
        else
        {
            return node.getHttp().delete(request);
        }
    }

    protected HttpResponse createHttpResponse(int statusCode)
    {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(statusCode);
        return response;
    }

    protected RESTTree findNode(String path)
    {
        String parts[] = path.split("/");
        return findNode(itsTree,parts);
    }

    protected RESTTree findNode(RESTTree node, String parts[])
    {
        if (parts.length == 0)
            return node;
        String name = parts[0];
        for (RESTTree child: node.getChildren())
        {
            if (child.getName().equals(name))
                return findNode(child,shift(parts));
        }
        return null;
    }


    protected RESTTree ensureNode(String url)
    {
        String parts[] = url.split("/");
        return ensureNode(itsTree,parts);
    }

    protected RESTTree ensureNode(RESTTree node, String parts[])
    {
        if (parts.length == 0)
            return node;
        for (RESTTree child: node.getChildren())
        {
            if (child.getName().equals(parts[0]))
            {
                return ensureNode(child,shift(parts));
            }
        }
        // need a new node
        RESTTree newNode = new RESTTree();
        newNode.setName(parts[0]);
        node.addChild(newNode);
        return ensureNode(newNode,shift(parts));
    } 

    protected String[] shift(String arr[])
    {
        if (arr.length == 0)
            return arr;

        if (arr.length == 1)
        {
            return new String[0];
        }

        String returnMe[] = new String[arr.length-1];
        System.arraycopy(arr,1,returnMe,0,returnMe.length);
        return returnMe;
    }
}
