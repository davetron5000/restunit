// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.http;

import java.util.regex.*;

/** Handles accepting a string content type and exposing the encoded mime type and character encoding. */
public class ContentType
{
    private static final String CHARSET= "charset";
    // $1 - mime type
    // $2 - modifier (e.g. "charset")
    // $3 - value of the modifier (e.g. "UTF-8")
    private static final Pattern theCharsetPattern = Pattern.compile("^(\\s*[^;]*)\\s*;\\s*([^=]*)\\s*=\\s*(.*)\\s*$");

    private String itsMimeType;
    private String itsEncoding;

    /** Create the ContentType based upon the string received from an HTTP header or other location.
     * @param mimeType the mime type, should not be null
     * @param encoding the character encoding, or null if it wasn't provided
     */
    private ContentType(String mimeType, String encoding)
    {
        if (mimeType == null)
            throw new IllegalArgumentException("mimeType may not be null to ContentType(String,String)");
        itsMimeType = mimeType;
        itsEncoding = encoding;
    } 

    /** Returns a content type based on the "Content-Type" header passed in.
     * @param string the result of getting the "Content-Type" header; may be null
     * @return a ContentType object if the string could be parsed, otherwise null
     */
    public static ContentType getContentType(String string)
    {
        if (string != null)
        {
            Matcher matcher = theCharsetPattern.matcher(string);
            if (matcher.matches())
            {
                int group = 1;
                String mimeType = matcher.group(group++).toLowerCase().trim();
                String modifier = matcher.group(group++).toLowerCase().trim();
                String value = matcher.group(group++).trim();
                if (modifier.equals(CHARSET))
                    return new ContentType(mimeType,value);
                else
                    return new ContentType(mimeType,null);
            }
            else
            {
                return new ContentType(string.toLowerCase().trim(),null);
            }
        }
        else
        {
            return null;
        }
    }

    /** Returns the mime-type, normalized to lower-case and trimmed of whitespace.
     * @return a normalized version of the mimetype; never null
     */
    public String getMimeType()
    {
        return itsMimeType;
    }

    /** Returns the character encoding, or null if it wasn't present.
     * @return the character encoding that was part of the string
     */
    public String getEncoding()
    {
        return itsEncoding;
    }
}
