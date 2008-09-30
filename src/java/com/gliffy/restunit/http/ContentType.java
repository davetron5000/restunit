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
    private Pattern itsCharsetPattern = Pattern.compile("^(\\s*[^;]*)\\s*;\\s*([^=]*)\\s*=\\s*(.*)\\s*$");
    private String itsMimeType;
    private String itsEncoding;
    /** Create the ContentType based upon the string received from an HTTP header or other location.
     * @param string the string that may contain a content-type
     */
    public ContentType(String string)
    {
        if (string != null)
        {
            Matcher matcher = itsCharsetPattern.matcher(string);
            if (matcher.matches())
            {
                int group = 1;
                String mimeType = matcher.group(group++).toLowerCase().trim();
                String modifier = matcher.group(group++).toLowerCase().trim();
                String value = matcher.group(group++).trim();
                itsMimeType = mimeType;
                if (modifier.equals(CHARSET))
                    itsEncoding = value;
            }
            else
            {
                itsMimeType = string.toLowerCase().trim();
            }
        }
    }

    /** Returns the mime-type, normalized to lower-case and trimmed of whitespace.
     * @return a normalized version of the mimetype or null if it couldn't be determined
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
