// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

import java.util.*;

import org.testng.annotations.*;

/** Allows using RESTUnit inside TestNG.
 * To use this, subclass and implement {@link #getTests()} (you will also need to ensure
 * that {@link #setRestUnit(RestUnit)} is called).  Each will be called by 
 * TestNG at runtime, assuming your class is available to TestNG.
 * {@link #getTests()} is used by {@link #getTestsDataProvider()} to tell
 * TestNG which tests to run.  These tests are run via {@link #runTest(RestTest)}.
 *
 * For example:
 * <pre>
 * public class MyTests etends TestNGRestUnit
 * {
 *     public MyTests()
 *     {
 *         super();
 *         setRestUnit(new RestUnit());
 *         getRestUnit().getExecutor().setHttp(new JavaHttp());
 *     }
 *
 *     public Set&lt;RestTest&gt;getTests()
 *     {
 *         Set&lt;RestTest&gt; tests = new HashSet&lt;RestTest&gt;();
 *         tests.add(createSomeTest());
 *         tests.add(createSomeOtherTest());
 *         tests.add(createYetAnotherTest());
 *         return tests;
 *     }
 * }
 * </pre>
 * This will result in three tests being run, one for each of the tests created in <tt>getTests</tt>.
 */
public abstract class TestNGRestUnit
{
    private RestUnit itsRestUnit;
    private ResultFormatter itsFormatter = new ResultFormatter();

    /** Returns the tests you want run.
     * @return a set of RestTest objects that will be run via RestUnit by way of TestNG
     */
    protected abstract Set<RestTest> getTests();

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
    @Test(dataProvider="tests")
    public void runTest(RestTest test)
    {
        RestTestResult result = getRestUnit().runTest(test);
        assert result.getSuccess() : "Test " + test.getName() + " failed:\n" + getResultFormatter().format(result);
    }

    /** DataProvider for translating the tests returned by {@link #getTests()} into something
     * TestNG can use.
     * @return TestNG's parameter list of tests.  Only one column with one row for each test.
     */
    @DataProvider(name="tests")
    public final Object[][] getTestsDataProvider()
    {
        Object[][] tests = new Object[getTests().size()][1];
        int i = 0;
        for (RestTest test: getTests())
        {
            tests[i][0] = test;
            i++;
        }
        return tests;
    }
}
