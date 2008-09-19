// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.http;

import java.io.*;
import java.net.*;
import java.util.*;

import com.gliffy.restunit.*;

/** This class produces {@link HttpRequest} objects based upon {@link com.gliffy.restunit.RestCall} objects.
 * This is the bridge between a human-usable call and the mechanics of HTTP.
 */
public class HttpRequestFactory
{
    private String itsBaseURL;

    /** Create an HttpRequestFactory with the given baseURL.
     * @param baseURL the base of all URLs that will be created.  If null, the calls
     * will be assumed to have complete URLs in them
     */
    public HttpRequestFactory(String baseURL)
    {
        itsBaseURL = baseURL == null ? "" : baseURL;
    }

    /** Creates the HttpRequest using the baseURL and the provided call.
     * @param call the call to use to create an HttpRequest
     * @return an HttpRequest
     * @throws MalformedURLException if the baseURL + the call URL do not create a valid URL.  This represents a problem with your configuration and/or calls.
     */
    public HttpRequest createRequest(RestCall call)
        throws MalformedURLException
    {
        HttpRequest request = new HttpRequest();
        String url = createURL(call);
        request.setURL(new URL(url));
        request.setHeaders(call.getHeaders());
        if (call instanceof BodyCall)
        {
            BodyCall bodyTest = (BodyCall)call;
            if (bodyTest.getBody() != null)
            {
                request.getHeaders().put("Content-Type",bodyTest.getContentType());
                request.getHeaders().put("Content-Length",String.valueOf(bodyTest.getBody().length));
                request.setBody(bodyTest.getBody());
            }
        }
        else
        {
            request.setBody(null);
        }
        return request;
    }

    private String createURL(RestCall call)
    {
        String queryString = createQueryString(call.getParameters());
        if (queryString.length() > 0)
            return itsBaseURL + call.getURL() + "?" + queryString;
        else
            return itsBaseURL + call.getURL();
    }

    private String createQueryString(Map<String,List<String>> params)
    {
        if (params == null)
            return "";
        StringBuilder b = new StringBuilder("");
        for (String param: params.keySet())
        {
            for (String value: params.get(param))
            {
                b.append(param);
                b.append("=");
                try
                {
                    b.append(URLEncoder.encode(value,"UTF-8"));
                }
                catch (UnsupportedEncodingException e)
                {
                    // UTF-8 is supported; this is stupid to have to catch, thanks for
                    // deprecating the easy method 
                }
                b.append("&");
            }
        }
        if (b.length() > 0)
            b.setLength(b.length() - 1);

        return b.toString();
    }
}
