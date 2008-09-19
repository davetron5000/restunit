// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

import java.io.*;
import java.util.*;

import com.gliffy.restunit.http.*;

/** The results of a RestCall having been executed. */
public class ExecutionResult implements Serializable
{
    private Result itsResult;
    private Date itsExecutionDate;
    private long itsExecutionTime;
    private RestCall itsCall;
    private Throwable itsThrowable;
    private String itsDescription;
    private HttpResponse itsResponse;

    /** Results of the call execution.
     * @return the results of the call execution
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

    /** Milliseconds it took for the call to run.
     * @return number of millisecond elapsed while the call ran.
     */
    public long getExecutionTime() 
    {
        return itsExecutionTime; 
    }
    public void setExecutionTime(long i) 
    {
        itsExecutionTime = i; 
    }

    /** The call that was run.
     * @return the call that was run
     */
    public RestCall getCall() 
    {
        return itsCall; 
    }
    public void setCall(RestCall i) 
    {
        itsCall = i; 
    }

    public Throwable getThrowable() 
    { 
        return itsThrowable; 
    }

    public void setThrowable(Throwable i) 
    { 
        itsThrowable = i; 
    }

    /** Returns a description of what happened, as provided by the executor of the call. 
     * @return the executor-provided explanation of why the call didn't pass.
     * */
    public String getDescription() 
    { 
        return itsDescription; 
    }

    /** Set an explanation as to why the call failed; an assert message. 
     * @param i the description
     * */
    public void setDescription(String i) 
    { 
        itsDescription = i; 
    }

    /** Returns a simple description of what happened when the call ran.
     * @return a string with the description of this execution.
     */
    public String toString()
    {
        String string = (getCall() == null ? "NULL TEST" : getCall().getName()) + " " 
            + getResult().getPastTense() + ": " 
            + getDescription();

        if (getResult() == Result.EXCEPTION)
            return string + " (Throwable was: " + (getThrowable() == null ? "null" : getThrowable().getMessage()) + ")";
        else
            return string;
    }

    public HttpResponse getResponse() 
    {
        return itsResponse; 
    }

    public void setResponse(HttpResponse i) 
    {
        itsResponse = i; 
    }

}
