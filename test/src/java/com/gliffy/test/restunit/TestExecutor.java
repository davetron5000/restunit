package com.gliffy.test.restunit;

import java.util.*;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

import org.testng.*;
import org.testng.annotations.*;

import static org.easymock.classextension.EasyMock.*;

public class TestExecutor
{
    @Test
    public void testGet()
    {
        testBasicMethod("GET");
    }
    @Test
    public void testHead()
    {
        testBasicMethod("HEAD");
    }
    @Test
    public void testPut()
    {
        testBasicMethod("PUT");
    }
    @Test
    public void testPost()
    {
        testBasicMethod("POST");
    }
    @Test
    public void testDelete()
    {
        testBasicMethod("DELETE");
    }

    @Test
    public void testBadSetup()
    {
        Executor executor = new Executor();
        executor.setBaseURL("http://www.google.com/");

        try
        {
            executor.execute(TestFactory.getRandomTest());
            assert false : "Expected an exception when HTTP wasn't configured";
        }
        catch (IllegalStateException e)
        {
            // successful test
        }
    }

    /** Runs a basic test for a method to see that it gets called.
     * @param method the method to use
     */
    private void testBasicMethod(String method)
    {
        Http mockHttp = getMockHttp(method.toUpperCase());

        replay(mockHttp);

        RestTest test = TestFactory.getRandomTest();
        test.setMethod(method.toUpperCase());
        test.getResponse().setStatusCode(200);
        clearHeaderRequirements(test);

        Executor executor = new Executor(mockHttp);
        executor.setBaseURL("http://www.google.com/");
        ExecutionResult result = executor.execute(test);

        assert result.getResult() == Result.PASS : "Got " + result.getResult() + " for " + result.toString() + " instead of " + Result.PASS.toString();

        verify(mockHttp);
    }

    private void clearHeaderRequirements(RestTest test)
    {
        test.getResponse().setHeaders(new HashMap<String,String>());
        test.getResponse().setBannedHeaders(new HashSet<String>());
        test.getResponse().setRequiredHeaders(new HashSet<String>());
    }

    /** This tests that the HttpRequest created by the Executor to give to Http was done using the actual
     * test information; the Executor must interrogate the test to find out what to request
     */
    @Test(dependsOnMethods = { "testGet" } )
    public void testRequestCreation()
    {
        RestTest mockTest = createMock(RestTest.class);
        RestTest randomTest = TestFactory.getRandomGetTest();

        expect(mockTest.getMethod()).andReturn("GET");
        expect(mockTest.getURL()).andReturn(randomTest.getURL());
        expect(mockTest.getParameters()).andReturn(randomTest.getParameters());
        expect(mockTest.getHeaders()).andReturn(randomTest.getHeaders());
        expect(mockTest.getResponse()).andReturn(randomTest.getResponse());

        Http mockHttp = getMockHttp("GET");
        replay (mockHttp);
        replay (mockTest);

        Executor executor =  new Executor(mockHttp);
        executor.setBaseURL("http://www.google.com/");
        executor.execute(mockTest);

        verify(mockHttp);
        verify(mockTest);
    }

    /** This tests that the results returned from the MockHTTP service ended up in the right place in the test results
     */
    @Test
    public void testResultPopulationSuccess()
    {
        RestTest fakeTest = TestFactory.getRandomGetTest();
        fakeTest.setMethod("GET");
        fakeTest.getResponse().setStatusCode(200);

        HttpResponse response = createMatchingResponse(fakeTest.getResponse());
        Http mockHttp = getMockHttp("GET",response);
        Executor executor = new Executor(mockHttp);
        executor.setBaseURL("http://www.google.com");
        replay(mockHttp);
        ExecutionResult result = executor.execute(fakeTest);
        verify(mockHttp);

        assert result.getResult() == Result.PASS : "Expected test to pass " + result.toString();
    }

    @Test
    public void testResultPopulationFailureHeadersBanned()
    {
        RestTest fakeTest = createTestForPopulationTest();

        HttpResponse response = createMatchingResponse(fakeTest.getResponse());

        // Make it so the response omits a header the test requires
        response.getHeaders().put(fakeTest.getResponse().getBannedHeaders().iterator().next(),"BLAH");
        testResultPopulation(fakeTest,response,Result.FAIL,"Expected test to fail, since we put a header that was banned in the response");
    }

    @Test
    public void testResultPopulationFailureDifferentBody()
    {
        RestTest fakeTest = createTestForPopulationTest();

        HttpResponse response = createMatchingResponse(fakeTest.getResponse());

        byte copy[] = new byte[response.getBody().length];
        for (int i=0;i<response.getBody().length; i++)
        {
            copy[i] = (byte)(255 - response.getBody()[i]);
        }
        response.setBody(copy);

        testResultPopulation(fakeTest,response,Result.FAIL,"Expected test to fail, since we had the response send a shorter body");
    }

    @Test
    public void testResultPopulationFailureNoBodyReceived()
    {
        RestTest fakeTest = createTestForPopulationTest();

        HttpResponse response = createMatchingResponse(fakeTest.getResponse());
        response.setBody(null);

        testResultPopulation(fakeTest,response,Result.FAIL,"Expected test to fail, since we expected no body, but had one sent");
    }

    @Test
    public void testResultPopulationFailureNoBodyExpected()
    {
        RestTest fakeTest = createTestForPopulationTest();

        HttpResponse response = createMatchingResponse(fakeTest.getResponse());

        ((BodyResponse)(fakeTest.getResponse())).setBody(null);
        testResultPopulation(fakeTest,response,Result.FAIL,"Expected test to fail, since we expected no body, but had one sent");
    }

    @Test
    public void testResultPopulationFailureShorterBody()
    {
        RestTest fakeTest = createTestForPopulationTest();

        HttpResponse response = createMatchingResponse(fakeTest.getResponse());

        byte copy[] = new byte[response.getBody().length - 1];
        System.arraycopy(response.getBody(),0,copy,0,copy.length);
        response.setBody(copy);

        testResultPopulation(fakeTest,response,Result.FAIL,"Expected test to fail, since we had the response send a shorter body");
    }

    @Test
    public void testResultPopulationFailureNoBody()
    {
        RestTest fakeTest = createTestForPopulationTest();

        HttpResponse response = createMatchingResponse(fakeTest.getResponse());

        response.setBody(new byte[0]);

        testResultPopulation(fakeTest,response,Result.FAIL,"Expected test to fail, since we had the response send no body");
    }

    @Test
    public void testResultPopulationFailureHeadersExpectedValue()
    {
        RestTest fakeTest = createTestForPopulationTest();

        HttpResponse response = createMatchingResponse(fakeTest.getResponse());

        // Make it so the response omits a header the test requires
        response.getHeaders().put(TestFactory.HEADERS_WE_WONT_USE[2], 
                response.getHeaders().get(TestFactory.HEADERS_WE_WONT_USE[2]) + "BLAH"
                );

        testResultPopulation(fakeTest,response,Result.FAIL,"Expected test to fail, since we changed the value of a header in the response");
    }

    @Test
    public void testResultPopulationFailureHeadersExpected()
    {
        RestTest fakeTest = createTestForPopulationTest();

        HttpResponse response = createMatchingResponse(fakeTest.getResponse());

        // Make it so the response omits a header the test requires
        response.getHeaders().remove(TestFactory.HEADERS_WE_WONT_USE[2]);
        testResultPopulation(fakeTest,response,Result.FAIL,"Expected test to fail, since we removed a header from the response");
    }

    /** Given a RestTestResponse, returns an HttpResponse that, if received, should indicate
     * that the two response match
     */
    private HttpResponse createMatchingResponse(RestTestResponse testResponse)
    {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(200);
        if (testResponse instanceof BodyResponse)
        {
            BodyResponse bodyResponse = (BodyResponse)testResponse;
            response.setBody(bodyResponse.getBody());
        }
        // set the headers to the exact values
        response.setHeaders(new HashMap<String,String>(testResponse.getHeaders()));
        // set some value for required headers
        for (String header: testResponse.getRequiredHeaders())
        {
            response.getHeaders().put(header,"foo");
        }
        return response;
    }


    private void testResultPopulation(RestTest fakeTest, HttpResponse response, Result res, String error)
    {
        Http mockHttp = getMockHttp("GET",response);
        Executor executor = new Executor(mockHttp);
        executor.setBaseURL("http://www.google.com");
        replay(mockHttp);
        ExecutionResult result = executor.execute(fakeTest);
        verify(mockHttp);

        assert result.getResult() == res : error + "(" + result.toString() + ")";
    }


    private RestTest createTestForPopulationTest()
    {
        RestTest fakeTest = TestFactory.getRandomGetTest();
        fakeTest.setMethod("GET");
        fakeTest.getResponse().setStatusCode(200);
        // Make sure each has at least one value
        fakeTest.getResponse().getBannedHeaders().add(TestFactory.HEADERS_WE_WONT_USE[0]);
        fakeTest.getResponse().getRequiredHeaders().add(TestFactory.HEADERS_WE_WONT_USE[1]);
        fakeTest.getResponse().getHeaders().put(TestFactory.HEADERS_WE_WONT_USE[2],TestFactory.HEADERS_WE_WONT_USE[3]);
        return fakeTest;
    }

    /** Returns a mock http object (<b>not</b> in replay mode) with a non-mock, but fake, response.
     * @param method the method that we expect to get called
     * @return an HTTP mock implementation
     */
    private Http getMockHttp(String method)
    {
        HttpResponse fakeResponse = new HttpResponse();
        fakeResponse.setStatusCode(200);
        fakeResponse.setBody(null);
        return getMockHttp(method,fakeResponse);
    }

    /** Returns a mock object (<b>not</b> in replay mode) with the given response.
     * @param method the method that we expect to get called
     * @param fakeResponse the response that we expect this object to return from its method
     * @return an HTTP mock implementation
     */
    private Http getMockHttp(String method, HttpResponse fakeResponse)
    {
        HttpRequest anyRequest = anyObject();
        Http mockHttp = createMock(Http.class);
        if (method.equalsIgnoreCase("get"))
            expect(mockHttp.get(anyRequest)).andReturn(fakeResponse);
        else if (method.equalsIgnoreCase("put"))
            expect(mockHttp.put(anyRequest)).andReturn(fakeResponse);
        else if (method.equalsIgnoreCase("post"))
            expect(mockHttp.post(anyRequest)).andReturn(fakeResponse);
        else if (method.equalsIgnoreCase("delete"))
            expect(mockHttp.delete(anyRequest)).andReturn(fakeResponse);
        else if (method.equalsIgnoreCase("head"))
            expect(mockHttp.head(anyRequest)).andReturn(fakeResponse);
        else
            throw new IllegalArgumentException(method + " isn't supported");
        return mockHttp;
    }

}
