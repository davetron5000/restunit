package com.gliffy.test.restunit;

import java.util.*;

import com.gliffy.restunit.*;
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
        itsRestUnit = new RestUnit();
        itsRestUnit.setExecutor(executor);
    }

    @Test
    public void testFunctional()
    {
        String url = "/accounts/BurnsODyne/users/lisa";
        RestTest restTest = new RestTest();
        restTest.setName("Basic Functional Test");
        restTest.setDefaultURL(url);

        BodyCall test = new BodyCall();
        test.setMethod("PUT");
        String body = "<user><name>lisa</name><email type=\"work\">lisa@simpsons.org</email></user>";
        String contentType = "text/xml";
        test.setContentType(contentType);
        test.setBody(body.getBytes());
        RestCallResponse response = new RestCallResponse();
        response.setStatusCode(201);
        test.setResponse(response);

        restTest.addCall(test);

        GetCall getCall = new GetCall();
        getCall.setMethod("GET");
        BodyResponse getResponse = new BodyResponse();
        getResponse.setStatusCode(200);
        getResponse.setContentType(contentType);
        getResponse.setBody(body.getBytes());
        getCall.setResponse(getResponse);

        restTest.addCall(getCall);

        RestCall deleteCall = new RestCall();
        deleteCall.setMethod("DELETE");
        RestCallResponse deleteResponse = new RestCallResponse();
        deleteResponse.setStatusCode(200);
        deleteCall.setResponse(deleteResponse);

        restTest.addCall(deleteCall);

        getCall = new GetCall();
        getCall.setMethod("GET");
        RestCallResponse errorResponse = new RestCallResponse();
        errorResponse.setStatusCode(404);
        getCall.setResponse(errorResponse);

        restTest.addCall(getCall);

        RestTestResult result = itsRestUnit.runTest(restTest);

        for (RestCallResult callResult: result.getDetailedResults())
        {
            assert callResult.getResult() == Result.PASS : "Got " + callResult.getResult() + " but expected a PASS (" + callResult.toString() + ")";
        }
        assert result.getSuccess() == true : "Expected our test to be marked successful";
    }

    /*
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
    */

    /** This simply access the URL from our fake service and sees if a test will pass */
    /*
    @Test (dataProvider = "getData")
    public void testSimple(String url, String body, String method, int status)
    {
        RestCall test = (method.equals("PUT") || method.equals("POST")) ? new BodyCall() : new RestCall();
        test.setURL(url);
        test.setMethod(method);
        test.setName("Call of " + url);
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
            RestCallResponse response = new RestCallResponse();
            response.setStatusCode(status);
            test.setResponse(response);
        }
        if (test instanceof BodyCall)
        {
            ((BodyCall)test).setBody(new byte[0]);
            ((BodyCall)test).setContentType("text/plain");
        }

        List<RestCallResult> results = itsRestUnit.runCall(test);
        for (RestCallResult result: results)
        {
            assert result.getResult() == Result.PASS : "A test didn't pass " + test.toString() + " got: " + result.toString();
        }
    }
    */
}
