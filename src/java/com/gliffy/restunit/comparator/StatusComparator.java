// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.comparator;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

/** A comparator that only matches the status codes.
 * This is useful mostly for checking erroneous conditions, but is also useful for 
 * subclassing if you wish to customize the header comparison logic.
 */
public class StatusComparator implements ResultComparator
{
    /** Requires only that the HTTP statusses match.
     * @param receivedResponse the response received
     * @param expectedResponse the response expected.
     * @return true if the stautses are equal.
     */
    public ComparisonResult compare(HttpResponse receivedResponse, RestTestResponse expectedResponse)
    {
        if (receivedResponse.getStatusCode() == expectedResponse.getStatusCode())
            return ComparisonResult.MATCHES;
        else
            return new ComparisonResult(false,"Expected status of " + expectedResponse.getStatusCode() + ", but received " + receivedResponse.getStatusCode());

    }
}
