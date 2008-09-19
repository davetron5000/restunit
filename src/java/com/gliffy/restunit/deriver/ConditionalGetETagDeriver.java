// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.deriver;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

import org.apache.commons.logging.*;

/** Derives a conditional get based on etags if the call supports it.
 * This will use the Etag header from the response to resend the 
 * get request, looking for a "304/Not Modified" response from the server.
 */
public class ConditionalGetETagDeriver implements Derivable
{
    private Log itsLogger = LogFactory.getLog(getClass());

    /** Derives a conditonal get using the ETag header's value.
     * @param call  The call.
     * @param response the response.  If this doesn't contain a ETag header, an exception is
     * thrown.  The reason is that the call passed-in should have required that the Last-Modified header
     * be present. 
     * @return a call for a conditional get, or null
     */
    public RestCall derive(RestCall call, HttpResponse response)
    {
        if (call instanceof GetCall)
        {
            GetCall getTest = (GetCall)call;
            if (getTest.getRespondsToIfNoneMatch())
            {
                GetCall etag = (GetCall)call.clone();
                etag.setDescription(etag.getDescription() + " (derived by " + getClass().getName() + ")");
                etag.setRespondsToHead(false);
                etag.setRespondsToIfModified(false);
                etag.setRespondsToIfNoneMatch(false);
                String etagTag = response.getHeaders().get(RestCallResponse.ETAG_HEADER);

                if (etagTag == null)
                    throw new IllegalStateException("Your call (" + call.toString() + ") says it responds to IfNoneMatch, however the " + 
                            RestCallResponse.ETAG_HEADER + " wasn't set in the response it generated.  This means you didn't require that header in the call.  You should.");

                etag.getHeaders().put(RestCallResponse.IF_NONE_MATCH_HEADER,etagTag);
                etag.getHeaders().remove(RestCallResponse.IF_MODIFIED_SINCE_HEADER);
                etag.getResponse().getHeaders().remove(RestCallResponse.ETAG_HEADER);
                etag.getResponse().getRequiredHeaders().remove(RestCallResponse.ETAG_HEADER);
                etag.getResponse().getHeaders().remove(RestCallResponse.LAST_MODIFIED_HEADER);
                etag.getResponse().getRequiredHeaders().remove(RestCallResponse.LAST_MODIFIED_HEADER);
                if (etag.getResponse() instanceof BodyResponse)
                {
                    BodyResponse r = (BodyResponse)etag.getResponse();
                    r.setBody(null);
                }
                etag.getResponse().setStatusCode(RestCallResponse.NOT_MODIFIED_STATUS);
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
