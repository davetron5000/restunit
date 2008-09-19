// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.deriver;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

/** Returns a derived call that checks that a <tt>HEAD</tt> acts just like a <tt>GET</tt>.
*/
public class HeadDeriver implements Derivable
{
    /** Returns a call identicial to the passed-in call, except that does a HEAD request and expects no return body.
     * This will return null if the call is not a {@link com.gliffy.restunit.GetCall} or does not return true from
     * {@link com.gliffy.restunit.GetCall#getRespondsToHead() }.
     * @param call the call to derive.
     * @param response the response from executing the call (not used)
     * @return a newly derived call, or null if no such derivation was possible.
     */
    public RestCall derive(RestCall call, HttpResponse response)
    {

        if (call instanceof GetCall)
        {
            GetCall getTest = (GetCall)call;
            if (getTest.getRespondsToHead())
            {
                GetCall head = (GetCall)call.clone();
                head.setDescription(head.getDescription() + " (derived by " + getClass().getName() + ")");
                head.setRespondsToHead(false);
                head.setMethod("HEAD");
                if (head.getResponse() instanceof BodyResponse)
                {
                    BodyResponse r = (BodyResponse)head.getResponse();
                    r.setBody(null);
                }
                return head;
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
