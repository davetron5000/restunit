package com.gliffy.test.restunit;

import java.util.*;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

import com.gliffy.test.restunit.http.*;

import org.testng.*;
import org.testng.annotations.*;

import static org.easymock.classextension.EasyMock.*;

//@Test (dependsOnGroups = { "mockhttp", "deriver", "executor", "clone", "comparator", "runtest" })
public class TestJavaHttp
{
    protected RestUnit itsRestUnit;

    @BeforeTest
    public void setUp()
    {
        itsRestUnit = new RestUnit();
        itsRestUnit.getExecutor().setHttp(new JavaHttp());
        itsRestUnit.getExecutor().setBaseURL("http://www.google.com");
    }

    public void testFunctional(boolean badGet, boolean exception)
    {
        String url = "";
        RestTest restTest = new RestTest();
        restTest.setName("Basic Test of JavaHttp");
        restTest.setDefaultURL(url);

        GetCall getCall = new GetCall();
        getCall.setMethod("GET");
        RestCallResponse getResponse = new RestCallResponse();
        getResponse.setStatusCode(200);
        getCall.setResponse(getResponse);

        restTest.addCall(getCall);

        assertTestsPass(restTest);
    }

    protected void assertTestsPass(RestTest restTest)
    {
        RestTestResult result = itsRestUnit.runTest(restTest);

        for (RestCallResult callResult: result.getDetailedResults())
        {
            assert callResult.getResult() == Result.PASS : "Got " + callResult.getResult() + " but expected a PASS (" + callResult.toString() + ")";
        }
        assert result.getSuccess() == true : "Expected our test to be marked successful";
    }

}
