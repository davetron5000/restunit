package com.gliffy.restunit.deriver;

import com.gliffy.restunit.*;

/** Returns a derived test that checks that a <tt>HEAD</tt> acts just like a <tt>GET</tt>.
 */
public class HeadDeriver implements Derivable
{
    /** Returns a test identicial to the passed-in test, except that does a HEAD request and expects no return body.
     * This will return null if the test is not a {@link com.gliffy.restunit.GetTest} or does not return true from
     * {@link com.gliffy.restunit.GetTest#getRespondsToHead()}.
     */
    public RestTest derive(RestTest test)
    {
        if (test instanceof GetTest)
        {
            GetTest getTest = (GetTest)test;
            if (getTest.getRespondsToHead())
            {
                GetTest head = (GetTest)test.clone();
                head.setRespondsToHead(false);
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
