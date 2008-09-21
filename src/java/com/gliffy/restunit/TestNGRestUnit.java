// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

/** Allows using RESTUnit inside TestNG.
 * The method {@link #runTest(RestTest)} can be called from TestNG; it will examine the results of
 * the test execution and use assert to fail the test.  You could write a TestNG test like so:
 * <pre>
 * public class TestMyService
 * {
 *    private TestNGRestUnit unit;
 *
 *    @BeforeTest
 *    public void setUp()
 *    {
 *      unit = new TestNGRestUnit();
 *      unit.setRestUnit(new RestUnit());
 *    }
 *    
 *    public void testSomeGetMethod()
 *    {
 *       RestTest test = new RestTest();
 *       test.setName("Test GET /");
 *       test.setDefaultURL("http://www.google.com");
 *
 *       RestCall call = new RestCall();
 *       call.setMethod("HEAD");
 *
 *       test.addCall(call);
 *
 *       unit.runTest(test);
 *    }
 * }
 * </pre>
 */
public class TestNGRestUnit
{
    private RestUnit itsRestUnit;
    private ResultFormatter itsFormatter = new ResultFormatter();

    /** sets the RestUnit instance to use for this test.  Typically, you would 
     * call this in your set up method.
     * @param restUnit the instance.
     */
    public void setRestUnit(RestUnit restUnit)
    {
        if (restUnit == null)
            throw new IllegalArgumentException("RestUnit instance may not be null!");
        itsRestUnit = restUnit;
    }

    public RestUnit getRestUnit() 
    {
        return itsRestUnit;
    }

    public void setResultFormatter(ResultFormatter restUnit)
    {
        itsFormatter = restUnit;
    }

    public ResultFormatter getResultFormatter() 
    {
        return itsFormatter;
    }

    /** Runs the test and does an <tt>assert</tt> if the test fails.  This can be called directly from a TestNG test.
     * You can also override this method in a subclass and attach a data provider to it
     * @param test the test to run
     */
    public void runTest(RestTest test)
    {
        RestTestResult result = getRestUnit().runTest(test);
        assert result.getSuccess() : "Test " + test.getName() + " failed:\n" + getResultFormatter().format(result);
    }
}
