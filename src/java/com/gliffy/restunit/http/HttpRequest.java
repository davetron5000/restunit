package com.gliffy.restunit.http;

import java.net.*;
import java.util.*;

/** Encapsulates an HTTP request. */
public class HttpRequest
{
    private URL itsURL;
    private Map<String,String> itsHeaders;
    private byte[] itsBody;

    public URL getURL() 
    {
        return itsURL; 
    }
    public void setURL(URL i) 
    {
        itsURL = i; 
    }
    public Map<String,String> getHeaders() 
    {
        return itsHeaders; 
    }
    public void setHeaders(Map<String,String> i) 
    {
        itsHeaders = i; 
    }
    public byte[] getBody() 
    {
        return itsBody; 
    }
    public void setBody(byte[] i) 
    {
        itsBody = i; 
    }

}