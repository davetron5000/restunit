package com.gliffy.restunit;

import java.util.*;

/** This class executes {@link RestTest} instances */
public class RestUnit
{
    private Set<RestTestExecution> itsExecutionResults;
    private Set<Derivable> itsDerivers;
    private RestTestExecutor itsExecutor;

    public RestUnit()
    {
        itsExecutionResults = new HashSet<RestTestExecution>();
        itsDerivers = new HashSet<Derivable>();
        itsExecutor = new RestTestExecutor();
    }

    /** Adds derivers to be used on all tests */
    public void addDeriver(Derivable d)
    {
        itsDerivers.add(d);
    }
    public RestTestExecutor getExecutor() { return itsExecutor; }
    public void setExecutor(RestTestExecutor i) { itsExecutor = i; }


    /** This executes a rest test, and possibly derived tests.
     * This will run as follows (a failure, skip, or exception at any step, stops the process):
     * <ol>
     * <li>The test itself.  
     * <li>Any tests derived from the derivers add via {@link #addDeriver(Derivable)}</li>
     * <li>Any dependent tests.</li>
     * </ol>
     * @param test the test to run.  
     * @throws TestFailedException if a test fails.
     */
    public void runTest(RestTest test)
    {
        RestTestExecution result = getExecutor().execute(test);
        itsExecutionResults.add(result);
        if (result.getResult() == RestTestResult.PASS)
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
