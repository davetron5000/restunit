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
        Object [][] data = new Object[2][3];
        RestTestResponse testResponse = TestFactory.getRandomResponse();
        HttpResponse httpResponse = TestFactory.createMatchingResponse(testResponse);
        testResponse.setStatusCode(httpResponse.getStatusCode() + 1);
        data[0] = new Object[] { httpResponse, testResponse, true, };

        testResponse = TestFactory.getRandomBodyResponse();
        httpResponse = TestFactory.createMatchingResponse(testResponse);
        testResponse.setStatusCode(httpResponse.getStatusCode() + 1);
        data[1] = new Object[] { httpResponse, testResponse, true, };
        return data;
    }
        
    @Test(dataProvider = "headerTests")
    public void test(HttpResponse httpResponse, RestTestResponse testResponse, boolean match)
    {
        HeaderComparator comparator = new HeaderComparator();
        ComparisonResult result = comparator.compare(httpResponse,testResponse);

        assert match == result.getMatches() : "Expected " + (match ? "comparison to match" : "comparison to not match") + (result.getMatches() ? "" : ("(" + result.getExplanation() + ")") );
    }
}
