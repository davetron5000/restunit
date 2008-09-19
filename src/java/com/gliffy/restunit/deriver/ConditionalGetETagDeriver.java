// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.deriver;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

import org.apache.commons.logging.*;

/** Derives a conditional get based on etags if the test supports it.
 * This will use the Etag header from the response to resend the 
 * get request, looking for a "304/Not Modified" response from the server.
 */
public class ConditionalGetETagDeriver implements Derivable
{
    private Log itsLogger = LogFactory.getLog(getClass());

    /** Derives a conditonal get using the ETag header's value.
     * @param test  The test.
     * @param response the response.  If this doesn't contain a ETag header, an exception is
     * thrown.  The reason is that the test passed-in should have required that the Last-Modified header
     * be present. 
     * @return a test for a conditional get, or null
     */
    public RestTest derive(RestTest test, HttpResponse response)
    {
        if (test instanceof GetTest)
        {
            GetTest getTest = (GetTest)test;
            if (getTest.getRespondsToIfNoneMatch())
            {
                GetTest etag = (GetTest)test.clone();
                etag.getDependentTests().clear();
                etag.setDescription(etag.getDescription() + " (derived by " + getClass().getName() + ")");
                etag.setRespondsToHead(false);
                etag.setRespondsToIfModified(false);
                etag.setRespondsToIfNoneMatch(false);
                String etagTag = response.getHeaders().get(RestTestResponse.ETAG_HEADER);

                if (etagTag == null)
                    throw new IllegalStateException("Your test (" + test.toString() + ") says it responds to IfNoneMatch, however the " + 
                            RestTestResponse.ETAG_HEADER + " wasn't set in the response it generated.  This means you didn't require that header in the test.  You should.");

                etag.getHeaders().put(RestTestResponse.IF_NONE_MATCH_HEADER,etagTag);
                etag.getHeaders().remove(RestTestResponse.IF_MODIFIED_SINCE_HEADER);
                etag.getResponse().getHeaders().remove(RestTestResponse.ETAG_HEADER);
                etag.getResponse().getRequiredHeaders().remove(RestTestResponse.ETAG_HEADER);
                etag.getResponse().getHeaders().remove(RestTestResponse.LAST_MODIFIED_HEADER);
                etag.getResponse().getRequiredHeaders().remove(RestTestResponse.LAST_MODIFIED_HEADER);
                if (etag.getResponse() instanceof BodyResponse)
                {
                    BodyResponse r = (BodyResponse)etag.getResponse();
                    r.setBody(null);
                }
                etag.getResponse().setStatusCode(RestTestResponse.NOT_MODIFIED_STATUS);
                return etag;
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
