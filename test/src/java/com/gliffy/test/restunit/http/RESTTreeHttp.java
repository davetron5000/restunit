package com.gliffy.test.restunit.http;

import java.text.*;
import java.util.*;

import com.gliffy.restunit.http.*;
import com.gliffy.restunit.*;

/** A base implementation of Http for the RESTTree.  These
 * methods all return "method not allowed".
 * HEAD returns whatever GET returns, but omits the body
 */
public class RESTTreeHttp implements Http
{
    private static final SimpleDateFormat RFC822_DATE_FORMAT = new SimpleDateFormat("EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z", Locale.US);
    private final Date itsModDate;
    private final String itsETag;

    public RESTTreeHttp()
    {
        itsModDate = new java.util.Date(1221511285);
        itsETag = "foobarbazblahcruddo";
    }
    public HttpResponse get(HttpRequest request)
    {
        boolean notModified = false;
        if (request.getHeaders().get("If-Modified-Since") != null)
        {
            try
            {
                Date date = RFC822_DATE_FORMAT.parse(request.getHeaders().get("If-Modified-Since"));
                if (date.before(itsModDate))
                    notModified = false;
                else
                    notModified = true;
            }
            catch (ParseException e)
            {
                // ignore for now
            }
        }
        if (!notModified)
        {
            if (request.getHeaders().get("If-None-Match") != null)
            {
                notModified = request.getHeaders().get("If-None-Match").equals(itsETag);
            }
        }
        return get(request,notModified);
    }

    protected HttpResponse get(HttpRequest request, boolean notModified)
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

        if (bytes != null)
        {
            response.getHeaders().put("Content-Length",String.valueOf(bytes.length));
            response.getHeaders().put("Last-Modified",RFC822_DATE_FORMAT.format(itsModDate));
            response.getHeaders().put("ETag",itsETag);
        }
        return response;
    }
}
