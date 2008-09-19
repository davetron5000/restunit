package com.gliffy.test.restunit;

import java.util.*;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

import org.testng.*;
import org.testng.annotations.*;

import static org.easymock.classextension.EasyMock.*;

public class TestExecutor
{
    @Test(groups = {"executor"})
    public void testBadSetup()
    {
        Executor executor = new Executor();
        executor.setBaseURL("http://www.google.com/");

        try
        {
            executor.execute(CallFactory.getRandomCall());
            assert false : "Expected an exception when HTTP wasn't configured";
        }
        catch (IllegalStateException e)
        {
            // successful test
        }
    }

    @Test(dataProvider = "methods", groups={"executor"})
    public void testBasicMethodNoBaseURL(String method)
    {
        Http mockHttp = getMockHttp(method.toUpperCase());

        replay(mockHttp);

        RestCall test = CallFactory.getRandomCall();
        test.setURL("http://www.google.com/" + test.getURL());
        test.setMethod(method.toUpperCase());
        test.getResponse().setStatusCode(200);
        clearHeaderRequirements(test);

        Executor executor = new Executor();
        executor.setHttp(mockHttp);
        ExecutionResult result = executor.execute(test);

        assert result.getResult() == Result.PASS : "Got " + result.getResult() + " for " + result.toString() + " instead of " + Result.PASS.toString();

        verify(mockHttp);
    }

    /** Runs a basic test for a method to see that it gets called.
     * @param method the method to use
     */
    @Test(dataProvider = "methods", groups={"executor"})
    public void testBasicMethod(String method)
    {
        Http mockHttp = getMockHttp(method.toUpperCase());

        replay(mockHttp);

        RestCall test = CallFactory.getRandomCall();
        test.setMethod(method.toUpperCase());
        test.getResponse().setStatusCode(200);
        clearHeaderRequirements(test);

        Executor executor = new Executor();
        executor.setHttp(mockHttp);
        executor.setBaseURL("http://www.google.com/");
        ExecutionResult result = executor.execute(test);

        assert result.getResult() == Result.PASS : "Got " + result.getResult() + " for " + result.toString() + " instead of " + Result.PASS.toString();

        verify(mockHttp);
    }

    /** This tests that the HttpRequest created by the Executor to give to Http was done using the actual
     * test information; the Executor must interrogate the test to find out what to request
     */
    @Test(groups = {"executor"})
    public void testRequestCreation()
    {
        RestCall mockCall = createMock(RestCall.class);
        RestCall randomCall = CallFactory.getRandomGetCall();

        expect(mockCall.getName()).andReturn("Mock Call").anyTimes();
        expect(mockCall.getMethod()).andReturn("GET");
        expect(mockCall.getURL()).andReturn(randomCall.getURL());
        expect(mockCall.getParameters()).andReturn(randomCall.getParameters());
        expect(mockCall.getHeaders()).andReturn(randomCall.getHeaders());
        expect(mockCall.getResponse()).andReturn(randomCall.getResponse());

        Http mockHttp = getMockHttp("GET");
        replay (mockHttp);
        replay (mockCall);

        Executor executor =  new Executor();
        executor.setHttp(mockHttp);
        executor.setBaseURL("http://www.google.com/");
        executor.execute(mockCall);

        verify(mockHttp);
        verify(mockCall);
    }

    /** This tests that the results returned from the MockHTTP service ended up in the right place in the test results
     */
    @Test(groups = {"executor"})
    public void testResultPopulationSuccess()
    {
        RestCall fakeCall = CallFactory.getRandomGetCall();
        fakeCall.setMethod("GET");
        fakeCall.getResponse().setStatusCode(200);

        HttpResponse response = CallFactory.createMatchingResponse(fakeCall.getResponse());
        Http mockHttp = getMockHttp("GET",response);
        Executor executor = new Executor();
        executor.setHttp(mockHttp);
        executor.setBaseURL("http://www.google.com");
        replay(mockHttp);
        ExecutionResult result = executor.execute(fakeCall);
        verify(mockHttp);

        assert result.getResult() == Result.PASS : "Expected test to pass " + result.toString();
    }

    @Test(dataProvider="methods", groups = {"executor"})
    public void testResultPopulationSuccessSimple(String method)
    {
        RestCall fakeCall = CallFactory.getRandomCall();
        fakeCall.setMethod(method);
        fakeCall.getResponse().setStatusCode(200);

        HttpResponse response = CallFactory.createMatchingResponse(fakeCall.getResponse());
        Http mockHttp = getMockHttp(method,response);
        Executor executor = new Executor();
        executor.setHttp(mockHttp);
        executor.setBaseURL("http://www.google.com");
        replay(mockHttp);
        ExecutionResult result = executor.execute(fakeCall);
        verify(mockHttp);

        assert result.getResult() == Result.PASS : "Expected test to pass " + result.toString();
    }

    @Test(groups = {"executor"})
    public void testResultPopulationFailureHeadersBanned()
    {
        RestCall fakeCall = createCallForPopulationCall();

        HttpResponse response = CallFactory.createMatchingResponse(fakeCall.getResponse());

        // Make it so the response omits a header the test requires
        response.getHeaders().put(fakeCall.getResponse().getBannedHeaders().iterator().next(),"BLAH");
        testResultPopulation(fakeCall,response,Result.FAIL,"Expected test to fail, since we put a header that was banned in the response");
    }

    @Test(groups = {"executor"})
    public void testResultPopulationFailureDifferentBody()
    {
        RestCall fakeCall = createCallForPopulationCall();

        HttpResponse response = CallFactory.createMatchingResponse(fakeCall.getResponse());

        byte copy[] = new byte[response.getBody().length];
        for (int i=0;i<response.getBody().length; i++)
        {
            copy[i] = (byte)(255 - response.getBody()[i]);
        }
        response.setBody(copy);

        testResultPopulation(fakeCall,response,Result.FAIL,"Expected test to fail, since we had the response send a shorter body");
    }

    @Test(groups = {"executor"})
    public void testResultPopulationFailureNoBodyReceived()
    {
        RestCall fakeCall = createCallForPopulationCall();

        HttpResponse response = CallFactory.createMatchingResponse(fakeCall.getResponse());
        response.setBody(null);

        testResultPopulation(fakeCall,response,Result.FAIL,"Expected test to fail, since we expected no body, but had one sent");
    }

    @Test(groups = {"executor"})
    public void testResultPopulationFailureNoBodyExpected()
    {
        RestCall fakeCall = createCallForPopulationCall();

        HttpResponse response = CallFactory.createMatchingResponse(fakeCall.getResponse());

        ((BodyResponse)(fakeCall.getResponse())).setBody(null);
        testResultPopulation(fakeCall,response,Result.FAIL,"Expected test to fail, since we expected no body, but had one sent");
    }

    @Test(groups = {"executor"})
    public void testResultPopulationFailureShorterBody()
    {
        RestCall fakeCall = createCallForPopulationCall();

        HttpResponse response = CallFactory.createMatchingResponse(fakeCall.getResponse());

        byte copy[] = new byte[response.getBody().length - 1];
        System.arraycopy(response.getBody(),0,copy,0,copy.length);
        response.setBody(copy);

        testResultPopulation(fakeCall,response,Result.FAIL,"Expected test to fail, since we had the response send a shorter body");
    }

    @Test(groups = {"executor"})
    public void testResultPopulationFailureNoBody()
    {
        RestCall fakeCall = createCallForPopulationCall();

        HttpResponse response = CallFactory.createMatchingResponse(fakeCall.getResponse());

        response.setBody(new byte[0]);

        testResultPopulation(fakeCall,response,Result.FAIL,"Expected test to fail, since we had the response send no body");
    }

    @Test(groups = {"executor"})
    public void testResultPopulationFailureHeadersExpectedValue()
    {
        RestCall fakeCall = createCallForPopulationCall();

        HttpResponse response = CallFactory.createMatchingResponse(fakeCall.getResponse());

        // Make it so the response omits a header the test requires
        response.getHeaders().put(CallFactory.HEADERS_WE_WONT_USE[2], 
                response.getHeaders().get(CallFactory.HEADERS_WE_WONT_USE[2]) + "BLAH"
                );

        testResultPopulation(fakeCall,response,Result.FAIL,"Expected test to fail, since we changed the value of a header in the response");
    }

    @Test(groups = {"executor"})
    public void testResultPopulationFailureHeadersExpected()
    {
        RestCall fakeCall = createCallForPopulationCall();

        HttpResponse response = CallFactory.createMatchingResponse(fakeCall.getResponse());

        // Make it so the response omits a header the test requires
        response.getHeaders().remove(CallFactory.HEADERS_WE_WONT_USE[2]);
        testResultPopulation(fakeCall,response,Result.FAIL,"Expected test to fail, since we removed a header from the response");
    }

    @DataProvider(name = "methods")
    public Object[][] getMethods() 
    {
        return new Object[][] {
            { "GET" },
            { "HEAD" },
            { "POST" },
            { "PUT" },
            { "DELETE" },
        };
    }

    private void testResultPopulation(RestCall fakeCall, HttpResponse response, Result res, String error)
    {
        Http mockHttp = getMockHttp("GET",response);
        Executor executor = new Executor();
        executor.setHttp(mockHttp);
        executor.setBaseURL("http://www.google.com");
        replay(mockHttp);
        ExecutionResult result = executor.execute(fakeCall);
        verify(mockHttp);

        assert result.getResult() == res : error + "(" + result.toString() + ")";
    }


    private RestCall createCallForPopulationCall()
    {
        RestCall fakeCall = CallFactory.getRandomGetCall();
        fakeCall.setMethod("GET");
        fakeCall.getResponse().setStatusCode(200);
        // Make sure each has at least one value
        fakeCall.getResponse().getBannedHeaders().add(CallFactory.HEADERS_WE_WONT_USE[0]);
        fakeCall.getResponse().getRequiredHeaders().add(CallFactory.HEADERS_WE_WONT_USE[1]);
        fakeCall.getResponse().getHeaders().put(CallFactory.HEADERS_WE_WONT_USE[2],CallFactory.HEADERS_WE_WONT_USE[3]);
        return fakeCall;
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

    private void clearHeaderRequirements(RestCall test)
    {
        test.getResponse().setHeaders(new HashMap<String,String>());
        test.getResponse().setBannedHeaders(new HashSet<String>());
        test.getResponse().setRequiredHeaders(new HashSet<String>());
    }

}
