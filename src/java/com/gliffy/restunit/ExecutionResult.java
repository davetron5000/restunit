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
    private Throwable itsThrowable;
    private String itsDescription;


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

    public Throwable getThrowable() 
    { 
        return itsThrowable; 
    }

    public void setThrowable(Throwable i) 
    { 
        itsThrowable = i; 
    }

    /** Returns a description of what happened, as provided by the executor of the test. 
     * @return the executor-provided explanation of why the test didn't pass.
     * */
    public String getDescription() 
    { 
        return itsDescription; 
    }

    /** Set an explanation as to why the test failed; an assert message. 
     * @param i the description
     * */
    public void setDescription(String i) 
    { 
        itsDescription = i; 
    }

    /** Returns a simple description of what happened when the test ran.
     * @return a string with the description of this execution.
     */
    public String toString()
    {
        String string = (getTest() == null ? "NULL TEST" : getTest().getName()) + " " 
            + getResult().getPastTense() + ": " 
            + getDescription();

        if (getResult() == Result.EXCEPTION)
            return string + " (Throwable was: " + (getThrowable() == null ? "null" : getThrowable().getMessage()) + ")";
        else
            return string;
    }
}
