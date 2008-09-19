// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** A call to a REST endpoint. This is essentially a test of an HTTP method to a URL */
public class RestCall implements Serializable, Cloneable
{
    private String itsURL;
    private String itsMethod;
    private Map<String,List<String>> itsParameters;
    private Map<String,String> itsHeaders;
    private String itsName;
    private String itsDescription;
    private RestCallResponse itsResponse;
    private SSLRequirement itsSSLRequirement;

    /** The URL being called.  This can be a complete URL or a partial one; it depends on your runtime configuration.  The recommendation is
     * that your calls only have URLs relative to the REST service root 
     * @return the URL being called.
     * */
    public String getURL() 
    {
        return itsURL; 
    }
    public void setURL(String i) 
    {
        itsURL = i; 
    }

    /** The HTTP method to use for the request. 
     * @return the HTTP method to use.
     * */
    public String getMethod() 
    {
        return itsMethod; 
    }
    public void setMethod(String i) 
    {
        itsMethod = i; 
    }

    /** The parameters to include with the request. 
     * @return the parameters to include in this call.
     * */
    public Map<String,List<String>> getParameters() 
    {
        if (itsParameters == null)
            itsParameters = new HashMap<String,List<String>>();
        return itsParameters; 
    }
    public void setParameters(Map<String,List<String>> i) 
    {
        itsParameters = i; 
    }

    /** Adds a parameter (with one or more values) to the list of parameters.
     * @param param the name of the parameter
     * @param values the list of values that this parameter should have
     */
    public void addParameter(String param, String... values)
    {
        List<String> paramValues = Arrays.asList(values);
        getParameters().put(param,paramValues);

    }

    /** The HTTP headers to include with the request.
     * @return a map of header names to values.
     * */
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

    /** Adds one header to the list of headers to send.
     * @param header the name of the header, without the colon.
     * @param value the value this header should have
     */
    public void addHeader(String header, String value)
    {
        getHeaders().put(header,value);
    }

    /** An arbitrary name for the call, to help you identify it in results.
     * @return the name of this call.
     * */
    public String getName() 
    {
        return itsName == null ? "" : itsName; 
    }
    public void setName(String i) 
    {
        itsName = i; 
    }

    /** A longer description of what the call does.
     * @return a string containing the longer description of this call.
     */
    public String getDescription() 
    {
        return itsDescription == null ? "" : itsDescription; 
    }
    public void setDescription(String i) 
    {
        itsDescription = i; 
    }

    public RestCallResponse getResponse() 
    {
        return itsResponse; 
    }
    public void setResponse(RestCallResponse i) 
    {
        itsResponse = i; 
    }

    /** The SSL requirements for this call.
     * @return the SSL requirements for this call.
     */
    public SSLRequirement getSSLRequirement() 
    {
        return itsSSLRequirement; 
    }
    public void setSSLRequirement(SSLRequirement i) 
    {
        itsSSLRequirement = i; 
    }

    /** Returns a deep-ish copy of this object.
     * All fields are copied as via {@link java.lang.Object#clone()}, however all internal collections are
     * also copied, so that you can freely change them in the returned object without affecting this one.
     * <b>Those</b> collections' contents will still be the same objects as this call, however.
     * @return a deeper-than-shallow copy of this object.
     */
    public Object clone()
    {
        try
        {
            RestCall clone = (RestCall)super.clone();
            if (getResponse() != null)
                clone.setResponse((RestCallResponse)(getResponse().clone()));
            clone.setHeaders(new HashMap<String,String>(getHeaders()));
            clone.setParameters(new HashMap<String,List<String>>(getParameters()));

            return clone;
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }

    /** Returns the name and description of this call, or {@link java.lang.Object Object's} implementation
     * if those weren't set.
     * @return a string description of this call, if possible.
     */
    public String toString()
    {
        if ( (getName().equals("") ) && (getDescription().equals("")) )
            return super.toString();
        else
            return getName() + " - " + getDescription();
    }
}
