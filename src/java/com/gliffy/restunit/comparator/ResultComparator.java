// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.comparator;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

/** Interface defining the comparison of results.  This allows you to customize the logic that
 * determines if a test passed or not.  For example, you may be receiving XML that contains temporal data and wish to
 * ignore it for the purposes of testing.
 */
public interface ResultComparator
{
    /** Compares the response from the service to the expected response.
     * @param receivedResponse the response received from the REST service under test.
     * @param expectedResponse the response that was expected, as defined by the test.
     * @return the results of the comparison.
     * @throws Exception Implementors may throw whichever exceptions they need to.  Exceptions should be 
     * throw for exceptional conditions unrelated to result comparison.  This allows implementors to
     * just focuse on comparing the results. 
     */
    ComparisonResult compare(HttpResponse receivedResponse, RestTestResponse expectedResponse)
        throws Exception;
}
