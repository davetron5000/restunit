// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

import java.io.*;
import java.util.*;

import com.gliffy.restunit.http.*;

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

    /** Sets the content type ensuring that the specified encoding is includes.
     * @param contentType a ContentType describing the content type and optional character encoding
     */
    public void setContentType(ContentType contentType)
    {
        setContentType(contentType.toString());
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
