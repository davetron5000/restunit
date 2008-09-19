package com.gliffy.test.restunit.comparator;

import java.util.*;

import com.gliffy.restunit.comparator.*;
import com.gliffy.restunit.http.*;
import com.gliffy.restunit.*;
import com.gliffy.test.restunit.*;

import org.testng.*;
import org.testng.annotations.*;

public class TestHeaderComparator
{
    @DataProvider(name = "headerTests")
    public Object[][] getMethods() 
    {
        Object [][] data = new Object[6][4];

        int i = 0;
        RestCallResponse testResponse = CallFactory.getRandomResponse();
        HttpResponse httpResponse = CallFactory.createMatchingResponse(testResponse);
        testResponse.setStatusCode(httpResponse.getStatusCode() + 1);
        data[i] = new Object[] { httpResponse, testResponse, true, "Checking that exact headers will match, even if status code doesn't" };
        i++;

        testResponse = CallFactory.getRandomBodyResponse();
        httpResponse = CallFactory.createMatchingResponse(testResponse);
        httpResponse.getBody()[0]++;
        testResponse.setStatusCode(httpResponse.getStatusCode() + 1);
        data[i] = new Object[] { httpResponse, testResponse, true, "Checking that exact headers will match, even if status and body don't"};
        i++;

        testResponse = CallFactory.getRandomBodyResponse();
        httpResponse = CallFactory.createMatchingResponse(testResponse);
        httpResponse.getHeaders().remove(testResponse.getHeaders().keySet().iterator().next());
        data[i] = new Object[] { httpResponse, testResponse, false, "Checking that a missing header causes failure" };
        i++;

        testResponse = CallFactory.getRandomBodyResponse();
        httpResponse = CallFactory.createMatchingResponse(testResponse);
        testResponse.getRequiredHeaders().add(CallFactory.HEADERS_WE_WONT_USE[0]);
        data[i] = new Object[] { httpResponse, testResponse, false, "Checking that a missing required header causes failure" };
        i++;

        testResponse = CallFactory.getRandomBodyResponse();
        httpResponse = CallFactory.createMatchingResponse(testResponse);
        testResponse.getBannedHeaders().add(httpResponse.getHeaders().keySet().iterator().next());
        data[i] = new Object[] { httpResponse, testResponse, false, "Checking that a present banned header causes failure" };
        i++;

        testResponse = CallFactory.getRandomBodyResponse();
        testResponse.getHeaders().put(CallFactory.HEADERS_WE_WONT_USE[0],"foo");
        httpResponse = CallFactory.createMatchingResponse(testResponse);
        httpResponse.getHeaders().put(CallFactory.HEADERS_WE_WONT_USE[0],"foofoo");
        data[i] = new Object[] { httpResponse, testResponse, false, "Checking that a required header with a different value causes failure" };
        i++;

        return data;
    }
        
    @Test(dataProvider = "headerTests", groups = { "comparator"} )
    public void test(HttpResponse httpResponse, RestCallResponse testResponse, boolean match, String testExplanation)
    {
        HeaderComparator comparator = new HeaderComparator();
        ComparisonResult result = comparator.compare(httpResponse,testResponse);

        assert match == result.getMatches() : "For test '" + testExplanation + "' : Expected " + (match ? "comparison to match" : "comparison to not match") + (result.getMatches() ? "" : ("(" + result.getExplanation() + ")") );
    }
}
