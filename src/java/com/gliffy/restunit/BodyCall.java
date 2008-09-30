// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

import java.io.*;
import java.util.*;

import com.gliffy.restunit.http.*;

/** A call of a request that has a body. */
public class BodyCall extends RestCall
{
    /** The MIME type of the body. */
    private String itsContentType;

    /** The body to send in the call, as bytes. */
    private byte[] itsBody;

    public String getContentType() 
    {
        return itsContentType; 
    }
    public void setContentType(String i) 
    {
        itsContentType = i; 
    }

    /** Sets the content type via a ContentType object, with optional character encoding.
     * @param type the ContentType
     */
    public void setContentType(ContentType type)
    {
        setContentType(type.toString());
    }

    public byte[] getBody() 
    {
        return itsBody; 
    }
    public void setBody(byte[] i) 
    {
        itsBody = i; 
    }
}
