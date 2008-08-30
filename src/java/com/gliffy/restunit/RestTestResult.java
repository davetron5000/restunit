package com.gliffy.restunit;

/** The results of running a test */
public enum RestTestResult
{
    /** The test succeeded */
    PASS,
    /** The test failed */
    FAIL,
    /** There was an exception unrelated to the test while executing the test */
    EXCEPTION,
    /** The test was not run */
    SKIP;

    public String getPastTense()
    {
        switch (this)
        {
            case PASS :
                return "passed";
            case FAIL : 
                return "failed";
            case EXCEPTION :
                return "generated exception";
            case SKIP :
                return "skipped";
            default :
                return null;
        }
        return null;
    }
};
