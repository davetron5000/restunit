package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** An test of a GET request */
public class GetTest extends RestTest
{
    private boolean itsRespondsToHead;
    /** If true, A HEAD request for this resource should act as HEAD is supposed to */
    public boolean getRespondsToHead() { return itsRespondsToHead; }
    public void setRespondsToHead(boolean i) { itsRespondsToHead = i; }

    private boolean itsRespondsToIfModified;
    /** If true, this test can be run as a conditional GET based on dates.  This means the resource should respond
     * with a <tt>Last-Modified</tt> header whose value, when used in the <tt>If-Modified-Since</tt> header will
     * cause the server to send a 302, instead of a 200.
     */
    public boolean getRespondsToIfModified() { return itsRespondsToIfModified; }
    public void setRespondsToIfModified(boolean i) { itsRespondsToIfModified = i; }

    private boolean itsRespondsToIfNoneMatch;
    /** If true, this resource respondes to conditional gets based on eTags */
    public boolean getRespondsToIfNoneMatch() { return itsRespondsToIfNoneMatch; }
    public void setRespondsToIfNoneMatch(boolean i) { itsRespondsToIfNoneMatch = i; }

}
