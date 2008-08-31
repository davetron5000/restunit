package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** An test of a GET request. */
public class GetTest extends RestTest 
{
    /** true if this test should respond to HEAD requests. */
    private boolean itsRespondsToHead;

    /** True if conditional gets based upon date are supported. */
    private boolean itsRespondsToIfModified;

    /** True if conditional gets based upon etag are supported. */
    private boolean itsRespondsToIfNoneMatch;

    public boolean getRespondsToHead() 
    {
        return itsRespondsToHead; 
    }
    public void setRespondsToHead(boolean i) 
    {
        itsRespondsToHead = i; 
    }

    public boolean getRespondsToIfModified() 
    {
        return itsRespondsToIfModified; 
    }
    public void setRespondsToIfModified(boolean i) 
    {
        itsRespondsToIfModified = i; 
    }

    public boolean getRespondsToIfNoneMatch() 
    {
        return itsRespondsToIfNoneMatch; 
    }
    public void setRespondsToIfNoneMatch(boolean i) 
    {
        itsRespondsToIfNoneMatch = i; 
    }

}
