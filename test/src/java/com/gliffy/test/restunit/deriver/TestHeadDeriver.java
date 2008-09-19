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
    @Test(groups = { "deriver" } )
    public void testHeadDeriverWithHead()
    {
        GetCall getTest = CallFactory.getRandomGetCall();
        getTest.setRespondsToHead(true);
        getTest.setMethod("GET");

        HeadDeriver deriver = new HeadDeriver();
        RestCall derived = deriver.derive(getTest,null);

        assert derived != null : "Expected a new test, since our test test responds to head";
        assert derived instanceof GetCall : "Expected a GetCall, not a " + derived.getClass().getName();
        assert derived.getMethod().equals("HEAD") : "Expected method of 'HEAD', but got " + derived.getMethod();
        checkEqualityMoreOrLess(getTest,derived);

        RestCallResponse response = derived.getResponse();

        assert response instanceof BodyResponse : "Expected a BodyResponse, not a " + response.getClass().getName();
        BodyResponse derivedResponse = (BodyResponse)response;
        BodyResponse initialResponse = (BodyResponse)getTest.getResponse();

        assert derivedResponse.getContentType().equals(initialResponse.getContentType()) 
            : "Expected content type of " + initialResponse.getContentType() + " and not " + derivedResponse.getContentType();
        assert derivedResponse.getBody() == null 
            : "Expected a derivedResponse body of nothing, not " + derivedResponse.getBody().length + " bytes";
    }

    @Test(groups = { "deriver" } )
    public void testHeadDeriverWithoutHead()
    {
        GetCall getTest = CallFactory.getRandomGetCall();
        getTest.setRespondsToHead(false);
        getTest.setMethod("GET");

        HeadDeriver deriver = new HeadDeriver();
        RestCall derived = deriver.derive(getTest,null);

        assert derived == null : "Expected null, since the test doesn't suport head";
    }

    @Test(groups = { "deriver" } )
    public void testHeadDeriverNotGetCall()
    {
        RestCall test = CallFactory.getRandomCall();
        test.setMethod("GET");

        HeadDeriver deriver = new HeadDeriver();
        RestCall derived = deriver.derive(test,null);

        assert derived == null : "Expected null, since the test doesn't suport head";
    }

    @Test(groups = { "deriver" } )
    public void testHeadDeriverNotGetMethod()
    {
        RestCall test = CallFactory.getRandomCall();
        test.setMethod("PUT");

        HeadDeriver deriver = new HeadDeriver();
        RestCall derived = deriver.derive(test,null);

        assert derived == null : "Expected null, since the test doesn't suport head";
    }

    /** Checks that two tests are essentially equal in their request, save for the method */
    private void checkEqualityMoreOrLess(RestCall expected, RestCall got)
    {
        assert expected.getURL().equals(got.getURL()) : "Expected URL " + expected.getURL() + ", but got " + got.getURL();
        assert expected.getURL().equals(got.getURL()) : "Expected URL " + expected.getURL() + ", but got " + got.getURL();
        assert expected.getName().equals(got.getName()) : "Expected Name " + expected.getName() + ", but got " + got.getName();
        assert expected.getSSLRequirement().equals(got.getSSLRequirement()) : "Expected SSLRequirement " + expected.getSSLRequirement() + ", but got " + got.getSSLRequirement();
        TestAssertions.assertMapsEqual(expected.getHeaders(),got.getHeaders());
        TestAssertions.assertParamsEqual(expected.getParameters(),got.getParameters());
        TestAssertions.assertEquals(expected.getResponse(),got.getResponse());
    }

}
