// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.comparator;

import java.io.*;

/** The results of a comparison.  A simple boolean won't work, since we also need an explanation that might be useful
 * in the test results.
 */
public class ComparisonResult implements Serializable
{
    /** Singleton matching result, to avoid object creation. */
    public static final ComparisonResult MATCHES = new ComparisonResult(true,null);

    private boolean itsMatches;
    private String itsExplanation;

    /** Create a result.
     * @param matches if true, this object indicates the comparison succeeded and the results matched.
     * @param explanation if matches was false, this provides an explanation to include in the test results.
     */
    public ComparisonResult(boolean matches, String explanation)
    {
        itsMatches = matches;
        itsExplanation = explanation;
    }

    public boolean getMatches() 
    { 
        return itsMatches; 
    }

    public String getExplanation() 
    { 
        return itsExplanation; 
    }
}
