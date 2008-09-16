// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

/** The results of running a test. */
public enum Result
{

    /** The test succeeded. */
    PASS,
    /** The test failed. */
    FAIL,
    /** The test was not executed. */
    SKIP, 
    /** There was an exception unrelated to the test while executing the test. */
    EXCEPTION;

    /** Gets a description of this result in past-tense.
     * This facilitates the creation of sentences like "test 64 failed".
     * @return a string with the item in past-tense.
     */
    public String getPastTense()
    {
        switch (this)
        {
            case PASS :
                return "passed";
            case FAIL : 
                return "failed";
            case SKIP : 
                return "skipped";
            case EXCEPTION :
                return "generated exception";
            default :
                return null;
        }
    }
}
