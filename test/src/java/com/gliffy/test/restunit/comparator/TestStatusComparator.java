package com.gliffy.test.restunit.comparator;

import java.util.*;

import com.gliffy.restunit.comparator.*;
import com.gliffy.restunit.http.*;
import com.gliffy.restunit.*;
import com.gliffy.test.restunit.*;

import org.testng.*;
import org.testng.annotations.*;

public class TestStatusComparator
{
    @DataProvider(name = "statusTests")
    public Object[][] getMethods() {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusCode(200);
        HttpResponse httpErrorResponse = new HttpResponse();
        httpErrorResponse.setStatusCode(404);
        return new Object[][] {
            { httpResponse, TestFactory.getRandomResponse(200), true, },
            { httpResponse, TestFactory.getRandomBodyResponse(200), true, },
            { httpErrorResponse, TestFactory.getRandomResponse(404), true, },
            { httpErrorResponse, TestFactory.getRandomBodyResponse(404), true, },
            { httpErrorResponse, TestFactory.getRandomResponse(400), false, },
            { httpErrorResponse, TestFactory.getRandomBodyResponse(400), false, },
            { httpResponse, TestFactory.getRandomResponse(0), false, },
            { httpResponse, TestFactory.getRandomBodyResponse(0), false, },
        };
    }
        
    @Test(dataProvider = "statusTests")
    public void test(HttpResponse httpResponse, RestTestResponse testResponse, boolean match)
    {
        StatusComparator comparator = new StatusComparator();
        ComparisonResult result = comparator.compare(httpResponse,testResponse);

        assert match == result.getMatches() : "Expected " + (match ? "comparison to match" : "comparison to not match") + (result.getMatches() ? "" : ("(" + result.getExplanation() + ")") );
    }
}
