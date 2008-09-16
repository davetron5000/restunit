// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.deriver;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

/** Derives a conditional get if the test supports it.
 * This will use the last-modified header from the response to resend the 
 * get request, looking for a "304/Not Modified" response from the server.
 */
public class ConditionalGetLastModifiedDeriver implements Derivable
{
    /** Derives a conditonal get using the Last-Modified header's value.
     * @param test  The test.
     * @param response the response.  If this doesn't contain a Last-Modified header, an exception is
     * thrown.  The reason is that the test passed-in should have required that the Last-Modified header
     * be present. 
     * @return a test for a conditional get, or null
     */
    public RestTest derive(RestTest test, HttpResponse response)
    {
        if (test instanceof GetTest)
        {
            GetTest getTest = (GetTest)test;
            if (getTest.getRespondsToIfModified())
            {
                GetTest lastModified = (GetTest)test.clone();
                lastModified.setDescription(lastModified.getDescription() + " (derived by " + getClass().getName() + ")");
                lastModified.setRespondsToHead(false);
                lastModified.setRespondsToIfModified(false);
                lastModified.setRespondsToIfNoneMatch(false);

                String lastModifiedDate = response.getHeaders().get(RestTestResponse.LAST_MODIFIED_HEADER);

                if (lastModifiedDate == null)
                    throw new IllegalStateException("Your test (" + test.toString() + ") says it responds to IfModified, however the " + 
                            RestTestResponse.LAST_MODIFIED_HEADER + " wasn't set in the response it generated.  This means you didn't require that header in the test.  You should.");

                lastModified.getHeaders().put(RestTestResponse.IF_MODIFIED_SINCE_HEADER,lastModifiedDate);
                lastModified.getHeaders().remove(RestTestResponse.IF_NONE_MATCH_HEADER);
                lastModified.getResponse().getHeaders().remove(RestTestResponse.LAST_MODIFIED_HEADER);
                lastModified.getResponse().getRequiredHeaders().remove(RestTestResponse.LAST_MODIFIED_HEADER);
                lastModified.getResponse().getHeaders().remove(RestTestResponse.ETAG_HEADER);
                lastModified.getResponse().getRequiredHeaders().remove(RestTestResponse.ETAG_HEADER);
                if (lastModified.getResponse() instanceof BodyResponse)
                {
                    BodyResponse r = (BodyResponse)lastModified.getResponse();
                    r.setBody(null);
                }
                lastModified.getResponse().setStatusCode(RestTestResponse.NOT_MODIFIED_STATUS);
                return lastModified;
            }
            else
            {
                return null;
            }
        }
        else
        {
            return null;
        }
    }
}
