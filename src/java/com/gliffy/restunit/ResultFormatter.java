// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

/** Formats results for some readability.
 * This just does a simple ASCII formatting of the results.  If more sophisticated formats are needed, you can
 * subclass this and override {@link #format(RestTestResult)}.
 */
public class ResultFormatter
{
    private static final String INDENT = "\t";

    /** Format the given result as a string.
     * @param result a result from a test execution
     * @return a string that describes the results.  This implementation just uses basic ASCII formatting
     */
    public String format(RestTestResult result)
    {
        StringBuilder b = new StringBuilder();
        b.append("[\n");
        for (RestCallResult callResult: result.getDetailedResults())
        {
            RestCall call = callResult.getCall();
            b.append(INDENT);
            b.append(call.getMethod() + " " + call.getURL() + " - " + callResult.getResult().getPastTense());
            if ( (callResult.getResult() == Result.PASS)
                    || (callResult.getResult() == Result.SKIP))
            {
                // nothing fancy
            }
            else
            {
                b.append(":\n");
                b.append(INDENT);
                b.append(INDENT);
                if (callResult.getResult() == Result.FAIL)
                {
                    b.append(callResult.toString());
                }
                else
                {
                    b.append(callResult.getThrowable().toString());
                    for (StackTraceElement element: callResult.getThrowable().getStackTrace())
                    {
                        b.append(INDENT);
                        b.append(element);
                    }
                }
            }
            b.append(",\n");
        }
        b.append("]");
        return b.toString();
    }
}
