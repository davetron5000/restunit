// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** A test for a REST endpoint.
 * This is essentially an ordered list of {@link RestCall} objects that, when run in order, 
 * constitute a test of a REST service endpoint/resource.
 */
public class RestTest implements Serializable
{
    private String itsDefaultURL;
    private String itsName;

    private List<RestCall> itsCalls;

    /** Create a RestTest. */
    public RestTest()
    {
        itsCalls = new ArrayList<RestCall>();
    }

    /** This is the URL used by default for calls that are part of this test.
     * Those calls can specify a URL via {@link RestCall#getURL()}, which would override
     * this when the test is executed.
     * @param url the url, relative or absolute, depending on the test context.
     */
    public void setDefaultURL(String url) 
    {
        itsDefaultURL = url; 
    }

    public String getDefaultURL() 
    {
        return itsDefaultURL; 
    }

    /** Adds a call to this test.  It will be executed after the previously added call.
     * @param call the call to add
     */
    public void addCall(RestCall call)
    {
        itsCalls.add(call);
    }

    /** returns the list of calls.  
     * @return an ordered list of RestCall objects.  This is not a modifibale list.
     */
    public List<RestCall> getCalls() 
    { 
        return Collections.unmodifiableList(itsCalls); 
    }

    /** Sets a name to describe the test.
     * @param name the name of this test, to help identify it in a report
     */
    public void setName(String name) 
    {
        itsName = name; 
    }

    public String getName() 
    {
        return itsName; 
    }
}
