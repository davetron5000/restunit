package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** The results of a RestTest having been executed */
public class RestTestExecution implements Serializable
{
    private RestTestResult itsResult;
    public RestTestResult getResult() { return itsResult; }
    public void setResult(RestTestResult i) { itsResult = i; }

    private Date itsExecutionDate;
    public Date getExecutionDate() { return itsExecutionDate; }
    public void setExecutionDate(Date i) { itsExecutionDate = i; }

    private long itsExecutionTime;
    public long getExecutionTime() { return itsExecutionTime; }
    public void setExecutionTime(long i) { itsExecutionTime = i; }

    private RestTest itsTest;
    public RestTest getTest() { return itsTest; }
    public void setTest(RestTest i) { itsTest = i; }

}
