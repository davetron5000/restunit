// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.deriver;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

/** Derives a conditional get if the call supports it.
 * This will use the last-modified header from the response to resend the 
 * get request, looking for a "304/Not Modified" response from the server.
 */
public class ConditionalGetLastModifiedDeriver implements Derivable
{
    /** Derives a conditonal get using the Last-Modified header's value.
     * @param call  The call.
     * @param response the response.  If this doesn't contain a Last-Modified header, an exception is
     * thrown.  The reason is that the call passed-in should have required that the Last-Modified header
     * be present. 
     * @return a call for a conditional get, or null
     */
    public RestCall derive(RestCall call, HttpResponse response)
    {
        if (call instanceof GetCall)
        {
            GetCall getCall = (GetCall)call;
            if (getCall.getRespondsToIfModified())
            {
                GetCall lastModified = (GetCall)call.clone();
                lastModified.setDescription(lastModified.getDescription() + " (derived by " + getClass().getName() + ")");
                lastModified.setRespondsToHead(false);
                lastModified.setRespondsToIfModified(false);
                lastModified.setRespondsToIfNoneMatch(false);

                String lastModifiedDate = response.getHeaders().get(RestCallResponse.LAST_MODIFIED_HEADER);

                if (lastModifiedDate == null)
                    throw new IllegalStateException("Your call (" + call.toString() + ") says it responds to IfModified, however the " + 
                            RestCallResponse.LAST_MODIFIED_HEADER + " wasn't set in the response it generated.  This means you didn't require that header in the call.  You should.");

                lastModified.getHeaders().put(RestCallResponse.IF_MODIFIED_SINCE_HEADER,lastModifiedDate);
                lastModified.getHeaders().remove(RestCallResponse.IF_NONE_MATCH_HEADER);
                lastModified.getResponse().getHeaders().remove(RestCallResponse.LAST_MODIFIED_HEADER);
                lastModified.getResponse().getRequiredHeaders().remove(RestCallResponse.LAST_MODIFIED_HEADER);
                lastModified.getResponse().getHeaders().remove(RestCallResponse.ETAG_HEADER);
                lastModified.getResponse().getRequiredHeaders().remove(RestCallResponse.ETAG_HEADER);
                if (lastModified.getResponse() instanceof BodyResponse)
                {
                    BodyResponse r = (BodyResponse)lastModified.getResponse();
                    r.setBody(null);
                }
                lastModified.getResponse().setStatusCode(RestCallResponse.NOT_MODIFIED_STATUS);
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
