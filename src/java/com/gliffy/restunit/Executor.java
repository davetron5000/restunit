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
        ExecutionResult ex = new ExecutionResult();
        ex.setTest(test);
        ex.setExecutionDate(new java.util.Date());
        ex.setExecutionTime(0L);
        ex.setResult(Result.SKIP);
        return ex;
    }
}
