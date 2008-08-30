package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** An test of a request that has a body */
public class BodyTest extends RestTest
{
    private String itsContentType;
    public String getContentType() { return itsContentType; }
    public void setContentType(String i) { itsContentType = i; }

    private byte[] itsBody;
    public byte[] getBody() { return itsBody; }
    public void setBody(byte[] i) { itsBody = i; }
}
