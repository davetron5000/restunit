package com.gliffy.test.restunit.servlet;

import java.io.*;
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
    public void testGets(Http http)
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
        test.addCall(CallFactory.getPut(null,data));
        test.addCall(CallFactory.getGet(null,data,"text/plain"));
        test.addCall(CallFactory.getPost(null,data2));
        test.addCall(CallFactory.getGet(null,data2,"text/plain"));
        test.addCall(CallFactory.getDelete(null));
        test.addCall(CallFactory.get404(null));

        RestTestResult result = unit.runTest(test);

        assert result.getSuccess() : "Expected success\n" + result.toString();
    }
}
