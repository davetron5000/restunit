// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** A response that has a body. */
public class BodyResponse extends RestCallResponse
{
    /** The MIME type of the body content. */
    private String itsContentType;

    /** The body content, as bytes. */
    private byte[] itsBody;

    public String getContentType() 
    {
        return itsContentType; 
    }
    public void setContentType(String i) 
    {
        itsContentType = i; 
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
