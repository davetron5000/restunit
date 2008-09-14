package com.gliffy.restunit;

import java.util.*;

import org.apache.commons.logging.*;

/** This class executes {@link RestTest} instances. */
public class RestUnit
{
    private Set<Derivable> itsDerivers;
    private Executor itsExecutor;
    private Log itsLogger = LogFactory.getLog(Executor.class);

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
     * <li>Any tests derived from the derivers added via {@link #addDeriver(Derivable)}</li>
     * <li>Any dependent tests.</li>
     * </ol>
     * @param test the test to run.  
     * @return the results of the execution.  This is a list, as the test passed-in could yield numerous tests.
     * The results are inserted in the order executed (or examined and skipped).  
     */
    public List<ExecutionResult> runTest(RestTest test)
    {
        if (getExecutor() == null)
            throw new IllegalStateException("You may not run tests without a configured executor either via the constructor or setExecutor");

        itsLogger.debug("Executing test " + test.toString());

        List<ExecutionResult> results = new ArrayList<ExecutionResult>();
        ExecutionResult result = getExecutor().execute(test);
        results.add(result);
        if (result.getResult() == Result.PASS)
        {
            itsLogger.debug("Test passed");
            for (Derivable d: itsDerivers)
            {
                RestTest derived = d.derive(test);
                if (derived != null)
                {
                    itsLogger.debug("Executing derived test");
                    results.addAll(runTest(derived));
                }
            }
            for (RestTest dep: test.getDependentTests())
            {
                itsLogger.debug("Executing dependant test");
                results.addAll(runTest(dep));
            }
        }
        else
        {
            itsLogger.debug("Test failed, marking immediate children as skipped");
            for (RestTest dep: test.getDependentTests())
            {
                ExecutionResult skipped = new ExecutionResult();
                skipped.setTest(dep);
                skipped.setResult(Result.SKIP);
                skipped.setDescription("Parent did not pass");
                results.add(skipped);
            }
        }
        itsLogger.debug("Returning " + results.size() + " results");
        return results;
    }
}
