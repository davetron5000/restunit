// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

import java.io.*;
import java.net.*;
import java.util.*;

import com.gliffy.restunit.comparator.*;
import com.gliffy.restunit.http.*;

import org.apache.commons.logging.*;

/** Handles the nuts and bolts of executing one call.  This can be re-used, even if you wish to change HTTP implementations or comparators.
 * This class's behavior can be configured in the following ways:
 * <ul>
 * <li>{@link com.gliffy.restunit.http.Http} - this implements the basic HTTP protocol and allows you to use any implementation
 * you wish.  You must set something via {@link #setHttp(com.gliffy.restunit.http.Http)}.</li>
 * <li>{@link com.gliffy.restunit.comparator.ResultComparator} - this performs the comparison of results.  By default, this class uses the {@link
 * com.gliffy.restunit.comparator.StrictMatchComparator}.  You may override this if custom comparisons are required.
 * <li>BaseURL - you may optionally provide a base URL against which all requests are run.  This is handy if you don't want your calls to have full URLs in them</li>
 * </ul>
*/
public class Executor
{
    private Http itsHttp;
    private String itsBaseURL;
    private ResultComparator itsComparator;

    private HttpRequestFactory itsRequestFactory;
    private Log itsLogger = LogFactory.getLog(getClass());

    /** Creates an executor, deferring setting of the Http imlementation until later.
     */
    public Executor()
    {
        itsRequestFactory = new HttpRequestFactory("");
        setComparator(new StrictMatchComparator());
    }

    public Http getHttp() 
    { 
        return itsHttp; 
    }

    public void setHttp(Http i) 
    { 
        itsHttp = i; 
    }

    public ResultComparator getComparator() 
    {
        return itsComparator; 
    }

    public void setComparator(ResultComparator i) 
    {
        itsComparator = i; 
    }


    /** Returns the base URL for all requests, or the empty string if not configured.
     * @return a non-null string representing the base URL for all requests.
     */
    public String getBaseURL() 
    {
        return itsBaseURL == null ? "" : itsBaseURL; 
    }

    /** Sets the base URL against which call urls are appended.
     * This allows call urls to be relative to some externalized server, if needed
     * @param i the url
     */
    public void setBaseURL(String i) 
    {
        itsBaseURL = i; 
        itsRequestFactory = new HttpRequestFactory(itsBaseURL);
        itsLogger.debug("New HttpRequestFactory created with base url " + itsBaseURL);
    }

    /** Executes a rest call.  No derived calls are executed.  If the http implementation has not been set, this will
     * throw an {@link java.lang.IllegalStateException}.
     * @param call the call to execute.
     * @return the results of the call.  This will always return, no exceptions are thrown from this method
     */
    public RestCallResult execute(RestCall call)
    {
        if (getHttp() == null)
            throw new IllegalStateException("No HTTP implementation configured");

        itsLogger.debug("Starting call " + call.getName());
        long callStartTime = System.currentTimeMillis();
        RestCallResult executionResult = initializeResult(call);

        try
        {
            HttpRequest request = itsRequestFactory.createRequest(call);
            itsLogger.debug("Request created for " + request.getURL().toString());
            HttpResponse response = null;

            if (call.getMethod().equalsIgnoreCase("get"))
                response = getHttp().get(request);
            else if (call.getMethod().equalsIgnoreCase("head"))
                response = getHttp().head(request);
            else if (call.getMethod().equalsIgnoreCase("put"))
                response = getHttp().put(request);
            else if (call.getMethod().equalsIgnoreCase("post"))
                response = getHttp().post(request);
            else if (call.getMethod().equalsIgnoreCase("delete"))
                response = getHttp().delete(request);
            else
                throw new IllegalArgumentException(call.getMethod() + " is not a supported HTTP method");

            itsLogger.debug("Received response");

            populateResult(call,executionResult,response);
            return executionResult;
        }
        catch (MalformedURLException e)
        {
            executionResult.setResult(Result.EXCEPTION);
            executionResult.setThrowable(e);
        }
        executionResult.setExecutionTime(System.currentTimeMillis() - callStartTime);
        itsLogger.debug("Test execution complete");
        return executionResult;
    }

    /** Skips the call, generating a result that indicates that.  This is preferable to creating
     * RestCallResults yourself as it maintains a consistent means of vending this objects.
     * @param call the call to skip
     * @return a RestCallResult that indicates that this call was skipped.
     */
    public RestCallResult skip(RestCall call)
    {
        RestCallResult result = initializeResult(call);
        result.setResult(Result.SKIP);
        return result;
    }

    private RestCallResult initializeResult(RestCall call)
    {
        RestCallResult executionResult = new RestCallResult();
        executionResult.setCall(call);
        executionResult.setExecutionDate(new java.util.Date());
        return executionResult;
    }

    private void populateResult(RestCall call, RestCallResult result, HttpResponse response)
    {
        RestCallResponse expectedResponse = call.getResponse();
        if (expectedResponse == null)
        {
            throw new IllegalArgumentException("Test did not have an expected response");
        }

        try
        {
            result.setResponse(response);
            ComparisonResult comparisonResult = getComparator().compare(response,expectedResponse);
            if (comparisonResult.getMatches())
            {
                result.setResult(Result.PASS);
            }
            else
            {
                result.setResult(Result.FAIL);
                result.setDescription(comparisonResult.getExplanation());
            }
        }
        catch (Throwable t)
        {
            result.setResult(Result.EXCEPTION);
            result.setThrowable(t);
        }
    }
}
