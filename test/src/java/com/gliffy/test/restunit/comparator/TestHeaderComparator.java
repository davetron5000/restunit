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
        RestTestResponse testResponse = TestFactory.getRandomResponse();
        HttpResponse httpResponse = TestFactory.createMatchingResponse(testResponse);
        testResponse.setStatusCode(httpResponse.getStatusCode() + 1);
        data[i] = new Object[] { httpResponse, testResponse, true, "Checking that exact headers will match, even if status code doesn't" };
        i++;

        testResponse = TestFactory.getRandomBodyResponse();
        httpResponse = TestFactory.createMatchingResponse(testResponse);
        httpResponse.getBody()[0]++;
        testResponse.setStatusCode(httpResponse.getStatusCode() + 1);
        data[i] = new Object[] { httpResponse, testResponse, true, "Checking that exact headers will match, even if status and body don't"};
        i++;

        testResponse = TestFactory.getRandomBodyResponse();
        httpResponse = TestFactory.createMatchingResponse(testResponse);
        httpResponse.getHeaders().remove(testResponse.getHeaders().keySet().iterator().next());
        data[i] = new Object[] { httpResponse, testResponse, false, "Checking that a missing header causes failure" };
        i++;

        testResponse = TestFactory.getRandomBodyResponse();
        httpResponse = TestFactory.createMatchingResponse(testResponse);
        testResponse.getRequiredHeaders().add(TestFactory.HEADERS_WE_WONT_USE[0]);
        data[i] = new Object[] { httpResponse, testResponse, false, "Checking that a missing required header causes failure" };
        i++;

        testResponse = TestFactory.getRandomBodyResponse();
        httpResponse = TestFactory.createMatchingResponse(testResponse);
        testResponse.getBannedHeaders().add(httpResponse.getHeaders().keySet().iterator().next());
        data[i] = new Object[] { httpResponse, testResponse, false, "Checking that a present banned header causes failure" };
        i++;

        testResponse = TestFactory.getRandomBodyResponse();
        testResponse.getHeaders().put(TestFactory.HEADERS_WE_WONT_USE[0],"foo");
        httpResponse = TestFactory.createMatchingResponse(testResponse);
        httpResponse.getHeaders().put(TestFactory.HEADERS_WE_WONT_USE[0],"foofoo");
        data[i] = new Object[] { httpResponse, testResponse, false, "Checking that a required header with a different value causes failure" };
        i++;

        return data;
    }
        
    @Test(dataProvider = "headerTests")
    public void test(HttpResponse httpResponse, RestTestResponse testResponse, boolean match, String testExplanation)
    {
        HeaderComparator comparator = new HeaderComparator();
        ComparisonResult result = comparator.compare(httpResponse,testResponse);

        assert match == result.getMatches() : "For test '" + testExplanation + "' : Expected " + (match ? "comparison to match" : "comparison to not match") + (result.getMatches() ? "" : ("(" + result.getExplanation() + ")") );
    }
}
