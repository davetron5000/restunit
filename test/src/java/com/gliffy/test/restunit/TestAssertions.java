package com.gliffy.test.restunit;

import java.util.*;

import com.gliffy.restunit.*;

import org.testng.*;

/** Utility methods for implementing tests */
public class TestAssertions
{
    private TestAssertions() {}

    /** Checks that the items in RestTestResponds are the same */
    public static void assertEquals(RestTestResponse expected, RestTestResponse got)
    {
        assertSetsEqual(expected.getRequiredHeaders(),got.getRequiredHeaders());
        assertSetsEqual(expected.getBannedHeaders(),got.getBannedHeaders());
        assertMapsEqual(expected.getHeaders(),got.getHeaders());
        Assert.assertEquals(got.getStatusCode(),expected.getStatusCode());
    }

    /** Checks that two tests are essentially equal in their request, save for the method */
    public static void assertEqualRegardlessOfMethod(RestTest expected, RestTest got)
    {
        assert expected.getURL().equals(got.getURL()) : "Expected URL " + expected.getURL() + ", but got " + got.getURL();
        assert expected.getURL().equals(got.getURL()) : "Expected URL " + expected.getURL() + ", but got " + got.getURL();
        assert expected.getName().equals(got.getName()) : "Expected Name " + expected.getName() + ", but got " + got.getName();
        assert expected.getSSLRequirement().equals(got.getSSLRequirement()) : "Expected SSLRequirement " + expected.getSSLRequirement() + ", but got " + got.getSSLRequirement();
        assertMapsEqual(expected.getHeaders(),got.getHeaders());
        assertParamsEqual(expected.getParameters(),got.getParameters());
        assertSetsEqual(expected.getDependentTests(),got.getDependentTests());
        assertEquals(expected.getResponse(),got.getResponse());
    }

    public static void assertSetsEqual(Set<? extends Object> expected, Set<? extends Object> got)
    {
        for (Object o: expected)
            assert got.contains(o) : "Expected " + o.toString() + " but didn't get it";
        for (Object o: got)
            assert expected.contains(o) : "Got " + o.toString() + " but didn't expect it";
    }

    public static void assertParamsEqual(Map<String,List<String>> expected, Map<String,List<String>> got)
    {
        for (String key: expected.keySet())
        {
            List<String> value = expected.get(key);
            if (got.containsKey(key))
                Assert.assertEquals(value,got.get(key));
            else
                assert false : "Didn't get " + key + " at all";
        }
        for (String key: got.keySet())
        {
            List<String> value = got.get(key);
            if (expected.containsKey(key))
                    ;
            else
                assert false : "Got " + key + ", but wasn't expecting it";
        }
    }

    public static void assertMapsEqual(Map<String,String> expected, Map<String,String> got)
    {
        for (String key: expected.keySet())
        {
            String value = expected.get(key);
            if (got.containsKey(key))
                if (got.get(key).equals(value))
                    ;
                else
                    assert false : "Got " + got.get(key) + " for " + key + ", expected " + value;
            else
                assert false : "Didn't get " + key + " at all";
        }
        for (String key: got.keySet())
        {
            String value = got.get(key);
            if (expected.containsKey(key))
                    ;
            else
                assert false : "Got " + key + ", but wasn't expecting it";
        }
    }
}
