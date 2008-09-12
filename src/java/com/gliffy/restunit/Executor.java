package com.gliffy.restunit;

import java.io.*;
import java.net.*;
import java.util.*;

import com.gliffy.restunit.comparator.*;
import com.gliffy.restunit.http.*;

import org.apache.commons.logging.*;

/** Handles the nuts and bolts of executing one test.  This can be re-used, even if you wish to change HTTP implementations or comparators.
 * This class's behavior can be configured in the following ways:
 * <ul>
 * <li>{@link com.gliffy.restunit.http.Http} - this implements the basic HTTP protocol and allows you to use any implementation
 * you wish.  You must set something via {@link #setHttp(com.gliffy.restunit.http.Http)}.</li>
 * <li>{@link com.gliffy.restunit.comparator.ResultComparator} - this performs the comparison of results.  By default, this class uses the {@link
 * com.gliffy.restunit.comparator.StrictMatchComparator}.  You may override this if custom comparisons are required.
 * <li>BaseURL - you may optionally provide a base URL against which all requests are run.  This is handy if you don't want your tests to have full URLs in them</li>
 * </ul>
*/
public class Executor
{
    private Http itsHttp;
    private String itsBaseURL;
    private ResultComparator itsComparator;

    private HttpRequestFactory itsRequestFactory;
    private Log itsLogger = LogFactory.getLog(Executor.class);

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

    /** Sets the base URL against which test urls are appended.
     * This allows test urls to be relative to some externalized server, if needed
     * @param i the url
     */
    public void setBaseURL(String i) 
    {
        itsBaseURL = i; 
        itsRequestFactory = new HttpRequestFactory(itsBaseURL);
        itsLogger.debug("New HttpRequestFactory created with base url " + itsBaseURL);
    }

    /** Executes a rest test.  No derived or dependent tests are executed.  If the http implementation has not been set, this will
     * throw an {@link java.lang.IllegalStateException}.
     * @param test the test to execute.
     * @return the results of the test.  This will always return, no exceptions are thrown from this method
     */
    public ExecutionResult execute(RestTest test)
    {
        if (getHttp() == null)
            throw new IllegalStateException("No HTTP implementation configured");

        itsLogger.debug("Starting test " + test.getName());
        long testStartTime = System.currentTimeMillis();
        ExecutionResult executionResult = new ExecutionResult();
        executionResult.setTest(test);
        executionResult.setExecutionDate(new java.util.Date());

        try
        {
            HttpRequest request = itsRequestFactory.createRequest(test);
            itsLogger.debug("Request created for " + request.getURL().toString());
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

            itsLogger.debug("Received response");

            populateResult(test,executionResult,response);
            return executionResult;
        }
        catch (MalformedURLException e)
        {
            executionResult.setResult(Result.EXCEPTION);
            executionResult.setThrowable(e);
        }
        executionResult.setExecutionTime(System.currentTimeMillis() - testStartTime);
        itsLogger.debug("Test execution complete");
        return executionResult;
    }

    private void populateResult(RestTest test, ExecutionResult result, HttpResponse response)
    {
        RestTestResponse expectedResponse = test.getResponse();
        if (expectedResponse == null)
        {
            throw new IllegalArgumentException("Test did not have an expected response");
        }
        if (expectedResponse.getStatusCode() != response.getStatusCode())
        {
            result.setResult(Result.FAIL);
            result.setDescription("Got status " + response.getStatusCode() + ", expected " + expectedResponse.getStatusCode());
            return;
        }

        try
        {
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
