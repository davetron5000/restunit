package com.gliffy.test.restunit;

import java.util.*;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

import com.gliffy.test.restunit.http.*;

import org.testng.*;
import org.testng.annotations.*;

import static org.easymock.classextension.EasyMock.*;

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
    }

    @DataProvider(name = "getData")
    public Object[][] getMethods() 
    {
        Object returnMe[][] = new Object[itsService.keySet().size()][4];
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

    @Test (dependsOnGroups = { "mockhttp", "deriver", "executor", "clone", "comparator", "runtest" }, dataProvider = "getData")
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
