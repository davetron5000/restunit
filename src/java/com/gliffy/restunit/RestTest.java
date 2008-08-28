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
    public Map<String,List<String>> getParameters() { return itsParameters; }
    public void setParameters(Map<String,List<String>> i) { itsParameters = i; }

    private Map<String,String> itsHeaders;
    /** The HTTP headers to include with the request */
    public Map<String,String> getHeaders() { return itsHeaders; }
    public void setHeaders(Map<String,String> i) { itsHeaders = i; }

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
    public Set<RestTest> getDependentTests() { return itsDependentTests; }
    public void setDependentTests(Set<RestTest> i) { itsDependentTests = i; }
}
