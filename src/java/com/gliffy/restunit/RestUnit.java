// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

import java.io.*;
import java.util.*;

import org.apache.commons.logging.*;

/** Entry point to RESTUnit test execution.
 */
public class RestUnit 
{
    private Log itsLogger = LogFactory.getLog(getClass());
    private Executor itsExecutor;

    /** Executes the test.
     * @param test the test to execute.
     * @return returns the results of the test.  Will not throw an exception and will not return null.
     * */
    public RestTestResult runTest(RestTest test)
    {
        itsLogger.debug("Running test " + test.getName());
        List<RestCallResult> results = new ArrayList<RestCallResult>(test.getCalls().size());
        boolean skipRest = false;
        boolean success = true;
        for (RestCall call: test.getCalls())
        {
            if (call.getURL() == null)
                call.setURL(test.getDefaultURL());
            RestCallResult result;
            if (skipRest)
            {
                result = itsExecutor.skip(call);
            }
            else
            {
                result = itsExecutor.execute(call);
                if (result.getResult() != Result.PASS)
                {
                    success = false;
                    skipRest = true;
                }
            }
            results.add(result);
        }
        RestTestResult result = new RestTestResult();
        result.setSuccess(success);
        result.setDetailedResults(results);
        return result;
    }

    public void setExecutor(Executor executor) 
    { 
        itsExecutor = executor; 
    }
    public Executor getExecutor() 
    { 
        return itsExecutor; 
    }


}
