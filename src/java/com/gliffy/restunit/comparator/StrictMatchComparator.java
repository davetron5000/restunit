// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.comparator;

import java.io.*;
import java.text.*;
import java.util.*;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

import org.apache.commons.logging.*;

/** A comparator that requires a strict match between received and expected results.
 * This uses the {@link StatusComparator} and {@link HeaderComparator} to check those portions of the
 * response.  The response bodies are compared via {@link #compareBodies(byte[],com.gliffy.restunit.http.ContentType,byte[],com.gliffy.restunit.http.ContentType)}.  This implementation requires
 * a byte-for-byte match.  You may subclass this and override that method to provide fuzzier matches.
 * You may also wish to override {@link #createDiffMessage(byte[],com.gliffy.restunit.http.ContentType,byte[],com.gliffy.restunit.http.ContentType)}
 * to create a meaningful diff message.
 */
public class StrictMatchComparator implements ResultComparator
{
    private static final String DEFAULT_ENCODING_DESC = "system default";
    private StatusComparator itsStatusComparator = new StatusComparator();
    private HeaderComparator itsHeaderComparator = new HeaderComparator();
    private Log itsLogger = LogFactory.getLog(getClass());

    /** Compares the results as per the class Javadoc.
     * @param receivedResponse the response received.
     * @param expectedResponse the expected response, which could be a {@link com.gliffy.restunit.BodyResponse}.  If it
     * is not, the "expected body" is assumed to be null.
     * @return A matching result if the bodies, status and headers match exactly as per the test response.
     */
    public ComparisonResult compare(HttpResponse receivedResponse, RestCallResponse expectedResponse)
    {
        ComparisonResult result = itsStatusComparator.compare(receivedResponse,expectedResponse);
        if (result.getMatches())
        {
            result = itsHeaderComparator.compare(receivedResponse,expectedResponse);
            if (result.getMatches())
            {
                byte expectedBody[] = null;
                byte receivedBody[] = receivedResponse.getBody();

                ContentType expectedContentType = null;
                if (expectedResponse instanceof BodyResponse)
                {
                    BodyResponse bodyResponse = (BodyResponse)expectedResponse;
                    expectedBody = bodyResponse.getBody();
                    expectedContentType = ContentType.getContentType(bodyResponse.getContentType());
                }

                if (expectedContentType == null)
                {
                    itsLogger.debug("No content-type set in expected response, trying the headers");
                    expectedContentType = ContentType.getContentType(expectedResponse.getHeaders().get(RestCallResponse.CONTENT_TYPE_HEADER));
                }
                ContentType receivedContentType = ContentType.getContentType(receivedResponse.getHeaders().get(RestCallResponse.CONTENT_TYPE_HEADER));
                return compareBodies(expectedBody,expectedContentType,receivedBody,receivedContentType);
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
     * @param expectedContentType the content type expected by the test, or null if it wasn't specified
     * @param receivedBody the body received, may be null
     * @param receivedContentType the content type received in the response, or null if it wasn't specified
     * @return a successful match if both bodies are byte-for-byte matches.  a null body
     * and an empty body (0-length array) are considered equal.  Note that if the content type expected is a text-based
     * type the message included in the ComparisonResult will attempt to render both bits of data as strings, for purposes of
     * indicating the difference.  This will be done using the encoding's specified, or the default encoding if none is specified.
     */
    protected ComparisonResult compareBodies(byte []expectedBody, ContentType expectedContentType, byte []receivedBody, ContentType receivedContentType)
    {
        if (Arrays.equals(normalize(expectedBody),normalize(receivedBody)))
        {
            return ComparisonResult.MATCHES;
        }
        else
        {
            return new ComparisonResult(false,createDiffMessage(expectedBody,expectedContentType,receivedBody,receivedContentType));
        }
    }

    /** Given two differing bytestreams, and the content types attached to each, returns a string-based diff message that can help the
     * tester understand why the comparison failed.
     * @param expectedBody the body expected, may be null
     * @param expectedContentType the content type expected by the test, or null if it wasn't specified
     * @param receivedBody the body received, may be null
     * @param receivedContentType the content type received in the response, or null if it wasn't specified
     * @return a human-readable message explaining why the comparison failed.  This version checks to see if the expected content type is a text-based type.
     * If so, it attempts to convert the bytestreams to strings using the encodings specified in their content types.  If the expectedContentType has no encoding
     * the receivedContentType's encoding is used.  If no encodings were detected, the system default is used.  You should probably write your tests with encodings
     * because of this.
     */
    protected String createDiffMessage(byte []expectedBody, ContentType expectedContentType, byte []receivedBody, ContentType receivedContentType)
    {
        String extra = "";
        if ( (expectedContentType != null) && (expectedContentType.getMimeType().startsWith("text/")) )
        {
            itsLogger.debug("Expected content type was " + expectedContentType.getMimeType());
            String expectedEncoding = expectedContentType.getEncoding();
            String receivedEncoding = receivedContentType == null ? null : receivedContentType.getEncoding();
            if (expectedEncoding == null)
                expectedEncoding = receivedEncoding;

            try
            {
                String expected = null;
                String received = null;
                String expectedEncodingDesc = DEFAULT_ENCODING_DESC;
                String receivedEncodingDesc = DEFAULT_ENCODING_DESC;
                if (expectedEncoding == null)
                {
                    expected = new String(expectedBody);
                }
                else
                {
                    expectedEncodingDesc = expectedEncoding;
                    expected = new String(expectedBody,expectedEncoding);
                }

                if (receivedEncoding == null)
                {
                    received = new String(receivedBody);
                }
                else
                {
                    received = new String(receivedBody,receivedEncoding);
                    receivedEncodingDesc = receivedEncoding;
                }
                return MessageFormat.format("Bodies didn't match; expected (using encoding {0}):\n'{1}'\nreceived (using encoding {2}):\n'{3}'",
                        expectedEncodingDesc,expected,receivedEncodingDesc,received);
            }
            catch (UnsupportedEncodingException e)
            {
                itsLogger.warn("Coding " + expectedEncoding + " or " + receivedEncoding + " was not supported",e);
                extra = " (while attempting to output data in string format: " + e.getMessage() + ")";
            }
        }
        else
        {
            itsLogger.debug("Expected Content-Type was " + (expectedContentType == null ? "not specified" : expectedContentType.getMimeType()));
        }
        return "Bodies didn't match" + extra;
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
