package com.gliffy.restunit.http;

import java.io.*;
import java.net.*;
import java.util.*;

import com.gliffy.restunit.*;

/** This class produces {@link HttpRequest} objects based upon {@link com.gliffy.restunit.RestTest} objects.
 * This is the bridge between a human-usable test and the mechanics of HTTP.
 */
public class HttpRequestFactory
{
    private String itsBaseURL;

    /** Create an HttpRequestFactory with the given baseURL.
     * @param baseURL the base of all URLs that will be created.  If null, the tests
     * will be assumed to have complete URLs in them
     */
    public HttpRequestFactory(String baseURL)
    {
        itsBaseURL = baseURL == null ? "" : baseURL;
    }

    /** Creates the HttpRequest using the baseURL and the provided test.
     * @param test the test to use to create an HttpRequest
     * @return an HttpRequest
     * @throws MalformedURLException if the baseURL + the test URL do not create a valid URL.  This represents a problem with your configuration and/or tests.
     */
    public HttpRequest createRequest(RestTest test)
        throws MalformedURLException
    {
        HttpRequest request = new HttpRequest();
        String url = createURL(test);
        request.setURL(new URL(url));
        request.setHeaders(test.getHeaders());
        request.setBody(null);
        return request;
    }

    private String createURL(RestTest test)
    {
        String queryString = createQueryString(test.getParameters());
        if (queryString.length() > 0)
            return itsBaseURL + test.getURL() + "?" + queryString;
        else
            return itsBaseURL + test.getURL();
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
