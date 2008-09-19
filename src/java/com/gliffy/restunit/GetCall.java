// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** An call to a GET endpoint. */
public class GetCall extends RestCall 
{
    /** true if this call should respond to HEAD requests. */
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
