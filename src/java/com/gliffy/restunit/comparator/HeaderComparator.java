// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.comparator;

import java.util.*;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

/** A comparator that matches the headers against the test specification.
 * <ul>
 * <li>Any received header that is in {@link com.gliffy.restunit.RestTestResponse#getBannedHeaders()} will cause a failure</li>
 * <li>All headers in {@link com.gliffy.restunit.RestTestResponse#getRequiredHeaders()} must be present with non-null, non-empty values.</li>
 * <li>All headers in {@link com.gliffy.restunit.RestTestResponse#getHeaders()} must be present and their values must match exactly.</li>
 * </ul>
 * The header matching can be overriden via {@link #compareHeaderValues(java.lang.String,java.lang.String,java.lang.String)}.
 */
public class HeaderComparator implements ResultComparator
{
    /** Create a new HeaderComparator.
     * @param receivedResponse the response received
     * @param expectedResponse the response expected.
     * @return true if the headers match (see class Javadoc).
     */
    public ComparisonResult compare(HttpResponse receivedResponse, RestTestResponse expectedResponse)
    {
        Set<String> expectedHeaders = new HashSet<String>(expectedResponse.getRequiredHeaders());
        expectedHeaders.addAll(expectedResponse.getHeaders().keySet());

        for (String header: receivedResponse.getHeaders().keySet())
        {
            if (expectedResponse.getBannedHeaders().contains(header))
            {
                return new ComparisonResult(false,"Recieved header '" + header + "', which the test says should NOT be received");
            }

            if (expectedHeaders.contains(header))
            {
                expectedHeaders.remove(header);
                if (expectedResponse.getHeaders().containsKey(header))
                {
                    String headerValueGot = receivedResponse.getHeaders().get(header);
                    String headerValueExpected = expectedResponse.getHeaders().get(header);

                    ComparisonResult result = compareHeaderValues(header,headerValueGot,headerValueExpected);
                    if (!result.getMatches())
                        return result;
                }
            }
        }
        if (expectedHeaders.size() != 0)
        {
            StringBuilder b = new StringBuilder("Expected headers not received: ");
            for (String header: expectedHeaders)
            {
                b.append(header);
                b.append(",");
            }
            b.setLength(b.length()-1);
            return new ComparisonResult(false,b.toString());
        }
        return ComparisonResult.MATCHES;
    }

    /** Performs a comparison of header values for entires in {@link com.gliffy.restunit.RestTestResponse#getHeaders}.
     * This checks for an exactly value match.  You can override this method to provide fuzzy matches or whatever other
     * kind of comparison you wish.
     * @param header the name of the header (used in failure explanations)
     * @param received the value for the header that was received.
     * @param expected the value for the header that the test expects.  May not be null.
     * @return {@link ComparisonResult#MATCHES} if received and expects are an exact match.  A non-matching result otherwise.
     */
    protected ComparisonResult compareHeaderValues(String header, String received, String expected)
    {
        if (expected == null)
            throw new IllegalArgumentException("Cannot expect a header value of null (" + header + ")");

        if (!expected.equals(received))
        {
            return new ComparisonResult(false,"Expected header '" + header + "' received value '" + received + "', but test required '" + expected + "'");
        }
        else
        {
            return ComparisonResult.MATCHES;
        }
    }
}
