package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** An test of a request that has a body. */
public class BodyTest extends RestTest
{
    /** The MIME type of the body. */
    private String itsContentType;

    /** The body to send in the test, as bytes. */
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
