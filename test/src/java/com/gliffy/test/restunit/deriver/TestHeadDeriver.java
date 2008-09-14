package com.gliffy.test.restunit.deriver;

import java.util.*;

import com.gliffy.restunit.*;
import com.gliffy.restunit.deriver.*;

import com.gliffy.test.restunit.*;

import org.testng.*;
import org.testng.annotations.*;

/** Tests the {@link com.gliffy.restunit.deriver.HeadDeriver} class */
public class TestHeadDeriver
{
    @Test
    public void testHeadDeriverWithHead()
    {
        GetTest getTest = TestFactory.getRandomGetTest();
        getTest.setRespondsToHead(true);
        getTest.setMethod("GET");

        HeadDeriver deriver = new HeadDeriver();
        RestTest derived = deriver.derive(getTest);

        assert derived != null : "Expected a new test, since our test test responds to head";
        assert derived instanceof GetTest : "Expected a GetTest, not a " + derived.getClass().getName();
        assert derived.getMethod().equals("HEAD") : "Expected method of 'HEAD', but got " + derived.getMethod();
        TestAssertions.assertEqualRegardlessOfMethod(getTest,derived);

        RestTestResponse response = derived.getResponse();

        assert response instanceof BodyResponse : "Expected a BodyResponse, not a " + response.getClass().getName();
        BodyResponse derivedResponse = (BodyResponse)response;
        BodyResponse initialResponse = (BodyResponse)getTest.getResponse();

        assert derivedResponse.getContentType().equals(initialResponse.getContentType()) 
            : "Expected content type of " + initialResponse.getContentType() + " and not " + derivedResponse.getContentType();
        assert derivedResponse.getBody() == null 
            : "Expected a derivedResponse body of nothing, not " + derivedResponse.getBody().length + " bytes";
    }

    @Test
    public void testHeadDeriverWithoutHead()
    {
        GetTest getTest = TestFactory.getRandomGetTest();
        getTest.setRespondsToHead(false);
        getTest.setMethod("GET");

        HeadDeriver deriver = new HeadDeriver();
        RestTest derived = deriver.derive(getTest);

        assert derived == null : "Expected null, since the test doesn't suport head";
    }

    @Test
    public void testHeadDeriverNotGetTest()
    {
        RestTest test = TestFactory.getRandomTest();
        test.setMethod("GET");

        HeadDeriver deriver = new HeadDeriver();
        RestTest derived = deriver.derive(test);

        assert derived == null : "Expected null, since the test doesn't suport head";
    }

    @Test
    public void testHeadDeriverNotGetMethod()
    {
        RestTest test = TestFactory.getRandomTest();
        test.setMethod("PUT");

        HeadDeriver deriver = new HeadDeriver();
        RestTest derived = deriver.derive(test);

        assert derived == null : "Expected null, since the test doesn't suport head";
    }
}
