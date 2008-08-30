package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** A test of REST service */
public class RestTest implements Serializable, Cloneable
{
    private String itsURL;
    /** The URL being tested.  This can be a complete URL or a partial one; it depends on your runtime configuration.  The recommendation is
     * that your tests only have URLs relative to the REST service root */
    public String getURL() { return itsURL; }
    public void setURL(String i) { itsURL = i; }

    private String itsMethod;
    /** The HTTP method to use for the request */
    public String getMethod() { return itsMethod; }
    public void setMethod(String i) { itsMethod = i; }

    private Map<String,List<String>> itsParameters;
    /** The parameters to include with the request. */
    public Map<String,List<String>> getParameters() 
    { 
        if (itsParameters == null)
            itsParameters = new HashMap<String,List<String>>();
        return itsParameters; 
    }
    public void setParameters(Map<String,List<String>> i) { itsParameters = i; }

    public void addParameter(String param, String... values)
    {
        List<String> paramValues = Arrays.asList(values);
        getParameters().put(param,paramValues);
    }

    private Map<String,String> itsHeaders;
    /** The HTTP headers to include with the request */
    public Map<String,String> getHeaders() 
    { 
        if (itsHeaders == null)
            itsHeaders = new HashMap<String,String>();
        return itsHeaders; 
    }
    public void setHeaders(Map<String,String> i) { itsHeaders = i; }

    public void addHeader(String header, String value)
    {
        getHeaders().put(header,value);
    }

    private String itsName;
    /** An arbitrary name for the test, to help you identify it in results */
    public String getName() { return itsName; }
    public void setName(String i) { itsName = i; }

    private String itsDescription;
    /** A longer description of what the test does */
    public String getDescription() { return itsDescription; }
    public void setDescription(String i) { itsDescription = i; }

    private RestTestResponse itsResponse;
    /** The expected response */
    public RestTestResponse getResponse() { return itsResponse; }
    public void setResponse(RestTestResponse i) { itsResponse = i; }

    private SSLRequirement itSSLRequirement;
    /** The SSL requirements for this test */
    public SSLRequirement getSSLRequirement() { return itSSLRequirement; }
    public void setSSLRequirement(SSLRequirement i) { itSSLRequirement = i; }

    private Set<RestTest> itsDependentTests;
    /** Any tests that are dependent on this one */
    public Set<RestTest> getDependentTests() 
    { 
        if (itsDependentTests == null) 
            itsDependentTests = new HashSet<RestTest>(); 
        return itsDependentTests; 
    }
    public void setDependentTests(Set<RestTest> i) { itsDependentTests = i; }

    /** Returns a deep-ish copy of this object.
     * All fields are copied as via {@link java.lang.Object#clone()}, however all internal collections are
     * also copied, so that you can freely change them in the returned object without affecting this one.
     * <b>Those</b> collections' contents will still be the same objects as this test, however.
     */
    public Object clone()
    {
        try
        {
            RestTest clone = (RestTest)super.clone();
            clone.setDependentTests(new HashSet<RestTest>(getDependentTests()));
            if (getResponse() != null)
                clone.setResponse((RestTestResponse)(getResponse().clone()));
            clone.setHeaders(new HashMap<String,String>(getHeaders()));
            clone.setParameters(new HashMap<String,List<String>>(getParameters()));

            return clone;
        }
        catch (CloneNotSupportedException e)
        {
            throw new RuntimeException(e);
        }
    }
}
