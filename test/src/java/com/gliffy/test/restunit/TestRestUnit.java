package com.gliffy.test.restunit;

import java.util.*;

import com.gliffy.restunit.*;
import com.gliffy.restunit.deriver.*;
import com.gliffy.restunit.http.*;

import com.gliffy.test.restunit.http.*;

import org.testng.*;
import org.testng.annotations.*;

import static org.easymock.classextension.EasyMock.*;

//@Test (dependsOnGroups = { "mockhttp", "deriver", "executor", "clone", "comparator", "runtest" })
public class TestRestUnit
{
    private Map<String,String> itsService;
    private MockHttp itsHttp;
    private RestUnit itsRestUnit;

    @BeforeTest
    public void setUp()
    {
        itsService = new HashMap<String,String>();

        itsService.put("/accounts/Initech","Stuff about Initech");
        itsService.put("/accounts/BurnsODyne","Stuff about BurnsODyne");
        itsService.put("/accounts/PlanetExpress",null);
        itsService.put("/accounts/BurnsODyne/diagrams","List of diagrams goes here");
        itsService.put("/accounts/BurnsODyne/diagrams/1000005","<diagram id='1000005'>The diagram</diagram>");
        itsService.put("/accounts/BurnsODyne/diagrams/1000004","<diagram id='1000004'>The diagram</diagram>");
        itsService.put("/accounts/BurnsODyne/diagrams/1000003",null);
        itsService.put("/accounts/BurnsODyne/users/monty","This is monty's info");
        itsService.put("/accounts/BurnsODyne/users/smithers","This is smithers's info");
        itsService.put("/accounts/BurnsODyne/users/homer","This is homer's info");
        itsService.put("/accounts/BurnsODyne/users/moe",null);
        itsService.put("/accounts/BurnsODyne/users/lisa",null);

        itsHttp = new MockHttp(itsService);

        Executor executor = new Executor();
        executor.setHttp(itsHttp);
        executor.setBaseURL("http://www.google.com");
        itsRestUnit = new RestUnit(executor);
        itsRestUnit.addDeriver(new HeadDeriver());
        itsRestUnit.addDeriver(new ConditionalGetLastModifiedDeriver());
        itsRestUnit.addDeriver(new ConditionalGetETagDeriver());
    }

    @Test
    public void testDependentsSimple() { testDependents(false); }
    @Test
    public void testDependentsComplex() { testDependents(true); }

    /** tests dependents
     * @param full if true, the GET test will say that should respond to HEAD, last-Modified and ETag.  If false, it won't
     */
    private void testDependents(boolean full)
    {
        BodyTest test = new BodyTest();
        String url = "/accounts/BurnsODyne/users/lisa";
        test.setURL(url);
        test.setMethod("PUT");
        test.setName("Test of putting user lisa");
        String body = "<user><name>lisa</name><email type=\"work\">lisa@simpsons.org</email></user>";
        String contentType = "text/xml";
        test.setContentType(contentType);
        test.setBody(body.getBytes());
        RestTestResponse response = new RestTestResponse();
        response.setStatusCode(201);
        test.setResponse(response);

        GetTest getTest = new GetTest();
        getTest.setURL(url);
        getTest.setMethod("GET");
        getTest.setName("Getting " + url);
        getTest.setRespondsToHead(full);
        getTest.setRespondsToIfModified(full);
        getTest.setRespondsToIfNoneMatch(full);
        BodyResponse getResponse = new BodyResponse();
        getResponse.setStatusCode(200);
        getResponse.setContentType(contentType);
        getResponse.setBody(body.getBytes());
        getTest.setResponse(getResponse);

        test.getDependentTests().add(getTest);

        RestTest deleteTest = new RestTest();
        deleteTest.setURL(url);
        deleteTest.setMethod("DELETE");
        deleteTest.setName("Delete of " + url);
        RestTestResponse deleteResponse = new RestTestResponse();
        deleteResponse.setStatusCode(200);
        deleteTest.setResponse(deleteResponse);

        getTest.getDependentTests().add(deleteTest);

        List<ExecutionResult> results = itsRestUnit.runTest(test);

        for (ExecutionResult result: results)
        {
            assert result.getResult() == Result.PASS : "A test didn't pass " + result.getTest().toString() + " got: " + result.toString();
        }
        int numTests = 3;
        int derivedTests = full ? 5 : 0;
        numTests += derivedTests;
        assert results.size() == numTests : "Expected " + numTests + " total tests to have been run (our original and " + (numTests - 1) + "  derived).  Instead got " + results.size();
    }

    @DataProvider(name = "getBodyGetData")
    public Object[][] getGetTestsOnly() 
    {
        List<Object[]> testData = new ArrayList<Object[]>();
        for (String url: itsService.keySet())
        {
            Object oneTest[] = new Object[2];
            String data = itsService.get(url);
            oneTest[0] = url;
            oneTest[1] = data;
            if (data != null)
            {
                testData.add(oneTest);
            }
        }
        return testData.toArray(new Object[0][0]);
    }

    @DataProvider(name = "getData")
    public Object[][] getTestsForEntireDataSet() 
    {
        List<Object[]> testData = new ArrayList<Object[]>();
        for (String url: itsService.keySet())
        {
            Object oneTest[] = new Object[4];
            String data = itsService.get(url);
            oneTest[0] = url;
            oneTest[1] = data;
            if (data == null)
            {
                oneTest[2] = "PUT";
                oneTest[3] = 201;
                testData.add(oneTest);

                oneTest = new Object[4];
                oneTest[0] = url;
                oneTest[1] = data;
                oneTest[2] = "POST";
                oneTest[3] = 201;
                testData.add(oneTest);

                oneTest = new Object[4];
                oneTest[0] = url;
                oneTest[1] = data;
                oneTest[2] = "DELETE";
                oneTest[3] = 200;
                testData.add(oneTest);
            }
            else
            {
                oneTest[2] = "GET";
                oneTest[3] = 200;
                testData.add(oneTest);
            }
        }
        return testData.toArray(new Object[0][0]);
    }

    /** This tests the last-mod headers that should be sent with GET requests */
    @Test (dataProvider = "getBodyGetData")
    public void testLastModHeaders(String url, String body)
    {
        GetTest test = new GetTest();
        test.setURL(url);
        test.setMethod("GET");
        test.setName("Test of " + url);
        test.setRespondsToHead(true);
        test.setRespondsToIfModified(true);
        test.setRespondsToIfNoneMatch(true);
        BodyResponse response = new BodyResponse();
        if (body.startsWith("<"))
            response.setContentType("text/xml");
        else
            response.setContentType("text/plain");
        response.setBody(body.getBytes());
        response.setStatusCode(200);
        response.getRequiredHeaders().add("Last-Modified");
        response.getRequiredHeaders().add("ETag");
        test.setResponse(response);

        List<ExecutionResult> results = itsRestUnit.runTest(test);
        for (ExecutionResult result: results)
        {
            assert result.getResult() == Result.PASS : "A test didn't pass " + result.getTest().toString() + " got: " + result.toString();
        }
        int numTests = 6;
        assert results.size() == numTests : "Expected " + numTests + " total tests to have been run (our original and 3 derived).  Instead got " + results.size();
    }

    /** This simply access the URL from our fake service and sees if a test will pass */
    @Test (dataProvider = "getData")
    public void testSimple(String url, String body, String method, int status)
    {
        RestTest test = (method.equals("PUT") || method.equals("POST")) ? new BodyTest() : new RestTest();
        test.setURL(url);
        test.setMethod(method);
        test.setName("Test of " + url);
        if (body != null)
        {
            BodyResponse response = new BodyResponse();
            if (body.startsWith("<"))
                response.setContentType("text/xml");
            else
                response.setContentType("text/plain");
            response.setBody(body.getBytes());
            response.setStatusCode(status);
            test.setResponse(response);
        }
        else
        {
            RestTestResponse response = new RestTestResponse();
            response.setStatusCode(status);
            test.setResponse(response);
        }
        if (test instanceof BodyTest)
        {
            ((BodyTest)test).setBody(new byte[0]);
            ((BodyTest)test).setContentType("text/plain");
        }

        List<ExecutionResult> results = itsRestUnit.runTest(test);
        for (ExecutionResult result: results)
        {
            assert result.getResult() == Result.PASS : "A test didn't pass " + test.toString() + " got: " + result.toString();
        }
    }
}
