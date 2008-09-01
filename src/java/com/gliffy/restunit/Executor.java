package com.gliffy.restunit;

/** Handles the nuts and bolts of executing the rest test.
*/
public class Executor
{
    /** Executes a rest test.  No derived or dependent tests are executed
     * @param test the test to execute.
     * @return the results of the test.  This will always return, no exceptions are thrown from this method
     */
    public ExecutionResult execute(RestTest test)
    {
        ExecutionResult ex = new ExecutionResult();
        ex.setTest(test);
        ex.setExecutionDate(new java.util.Date());
        ex.setExecutionTime(0L);
        ex.setResult(Result.SKIP);
        return ex;
    }
}
