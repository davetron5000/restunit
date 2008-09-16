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
        RestTest test = new RestTest();
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

        List<ExecutionResult> results = itsRestUnit.runTest(test);
        for (ExecutionResult result: results)
        {
            assert result.getResult() == Result.PASS : "A test didn't pass " + test.toString() + " got: " + result.toString();
        }
    }
}
