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
    public Object[][] getMethods() 
    {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setStatusCode(200);
        HttpResponse httpErrorResponse = new HttpResponse();
        httpErrorResponse.setStatusCode(404);
        return new Object[][] {
            { httpResponse, CallFactory.getRandomResponse(200), true, "Ensure that tests with same status are equal" },
            { httpResponse, CallFactory.getRandomBodyResponse(200), true, "Ensure that tests with same status are equal for body response " },
            { httpErrorResponse, CallFactory.getRandomResponse(404), true, "Ensure that tests with the same non-OK HTTP status are equal" },
            { httpErrorResponse, CallFactory.getRandomBodyResponse(404), true, "Ensure success for same non-OK HTTP status for body response" },
            { httpErrorResponse, CallFactory.getRandomResponse(400), false, "Check that different status fails" },
            { httpErrorResponse, CallFactory.getRandomBodyResponse(400), false, "Check that different status fails for body response" },
            { httpResponse, CallFactory.getRandomResponse(0), false, "Check that invalid HTTP status fails" },
            { httpResponse, CallFactory.getRandomBodyResponse(0), false, "Check that invalid HTTP status fails for a body response" },
        };
    }
        
    @Test(dataProvider = "statusTests", groups = { "comparator"} )
    public void test(HttpResponse httpResponse, RestCallResponse testResponse, boolean match, String description)
    {
        StatusComparator comparator = new StatusComparator();
        ComparisonResult result = comparator.compare(httpResponse,testResponse);

        assert match == result.getMatches() : description + "; Expected " + (match ? "comparison to match" : "comparison to not match") + (result.getMatches() ? "" : ("(" + result.getExplanation() + ")") );
    }
}
