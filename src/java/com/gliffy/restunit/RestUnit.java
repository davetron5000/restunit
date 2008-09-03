package com.gliffy.restunit;

import java.util.*;

/** This class executes {@link RestTest} instances. */
public class RestUnit
{
    private Set<ExecutionResult> itsExecutionResults;
    private Set<Derivable> itsDerivers;
    private Executor itsExecutor;

    /** Creates a new RestUnit, deferring the setting of the executor to later.
     */
    public RestUnit()
    {
        this(null);
    }

    /** Create a new RestUnit. 
     * @param executor the test executor to use
     */
    public RestUnit(Executor executor)
    {
        itsExecutionResults = new HashSet<ExecutionResult>();
        itsDerivers = new HashSet<Derivable>();
        itsExecutor = executor;
    }

    /** Adds derivers to be used on all tests.
     * @param d the Derivable that will be used to derive new tests.
     */
    public void addDeriver(Derivable d)
    {
        itsDerivers.add(d);
    }

    public Executor getExecutor() 
    {
        return itsExecutor; 
    }
    public void setExecutor(Executor i) 
    {
        itsExecutor = i; 
    }

    /** This executes a rest test, and possibly derived tests.
     * This will run as follows (a failure, skip, or exception at any step, stops the process):
     * <ol>
     * <li>The test itself.  
     * <li>Any tests derived from the derivers add via 
     {@link #addDeriver(Derivable)}</li>
     * <li>Any dependent tests.</li>
     * </ol>
     * @param test the test to run.  
     */
    public void runTest(RestTest test)
    {
        if (getExecutor() == null)
            throw new IllegalStateException("You may not run tests without a configured executor either via the constructor or setExecutor");

        ExecutionResult result = getExecutor().execute(test);
        itsExecutionResults.add(result);
        if (result.getResult() == Result.PASS)
        {
            for (Derivable d: itsDerivers)
            {
                RestTest derived = d.derive(test);
                if (derived != null)
                    runTest(derived);
            }
            for (RestTest dep: test.getDependentTests())
            {
                runTest(dep);
            }
        }
        else
        {
            throw new TestFailedException(result);
        }
    }
}
