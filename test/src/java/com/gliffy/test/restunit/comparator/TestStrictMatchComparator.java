
package com.gliffy.test.restunit.comparator;

import java.util.*;

import com.gliffy.restunit.comparator.*;
import com.gliffy.restunit.http.*;
import com.gliffy.restunit.*;
import com.gliffy.test.restunit.*;

import org.testng.*;
import org.testng.annotations.*;

public class TestStrictMatchComparator
{
    @DataProvider(name = "strictTests")
    public Object[][] getMethods() 
    {
        List<Object[]> testData = new ArrayList<Object[]>();

        RestTestResponse testResponse = TestFactory.getRandomBodyResponse();
        HttpResponse httpResponse = TestFactory.createMatchingResponse(testResponse);

        testData.add(new Object[] { httpResponse, testResponse, true, "Exactly matching bodies are equal", });

        testResponse = TestFactory.getRandomResponse();
        httpResponse = TestFactory.createMatchingResponse(testResponse);

        testData.add(new Object[] { httpResponse, testResponse, true, "null bodies are equal", });

        testResponse = TestFactory.getRandomResponse();
        httpResponse = TestFactory.createMatchingResponse(testResponse);
        httpResponse.setBody(new byte[0]);

        testData.add(new Object[] { httpResponse, testResponse, true, "null body equals empty body", });

        testResponse = TestFactory.getRandomBodyResponse();
        ((BodyResponse)testResponse).setBody(new byte[0]);
        httpResponse = TestFactory.createMatchingResponse(testResponse);
        httpResponse.setBody(null);

        testData.add(new Object[] { httpResponse, testResponse, true, "empty body equals null body", });

        testResponse = TestFactory.getRandomBodyResponse();
        httpResponse = TestFactory.createMatchingResponse(testResponse);
        httpResponse.getBody()[0]++;

        testData.add(new Object[] { httpResponse, testResponse, false, "Same size, but different bodies fail"});

        testResponse = TestFactory.getRandomBodyResponse();
        httpResponse = TestFactory.createMatchingResponse(testResponse);
        httpResponse.setBody(new byte[((BodyResponse)testResponse).getBody().length]);
        System.arraycopy(((BodyResponse)testResponse).getBody(),0,
                httpResponse.getBody(),
                0,
                httpResponse.getBody().length - 1);
        httpResponse.getBody()[httpResponse.getBody().length - 1] = 4;

        testData.add(new Object[] { httpResponse, testResponse, false, "Different size, different bodies fail"});

        return testData.toArray(new Object[0][0]);
    }
        
    @Test(dataProvider = "strictTests")
    public void test(HttpResponse httpResponse, RestTestResponse testResponse, boolean match, String description)
    {
        StrictMatchComparator comparator = new StrictMatchComparator();
        ComparisonResult result = comparator.compare(httpResponse,testResponse);

        assert match == result.getMatches() : description + "; Expected " + (match ? "comparison to match" : "comparison to not match") + (result.getMatches() ? "" : ("(" + result.getExplanation() + ")") );
    }
}
