// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.deriver;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

/** Returns a derived test that checks that a <tt>HEAD</tt> acts just like a <tt>GET</tt>.
*/
public class HeadDeriver implements Derivable
{
    /** Returns a test identicial to the passed-in test, except that does a HEAD request and expects no return body.
     * This will return null if the test is not a 
     {@link com.gliffy.restunit.GetTest} or does not return true from
     * {@link com.gliffy.restunit.GetTest#getRespondsToHead() }.
     * @param test the test to derive.
     * @param response the response from executing the test (not used)
     * @return a newly derived test, or null if no such derivation was possible.
     */
    public RestTest derive(RestTest test, HttpResponse response)
    {

        if (test instanceof GetTest)
        {
            GetTest getTest = (GetTest)test;
            if (getTest.getRespondsToHead())
            {
                GetTest head = (GetTest)test.clone();
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
