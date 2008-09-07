package com.gliffy.restunit;

import java.io.*;
import java.net.*;
import java.util.*;

import com.gliffy.restunit.http.*;

/** Handles the nuts and bolts of executing the rest test.
*/
public class Executor
{
    private Http itsHttp;
    private String itsBaseURL;
    private HttpRequestFactory itsRequestFactory;

    /** Creates an executor, deferring setting of the Http imlementation until later.
     */
    public Executor()
    {
        this(null);
        itsRequestFactory = new HttpRequestFactory("");
    }

    /** Creates an executor with the given HTTP implementation.
     * @param http an implementation of HTTP to use
     */
    public Executor(Http http)
    {
        itsHttp = http;
    }

    public Http getHttp() 
    { 
        return itsHttp; 
    }

    public void setHttp(Http i) 
    { 
        itsHttp = i; 
    }

    /** Returns the base URL for all requests, or the empty string if not configured.
     * @return a non-null string representing the base URL for all requests.
     */
    public String getBaseURL() 
    {
        return itsBaseURL == null ? "" : itsBaseURL; 
    }

    /** Sets the base URL against which test urls are appended.
     * This allows test urls to be relative to some externalized server, if needed
     * @param i the url
     */
    public void setBaseURL(String i) 
    {
        itsBaseURL = i; 
        itsRequestFactory = new HttpRequestFactory(itsBaseURL);
    }

    /** Executes a rest test.  No derived or dependent tests are executed.  If the http implementation has not been set, this will
     * throw an {@link java.lang.IllegalStateException}.
     * @param test the test to execute.
     * @return the results of the test.  This will always return, no exceptions are thrown from this method
     */
    public ExecutionResult execute(RestTest test)
    {
        if (getHttp() == null)
            throw new IllegalStateException();

        long testStartTime = System.currentTimeMillis();
        ExecutionResult ex = new ExecutionResult();
        ex.setTest(test);
        ex.setExecutionDate(new java.util.Date());

        try
        {
            HttpRequest request = itsRequestFactory.createRequest(test);
            HttpResponse response = null;

            if (test.getMethod().equalsIgnoreCase("get"))
                response = getHttp().get(request);
            else if (test.getMethod().equalsIgnoreCase("head"))
                response = getHttp().head(request);
            else if (test.getMethod().equalsIgnoreCase("put"))
                response = getHttp().put(request);
            else if (test.getMethod().equalsIgnoreCase("post"))
                response = getHttp().post(request);
            else if (test.getMethod().equalsIgnoreCase("delete"))
                response = getHttp().delete(request);
            else
                throw new IllegalArgumentException(test.getMethod() + " is not a supported HTTP method");

            populateResult(test,ex,response);
            return ex;
        }
        catch (MalformedURLException e)
        {
            ex.setResult(Result.EXCEPTION);
            ex.setThrowable(e);
        }
        ex.setExecutionTime(System.currentTimeMillis() - testStartTime);
        return ex;
    }

    private void populateResult(RestTest test, ExecutionResult result, HttpResponse response)
    {
        RestTestResponse expectedResponse = test.getResponse();
        if (expectedResponse.getStatusCode() == response.getStatusCode())
            result.setResult(Result.PASS);
        else
            result.setResult(Result.FAIL);
    }

}
