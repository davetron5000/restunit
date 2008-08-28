package com.gliffy.restunit;

/** The results of running a test */
public enum RestTestResult
{
    /** The test succeeded */
    SUCCESS,
    /** The test failed */
    FAILURE,
    /** There was an exception unrelated to the test while executing the test */
    EXCEPTION,
    /** The test was not run */
    SKIPPED
};
