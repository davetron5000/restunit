package com.gliffy.restunit;

/** Indicates that an executed test did not succeed. */
public class TestFailedException extends RuntimeException
{
    private final RestTestExecution itsExecution;

    /** Create an exception based on the given result.
     * @param execution the test execution that triggered this exception.
     */
    public TestFailedException(RestTestExecution execution)
    {
        super(execution.toString());
        itsExecution = execution;
    }

    /** Gets the test execution that triggered this exception.
     * @return a RestTestExecution.
     */
    public RestTestExecution getExecution() 
    {
        return itsExecution; 
    }
    /** Returns a brief description of the failure.
     * @return a string with the failure description.
     * */
    public String getMessage() 
    {
        return itsExecution.toString(); 
    }

}
