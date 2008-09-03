package com.gliffy.restunit.http;

import java.util.*;

/** Encapsulates an HTTP response. */
public class HttpResponse
{
    private int itsStatusCode;
    private byte[] itsBody;
    private Map<String,String> itsHeaders;

    public int getStatusCode() 
    { 
        return itsStatusCode; 
    }
    public void setStatusCode(int i) 
    { 
        itsStatusCode = i; 
    }

    public byte[] getBody() 
    { 
        return itsBody; 
    }
    public void setBody(byte[] i) 
    { 
        itsBody = i; 
    }

    public Map<String,String> getHeaders() 
    { 
        return itsHeaders; 
    }
    public void setHeaders(Map<String,String> i) 
    { 
        itsHeaders = i; 
    }
}
