package com.gliffy.test.restunit;

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

        Executor executor = new Executor(mockHttp);
        executor.setBaseURL("http://www.google.com/");
        ExecutionResult result = executor.execute(test);

        assert result.getResult() == Result.PASS : "Got " + result.getResult() + " for " + result.toString() + " instead of " + Result.PASS.toString();

        verify(mockHttp);
    }

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
