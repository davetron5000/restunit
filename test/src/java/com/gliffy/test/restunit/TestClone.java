package com.gliffy.test.restunit;

import com.gliffy.restunit.*;

import org.testng.*;
import org.testng.annotations.*;

public class TestClone
{
    @Test( groups = { "clone" } )
    public void testRestCall()
    {
        RestCall t = CallFactory.getRandomCall();
        RestCall clone = (RestCall)t.clone();

        testClone(t,clone);
    }

    private void testClone(RestCall t, RestCall clone)
    {
        assert clone.getName().equals(t.getName()):  "Expected " + t.getName() + " for getName()";
        assert clone.getMethod().equals(t.getMethod()):  "Expected " + t.getMethod() + " for getMethod()";
        assert clone.getURL().equals(t.getURL()):  "Expected " + t.getURL() + " for getURL()";
        assert clone.getSSLRequirement().equals(t.getSSLRequirement()):  "Expected " + t.getSSLRequirement() + " for getSSLRequirement()";
        assert clone.getParameters() != t.getParameters() : "Expected that clone's parameters would be a different ref";
        assert clone.getHeaders() != t.getHeaders() : "Expected that clone's headers would be a different ref";
        if (t.getResponse() != null)
            assert clone.getResponse() != t.getResponse() : "Expected the responses to be different references";
    }

    @Test(dependsOnMethods = { "testRestCall" }, groups = {"clone"} )
    public void testGetCall()
    {
        GetCall t = CallFactory.getRandomGetCall();
        GetCall clone = (GetCall)t.clone();

        testClone(t,clone);

        assert clone.getRespondsToHead() == t.getRespondsToHead() : "Expected " + t.getRespondsToHead() + " for getRespondsToHead();";
        assert clone.getRespondsToIfModified() == t.getRespondsToIfModified() : "Expected " + t.getRespondsToIfModified() + " for getRespondsToIfModified();";
        assert clone.getRespondsToIfNoneMatch() == t.getRespondsToIfNoneMatch() : "Expected " + t.getRespondsToIfNoneMatch() + " for getRespondsToIfNoneMatch();";
    }
}
