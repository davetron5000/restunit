package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** An test of a request that has a body */
public class BodyTest extends RestTest
{
    public String itsContentType;
    public String getContentType() { return itsContentType; }
    public void setContentType(String i) { itsContentType = i; }

    public byte[] itsBody;
    public byte[] getBody() { return itsBody; }
    public void setBody(byte[] i) { itsBody = i; }
}
