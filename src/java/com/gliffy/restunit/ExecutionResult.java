package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** The results of a RestTest having been executed. */
public class ExecutionResult implements Serializable
{
    private Result itsResult;
    private Date itsExecutionDate;
    private long itsExecutionTime;
    private RestTest itsTest;

    /** Results of the test execution.
     * @return the results of the test execution
     */
    public Result getResult() 
    {
        return itsResult; 
    }
    public void setResult(Result i) 
    {
        itsResult = i; 
    }

    public Date getExecutionDate() 
    {
        return itsExecutionDate; 
    }
    public void setExecutionDate(Date i) 
    {
        itsExecutionDate = i; 
    }

    /** Milliseconds it took for the test to run.
     * @return number of millisecond elapsed while the test ran.
     */
    public long getExecutionTime() 
    {
        return itsExecutionTime; 
    }
    public void setExecutionTime(long i) 
    {
        itsExecutionTime = i; 
    }

    /** The test that was run.
     * @return the test that was run
     */
    public RestTest getTest() 
    {
        return itsTest; 
    }
    public void setTest(RestTest i) 
    {
        itsTest = i; 
    }

    /** Returns a simple description of what happened when the test ran.
     * @return a string with the description of this execution.
     */
    public String toString()

    {

        return getTest().getName() + " " + getResult().getPastTense();

    }

}
