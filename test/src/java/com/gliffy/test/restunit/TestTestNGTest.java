package com.gliffy.test.restunit;

import java.util.*;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

import com.gliffy.test.restunit.http.*;

import org.testng.*;
import org.testng.annotations.*;

import static org.easymock.classextension.EasyMock.*;

@Test (dependsOnGroups = { "mockhttp", "executor", "clone", "comparator" }, groups= { "restUnit" })
public class TestTestNGTest extends TestNGRestUnit
{
    private Map<String,String> itsService;
    protected RestUnit itsRestUnit;

    @BeforeSuite
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
        Http http = new MockHttp(itsService);
        RestUnit restUnit = new RestUnit();
        restUnit.getExecutor().setHttp(http);
        restUnit.getExecutor().setBaseURL("http://www.google.com");
        super.setRestUnit(restUnit);
    }

    protected Set<RestTest> getTests()
    {
        Set<RestTest> testsToReturn = new HashSet<RestTest>();

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

        testsToReturn.add(restTest);
        testsToReturn.addAll(getTestsForEntireDataSet());

        return testsToReturn;
    }

    public Set<RestTest> getTestsForEntireDataSet() 
    {
        Set<RestTest> testData = new HashSet<RestTest>();
        for (String url: itsService.keySet())
        {
            String data = itsService.get(url);
            if (data == null)
            {
                testData.add(getSimple(url,data,"PUT",201));
                testData.add(getSimple(url,data,"POST",201));
                testData.add(getSimple(url,data,"DELETE",200));
            }
            else
            {
                testData.add(getSimple(url,data,"GET",200));
            }
        }
        return testData;
    }

    private RestTest getSimple(String url, String body, String method, int status)
    {
        RestTest restTest = new RestTest();
        restTest.setDefaultURL(url);
        RestCall test = (method.equals("PUT") || method.equals("POST")) ? new BodyCall() : new RestCall();
        test.setMethod(method);
        test.setName("Call of " + url);
        if (body != null)
        {
            BodyResponse response = new BodyResponse();
            if (body.startsWith("<"))
                response.setContentType(new ContentType("text/xml","UTF-8"));
            else
                response.setContentType(new ContentType("text/plain","UTF-8"));
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
            ((BodyCall)test).setContentType(new ContentType("text/plain","UTF-8"));
        }

        restTest.addCall(test);
        return restTest;
    }
}
