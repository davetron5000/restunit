// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.comparator;

import java.util.*;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

/** A comparator that requires a strict match between received and expected results.
 * This uses the {@link StatusComparator} and {@link HeaderComparator} to check those portions of the
 * response.  The response bodies are compared via {@link #compareBodies(byte[],byte[])}.  This implementation requires
 * a byte-for-byte match.  You may subclass this and override that method to provide fuzzier matches.
 */
public class StrictMatchComparator implements ResultComparator
{
    private StatusComparator itsStatusComparator = new StatusComparator();
    private HeaderComparator itsHeaderComparator = new HeaderComparator();

    /** Compares the results as per the class Javadoc.
     * @param receivedResponse the response received.
     * @param expectedResponse the expected response, which could be a {@link com.gliffy.restunit.BodyResponse}.  If it
     * is not, the "expected body" is assumed to be null.
     * @return A matching result if the bodies, status and headers match exactly as per the test response.
     */
    public ComparisonResult compare(HttpResponse receivedResponse, RestTestResponse expectedResponse)
    {
        ComparisonResult result = itsStatusComparator.compare(receivedResponse,expectedResponse);
        if (result.getMatches())
        {
            result = itsHeaderComparator.compare(receivedResponse,expectedResponse);
            if (result.getMatches())
            {
                byte expectedBody[] = null;
                byte receivedBody[] = receivedResponse.getBody();

                if (expectedResponse instanceof BodyResponse)
                    expectedBody = ((BodyResponse)expectedResponse).getBody();

                return compareBodies(expectedBody,receivedBody);
            }
            else
            {
                return result;
            }
        }
        else
        {
            return result;
        }
    }

    /** Compares the two bodies, requiring an exact match.
     * @param expectedBody the body expected, may be null
     * @param receivedBody the body received, may be null
     * @return a successful match if both bodies are byte-for-byte matches.  a null body
     * and an empty body (0-length array) are considered equal.
     */
    protected ComparisonResult compareBodies(byte []expectedBody, byte []receivedBody)
    {
        if (Arrays.equals(normalize(expectedBody),normalize(receivedBody)))
        {
            return ComparisonResult.MATCHES;
        }
        else
        {
            return new ComparisonResult(false,"Bodies didn't match");
        }
    }

    /** Normalizes the byte array to an empty array if it is null.
     * @param bytes a byte array, or null
     * @return a byte array.  if bytes was null, a size 0 array is returned
     */
    private byte[] normalize(byte [] bytes)
    {
        if (bytes == null)
            return new byte[0];
        else
            return bytes;
    }
}
