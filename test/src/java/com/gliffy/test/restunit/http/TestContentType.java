package com.gliffy.test.restunit.http;

import java.util.*;

import com.gliffy.restunit.http.*;
import com.gliffy.restunit.*;

import org.testng.*;
import org.testng.annotations.*;

import org.apache.commons.logging.*;

public class TestContentType
{
    @Test(dataProvider = "simpleMimeTypes")
    public void testSimple(String simpleMimeType, String normalizedMimeType)
    {
        ContentType type = ContentType.getContentType(simpleMimeType);
        String expected = normalizedMimeType;
        if (expected == null)
            expected = simpleMimeType;

        assert type.getMimeType().equals(expected) : "Expected '" + expected + "', but got '" + type.getMimeType() + "'";
    }

    @Test(dataProvider = "mimeTypesWithEncodings")
    public void testWithEncodings(String string, String expectedType, String expectedEncoding)
    {
        ContentType type = ContentType.getContentType(string);

        assert type.getMimeType().equals(expectedType) : "Expected '" + expectedType + "', but got '" + type.getMimeType() + "'";
        String encodingGot = type.getEncoding();
        if (encodingGot == null)
            assert null == expectedEncoding : "Expected '" + expectedEncoding + "', but got '" + type.getEncoding() + "'";
        else
            assert encodingGot.equals(expectedEncoding) : "Expected '" + expectedEncoding + "', but got '" + type.getEncoding() + "'";
    }

    @DataProvider(name="mimeTypesWithEncodings")
    public Object[][] getMimeTypesWithEncodings()
    {
        return new Object[][] {
            { "text/xml ; charset=UTF-8", "text/xml", "UTF-8" },
            { "text/html;charset=latin1", "text/html", "latin1" },
            { "   tExT/XML ; blah=ISO", "text/xml", null },
            { "   tExT/XML ; charset = ISO", "text/xml", "ISO" },
            { "   tExT/XML ; charset = ISO           ", "text/xml", "ISO" },
        };
    }

    @DataProvider(name="simpleMimeTypes")
    public Object[][] getSimpleMimeTypes()
    {
        return new Object[][] {
            { "text/xml", null },
            { "text/html", null },
            { "image/png", null },
            { "image/jpeg", null },
            { "tExT/XML", "text/xml" },
            { "text/htML    ", "text/html" },
            { "   iMAge/png", "image/png" },
            { "   image/jpeg  ", "image/jpeg" },
        };
    }
}

