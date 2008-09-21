// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** Results of running a {@link RestTest}.
 */
public class RestTestResult implements Serializable
{
    private List<RestCallResult> itsDetailedResults;
    private boolean itsSuccess;

    /** Sets the detailed list of results. 
     * @param results the detailed list of results for the calls that make up the test for which this is the results.
     * */
    public void setDetailedResults (List<RestCallResult> results)
    {
        itsDetailedResults = Collections.unmodifiableList(results);
    }
    /** returns the detailed results of the test, with one result
     * per call.
     * @return the list of detailed results per call.
     */
    public List<RestCallResult> getDetailedResults() 
    {
        return itsDetailedResults;
    }

    public void setSuccess(boolean success) 
    {
        itsSuccess = success; 
    }

    public boolean getSuccess() 
    {
        return itsSuccess; 
    }
}
