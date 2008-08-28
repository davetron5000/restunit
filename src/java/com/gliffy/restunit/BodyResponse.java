package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** A response that has a body */
public class BodyResponse extends RestTestResponse
{
    private String itsContentType;
    public String getContentType() { return itsContentType; }
    public void setContentType(String i) { itsContentType = i; }

    private byte[] itsBody;
    public byte[] getBody() { return itsBody; }
    public void setBody(byte[] i) { itsBody = i; }
}
