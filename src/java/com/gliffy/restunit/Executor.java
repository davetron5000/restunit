package com.gliffy.restunit;

import com.gliffy.restunit.http.*;

/** Handles the nuts and bolts of executing the rest test.
*/
public class Executor
{
    private Http itsHttp;

    /** Creates an executor, deferring setting of the Http imlementation until later.
     */
    public Executor()
    {
        this(null);
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

    /** Executes a rest test.  No derived or dependent tests are executed.  If the http implementation has not been set, this will
     * throw an {@link java.lang.IllegalStateException}.
     * @param test the test to execute.
     * @return the results of the test.  This will always return, no exceptions are thrown from this method
     */
    public ExecutionResult execute(RestTest test)
    {
        if (getHttp() == null)
            throw new IllegalStateException();
        HttpRequest request = createRequest(test);

        HttpResponse response = null;

        long testStartTime = System.currentTimeMillis();

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

        ExecutionResult ex = new ExecutionResult();
        ex.setTest(test);
        ex.setExecutionDate(new java.util.Date());
        ex.setExecutionTime(System.currentTimeMillis() - testStartTime);
        ex.setResult(Result.SKIP);
        return ex;
    }

    private HttpRequest createRequest(RestTest test)
    {
        return new HttpRequest();
    }
}
