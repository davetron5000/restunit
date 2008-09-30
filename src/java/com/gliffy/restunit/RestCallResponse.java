// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** An expected response. */
public class RestCallResponse implements Serializable, Cloneable
{
    /** HTTP Header for Last-Modified. */
    public static final String LAST_MODIFIED_HEADER = "Last-Modified";
    /** HTTP Header for If-Modified-Since. */
    public static final String IF_MODIFIED_SINCE_HEADER = "If-Modified-Since";
    /** HTTP Header for ETag. */
    public static final String ETAG_HEADER = "ETag";
    /** HTTP Header for If-None-Match. */
    public static final String IF_NONE_MATCH_HEADER = "If-None-Match";
    /** HTTP Header for Content-Type. */
    public static final String CONTENT_TYPE_HEADER = "Content-Type";

    /** Status code for 304/Not Modified. */
    public static final int NOT_MODIFIED_STATUS = 304;

    private int itsStatusCode;
    private Set<String> itsRequiredHeaders;
    private Set<String> itsBannedHeaders;
    private Map<String,String> itsHeaders;

    /** The expected status code.
     * @return the status code that should be returned.
     */
    public int getStatusCode() 
    {
        return itsStatusCode; 
    }
    public void setStatusCode(int i) 
    {
        itsStatusCode = i; 
    }

    /** Entries here indicate that the header <b>must</b> be part of the response.  If a header is in the Map returned
     * by getHeaders, you don't need to duplicate it here.  Only use this if you just care that the header was included
     * @return a set of headers that must be included in the response for this call to be considered successful.
     */
    public Set<String> getRequiredHeaders() 
    {
        if (itsRequiredHeaders == null)
            itsRequiredHeaders = new HashSet<String>();
        return itsRequiredHeaders; 

    }
    public void setRequiredHeaders(Set<String> i) 
    {
        itsRequiredHeaders = i; 
    }

    /** Entries here indicate that the header <b>must not</b> be part of the response.
     * @return a set of headers that must not be part of the response for this call to be considered successful. */
    public Set<String> getBannedHeaders() 
    {
        if (itsBannedHeaders == null)
            itsBannedHeaders = new HashSet<String>();
        return itsBannedHeaders; 

    }
    public void setBannedHeaders(Set<String> i) 
    {
        itsBannedHeaders = i; 
    }

    /** Entries here indicate that a header is required and that it should have the given value in order for the call to be considered successful.
     * @return a map of headers and their expected values required for this call to be considered successful
     */
    public Map<String,String> getHeaders() 
    {
        if (itsHeaders == null)
            itsHeaders = new HashMap<String,String>();
        return itsHeaders; 

    }

    public void setHeaders(Map<String,String> i) 
    {
        itsHeaders = i; 
    }

    /** A deep-ish copy of this object.  All collections are passed to new colleciton objects in the new object, so they will be backed by the same references, however
     * they can be independently changed
     * @return a clone of this object.
     */
    public Object clone()
    {
        try
        {
            RestCallResponse clone = (RestCallResponse)super.clone();
            clone.setHeaders(new HashMap<String,String>(getHeaders()));
            clone.setBannedHeaders(new HashSet<String>(getBannedHeaders()));
            clone.setRequiredHeaders(new HashSet<String>(getRequiredHeaders()));
            return clone;
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
