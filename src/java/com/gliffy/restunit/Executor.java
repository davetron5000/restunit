package com.gliffy.restunit;

import java.io.*;
import java.net.*;
import java.util.*;

import com.gliffy.restunit.http.*;

import org.apache.commons.logging.*;

/** Handles the nuts and bolts of executing the rest test.
 * This class can be configured in two ways:
 * <ul>
 * <li>{@link com.gliffy.restunit.http.Http} - this implements the basic HTTP protocol and allows you to use any implementation
 * you wish.  You must set something via {@link #setHttp(com.gliffy.restunit.http.Http}.</li>
 * <li>BaseURL - you may optionally provide a base URL against which all requests are run.  This is handy if you don't want your tests to have full URLs in them</li>
 * </ul>
*/
public class Executor
{
    private Http itsHttp;
    private String itsBaseURL;
    private HttpRequestFactory itsRequestFactory;
    private Log itsLogger = LogFactory.getLog(Executor.class);

    /** Creates an executor, deferring setting of the Http imlementation until later.
     */
    public Executor()
    {
        itsRequestFactory = new HttpRequestFactory("");
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

        if (headersOK(expectedResponse,result,response))
        {
            if (bodyMatches(expectedResponse,result,response))
                result.setResult(Result.PASS);
            else
                result.setResult(Result.FAIL);
        }
        else
        {
            result.setResult(Result.FAIL);
        }
    }

    private boolean bodyMatches(RestTestResponse expectedResponse, ExecutionResult result, HttpResponse response)
    {
        if (expectedResponse instanceof BodyResponse)
        {
            byte expected[] = ((BodyResponse)expectedResponse).getBody();
            byte received[] = response.getBody();

            if ( (expected == null) && (received == null) )
                return true;
            if (expected == null)
            {
                result.setDescription("Expected no body, but received one");
                return false;
            }
            if (received == null)
            {
                result.setDescription("Expected a body, but didn't get one");
                return false;
            }
            if (received.length != expected.length)
            {
                result.setDescription("Expected " + expected.length + " bytes, but got " + received.length);
                return false;
            }

            for (int i=0;i<expected.length; i++)
            {
                if (expected[i] != received[i])
                {
                    result.setDescription("Byte " + i + " was a " + String.valueOf(received[i]) + ", but we expected " + String.valueOf(expected[i]));
                    return false;
                }
            }
            return true;
        }
        else
        {
            return true;
        }
    }

    private boolean headersOK(RestTestResponse expectedResponse, ExecutionResult result, HttpResponse response)
    {
        Set<String> expectedHeaders = new HashSet<String>(expectedResponse.getRequiredHeaders());
        expectedHeaders.addAll(expectedResponse.getHeaders().keySet());

        for (String header: response.getHeaders().keySet())
        {
            if (expectedResponse.getBannedHeaders().contains(header))
            {
                result.setDescription("Recieved header '" + header + "', which the test says should NOT be received");
                return false;
            }

            if (expectedHeaders.contains(header))
            {
                expectedHeaders.remove(header);
                if (expectedResponse.getHeaders().containsKey(header))
                {
                    String headerValueGot = response.getHeaders().get(header);
                    String headerValueExpected = expectedResponse.getHeaders().get(header);

                    if (headerValueExpected == null)
                        throw new IllegalArgumentException("Cannot expect a header value of null (" + header + ")");

                    if (!headerValueExpected.equals(headerValueGot))
                    {
                        result.setDescription("Expected header '" + header + "' received value '" + headerValueGot + "', but test required '" + headerValueExpected + "'");
                        return false;
                    }
                }
            }
        }
        if (expectedHeaders.size() != 0)
        {
            StringBuilder b = new StringBuilder("Expected headers not received: ");
            for (String header: expectedHeaders)
            {
                b.append(header);
                b.append(",");
            }
            b.setLength(b.length()-1);
            result.setDescription(b.toString());
            return false;
        }
        return true;
    }

}
