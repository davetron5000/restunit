// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.http;

import java.io.*;
import java.net.*;
import java.util.*;

/** Implementation that uses the {@link java.net.HttpURLConnection} class.  For many reasons, you should probably 
 * use another implementation, however this one carries the least dependencies and other baggage.
 */
public class JavaHttp implements Http
{
    /** Performs an HTTP GET.
     * @param request the request describing the GET.
     * @return a response
     * @throws IOException if HttpURLConnection generated an IO Exception
     */
    public HttpResponse get(HttpRequest request)
        throws IOException
    {
        HttpURLConnection connection = getConnection(request);
        connection.setRequestMethod("GET");
        connection.connect();
        HttpResponse response = createResponse(connection);
        connection.disconnect();
        return response;
    }
    /** Performs an HTTP HEAD. 
     * @param request the request describing the HEAD.
     * @return a response
     * @throws IOException if HttpURLConnection generated an IO Exception
     */
    public HttpResponse head(HttpRequest request)
        throws IOException
    {
        HttpURLConnection connection = getConnection(request);
        connection.setRequestMethod("HEAD");
        connection.connect();
        HttpResponse response = createResponse(connection);
        connection.disconnect();
        return response;
    }

    /** Performs an HTTP PUT. 
     * @param request the request describing the PUT.
     * @return a response
     * @throws IOException if HttpURLConnection generated an IO Exception
     */
    public HttpResponse put(HttpRequest request)
        throws IOException
    {
        HttpURLConnection connection = getConnection(request);
        connection.setRequestMethod("PUT");
        connection.connect();
        setBody(request,connection);
        HttpResponse response = createResponse(connection);
        connection.disconnect();
        return response;
    }
    /** Performs an HTTP POST.
     * @param request the request describing the POST.
     * @return a response
     * @throws IOException if HttpURLConnection generated an IO Exception
     */
    public HttpResponse post(HttpRequest request)
        throws IOException
    {
        HttpURLConnection connection = getConnection(request);
        connection.setRequestMethod("POST");
        connection.connect();
        setBody(request,connection);
        HttpResponse response = createResponse(connection);
        connection.disconnect();
        return response;
    }
    /** Performs an HTTP DELETE. 
     * @param request the request describing the DELETE.
     * @return a response
     * @throws IOException if HttpURLConnection generated an IO Exception
     */
    public HttpResponse delete(HttpRequest request)
        throws IOException
    {
        HttpURLConnection connection = getConnection(request);
        connection.setRequestMethod("DELETE");
        connection.connect();
        HttpResponse response = createResponse(connection);
        connection.disconnect();
        return response;
    }

    private HttpURLConnection getConnection(HttpRequest request)
        throws IOException
    {
        HttpURLConnection connection = (HttpURLConnection)request.getURL().openConnection();
        for (String header: request.getHeaders().keySet())
        {
            connection.setRequestProperty(header,request.getHeaders().get(header));
        }
        return connection;
    }

    private byte []readBody(HttpURLConnection connection)
        throws IOException
    {
        InputStream is = connection.getInputStream();
        is = connection.getInputStream();
        if (is == null)
            is = connection.getErrorStream();
        if (is == null)
            return null;

        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int ch = is.read();
        while (ch != -1)
        {
            os.write(ch);
        }
        return os.toByteArray();
    }

    private String joinHeaderValues(List<String> values)
    {
        if (values == null) return null;
        StringBuilder b = new StringBuilder();
        for (String val: values)
        {
            b.append(val);
            b.append(",");
        }
        if (b.length() > 0)
            b.setLength(b.length() - 1);
        return b.toString();
    }

    private HttpResponse createResponse(HttpURLConnection connection)
        throws IOException
    {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(connection.getResponseCode());
        response.setBody(readBody(connection));
        for (String header: connection.getHeaderFields().keySet())
        {
            response.getHeaders().put(header,joinHeaderValues(connection.getHeaderFields().get(header)));
        }
        return response;
    }

    private void setBody(HttpRequest request, HttpURLConnection connection)
        throws IOException
    {
        if (request.getBody() == null)
            return;
        OutputStream os = connection.getOutputStream();
        for (byte b: request.getBody())
        {
            os.write(b);
        }
    }

}
