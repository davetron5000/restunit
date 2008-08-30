package com.gliffy.restunit;

/** Indicates that an executed test did not succeed */
public class TestFailedException extends RuntimeException
{
    private RestTestExecution itsExecution;

    public TestFailedException(RestTestExecution execution)
    {
        super(execution.toString());
        itsExecution = execution;
    }

    public RestTestExecution getExecution() { return itsExecution; }
    public String getMessage() { return itsExecution.toString(); }
}
