package com.gliffy.test.restunit.servlet;

import java.io.*;
import java.net.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;
import com.gliffy.test.restunit.*;

import org.testng.*;
import org.testng.annotations.*;

public class TestHttps
{
    @DataProvider(name = "http")
    public Object[][] getTestsForEntireDataSet() 
    {
        Object[][] https = new Object[1][1];
        https[0][0] = new JavaHttp();
        return https;
    }


    @Test(dataProvider = "http")
    public void testBasic(Http http)
        throws Exception
    {
        RestUnit unit = new RestUnit();
        unit.getExecutor().setHttp(http);
        unit.getExecutor().setBaseURL("http://localhost:9090/test");

        RestTest test = new RestTest();
        test.setName("Basic Test of a " + http.getClass().getName());
        test.setDefaultURL("/foo/bar/baz/blah");

        String data = "This is a random? bit of& <data> that I will be posting";
        String data2 = "!!!!!This is a random? bit of& <data> that I will be posting again";
        RestCall reset = CallFactory.getGet(null,"",null);
        reset.addParameter("reset","true");
        test.addCall(reset);
        test.addCall(CallFactory.getPut(null,data));
        test.addCall(CallFactory.getGet(null,data,"text/plain"));
        test.addCall(CallFactory.getPost(null,data2));
        test.addCall(CallFactory.getGet(null,data2,"text/plain"));
        test.addCall(CallFactory.getDelete(null));
        test.addCall(CallFactory.get404(null));

        RestTestResult result = unit.runTest(test);

        assert result.getSuccess() : "Expected success\n" + result.toString();
    }

    @Test(dataProvider = "http")
    public void testErrors(Http http)
        throws Exception
    {
        RestUnit unit = new RestUnit();
        unit.getExecutor().setHttp(http);
        unit.getExecutor().setBaseURL("http://localhost:9090/test");

        RestTest test = new RestTest();
        test.setName("Basic Test of a " + http.getClass().getName());

        RestCall reset = CallFactory.getGet("/foo/bar","",null);
        reset.addParameter("reset","true");
        test.addCall(reset);
        RestCall put = CallFactory.getPut("/users/rudy","blah foo");
        put.getResponse().setStatusCode(HttpServletResponse.SC_FORBIDDEN);
        test.addCall(CallFactory.get404("/a/b/c/d/e"));
        test.addCall(CallFactory.get404("/users/rudy","image/png"));

        RestTestResult result = unit.runTest(test);

        assert result.getSuccess() : "Expected success\n" + result.toString();
    }

    @Test(dataProvider = "http")
    public void testConditionalGets(Http http)
        throws Exception
    {
        RestUnit unit = new RestUnit();
        unit.getExecutor().setHttp(http);
        unit.getExecutor().setBaseURL("http://localhost:9090/test");

        RestTest test = new RestTest();
        test.setName("Basic Test of a " + http.getClass().getName());
        test.setDefaultURL("/users/rudy");

        RestCall reset = CallFactory.getGet(null,"",null);
        reset.addParameter("reset","true");
        test.addCall(reset);

        HttpRequest request = new HttpRequest();
        request.setURL(new URL(unit.getExecutor().getBaseURL() + "/users/rudy"));
        HttpResponse response = http.get(request);

        assert response.getStatusCode() == 200 : "Got " + response.getStatusCode() + " instead of 200";
        assert response.getHeaders().containsKey("Last-Modified") : "expected 'Last-Modified', but didn't get it";
        assert response.getHeaders().containsKey("ETag") : "expected 'ETag', but didn't get it";

        request.getHeaders().put("If-Modified-Since",response.getHeaders().get("Last-Modified"));

        HttpResponse notModified = http.get(request);

        assert notModified.getStatusCode() == 304 : "Got " + notModified.getStatusCode() + " instead of 304 for If-Modified-Since of " + request.getHeaders().get("If-Modified-Since");

        request.getHeaders().remove("If-Modified-Since");
        request.getHeaders().put("If-None-Match",response.getHeaders().get("ETag"));

        notModified = http.get(request);

        assert notModified.getStatusCode() == 304 : "Got " + response.getStatusCode() + " instead of 304 for If-None-Match of " + request.getHeaders().get("If-None-Match");
    }

    @Test(dataProvider = "http")
    public void testServerError(Http http)
        throws Exception
    {
        RestUnit unit = new RestUnit();
        unit.getExecutor().setHttp(http);
        unit.getExecutor().setBaseURL("http://localhost:9090/test");

        RestTest test = new RestTest();
        test.setName("Basic Test of a " + http.getClass().getName());
        test.setDefaultURL("/users/rudy");

        RestCall reset = CallFactory.getGet(null,"",null);
        reset.addParameter("reset","true");
        test.addCall(reset);

        HttpRequest request = new HttpRequest();
        request.setURL(new URL(unit.getExecutor().getBaseURL() + "/users/rudy?data=12334"));
        HttpResponse response = http.post(request);

        assert response.getStatusCode() == 500 : "Got " + response.getStatusCode() + " instead of 500";
    }

    @Test(dataProvider = "http")
    public void testGetHead(Http http)
        throws Exception
    {
        RestUnit unit = new RestUnit();
        unit.getExecutor().setHttp(http);
        unit.getExecutor().setBaseURL("http://localhost:9090/test");

        RestTest test = new RestTest();
        test.setName("Basic Test of a " + http.getClass().getName());
        test.setDefaultURL("/users/rudy");

        RestCall reset = CallFactory.getGet(null,"",null);
        reset.addParameter("reset","true");
        test.addCall(reset);

        HttpRequest request = new HttpRequest();
        request.setURL(new URL(unit.getExecutor().getBaseURL() + "/users/rudy"));
        HttpResponse getResponse = http.get(request);
        HttpResponse headResponse = http.head(request);

        assert getResponse.getStatusCode() == 200 : "Got " + getResponse.getStatusCode() + " instead of 200";
        assert headResponse.getStatusCode() == 200 : "Got " + headResponse.getStatusCode() + " instead of 200";

        // TestServlet has an intentional bug in it related to this
        getResponse.getHeaders().remove("ETag");

        TestAssertions.assertMapsEqual(getResponse.getHeaders(),headResponse.getHeaders());
    }

}
