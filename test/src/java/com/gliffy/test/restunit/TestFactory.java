package com.gliffy.test.restunit;

import java.util.*;

import com.gliffy.restunit.*;

import org.testng.*;

public class TestFactory
{
    private static final String URL_PARTS[] = 
    {
        "apple",
        "boulion",
        "celery",
        "dill",
        "eel",
        "fennel",
        "garlic",
    };

    private static final String BASIC_METHODS[] = 
    {
        "GET",
        "PUT",
        "DELETE",
        "POST"
    };

    private static final String PARAMS[] =
    {
        "foo",
        "bar",
        "baz",
        "blah",
        "quux",
    };

    private static final String VALUES[][] = 
    {
        {"a"},
        {"b","c","def"},
        {"ghi","jklmn","opqrstuvwxy","z"},
    };

    private static final String HEADERS[] = 
    {
        "Header1",
        "Header2",
        "Header3",
        "Header4",
        "Header5",
    };

    private static final String HEADER_VALUES[] = 
    {
        "Value1",
        "Value2",
        "Value3",
        "Value4",
        "Value5",
    };

    private static final Random RANDOM = new Random(1L);
    private static final List<SSLRequirement> SSLS = new ArrayList<SSLRequirement>(EnumSet.allOf(SSLRequirement.class));

    private static String[] random(String [][]array)
    {
        return array[RANDOM.nextInt(array.length)];
    }

    private static String random(String []array)
    {
        return array[RANDOM.nextInt(array.length)];
    }

    public static RestTest getRandomTest()
    {
        RestTest t = new RestTest();
        populateWithDependents(t);
        return t;
    }

    public static GetTest getRandomGetTest()
    {
        GetTest t = new GetTest();
        populateWithDependents(t);
        popualteGet(t);
        return t;
    }

    private static void popualteGet(GetTest t)
    {
        t.setRespondsToIfNoneMatch(RANDOM.nextBoolean());
        t.setRespondsToIfModified(RANDOM.nextBoolean());
        t.setRespondsToHead(RANDOM.nextBoolean());
    }

    private static void populateWithDependents(RestTest t) { populate(t,true); }
    private static void populateWithOutDependents(RestTest t){ populate(t,false); }
    private static void populate(RestTest t, boolean dependents)
    {
        int parts = RANDOM.nextInt(5) + 1;

        StringBuilder b = new StringBuilder();
        for (int i=0;i<parts; i++)
        {
            b.append(random(URL_PARTS));
            b.append("/");
        }
        t.setURL(b.toString());
        t.setMethod(random(BASIC_METHODS));

        int params = RANDOM.nextInt(PARAMS.length);
        for (int i=0;i<params; i++)
        {
            t.addParameter( random(PARAMS), random(VALUES));
        }
        int headers = RANDOM.nextInt(HEADERS.length);
        for (int i=0;i<headers; i++)
        {
            t.addHeader(random(HEADERS), random(HEADER_VALUES));
        }
        t.setName("Pretend Test");
        t.setDescription("A pretend Test");
        int ssl = RANDOM.nextInt(SSLS.size());
        t.setSSLRequirement(SSLS.get(ssl));
        if (dependents)
        {
            int deps = RANDOM.nextInt(5);
            for (int i=0;i<deps; i++)
            {
                RestTest dep = new RestTest();
                populateWithOutDependents(dep);
                t.getDependentTests().add(dep);
            }
        }
        t.setResponse(getRandomResponse());
    }

    public static RestTestResponse getRandomResponse()
    {
        RestTestResponse r = new RestTestResponse();
        populate(r);
        return r;
    }

    private static void populate(RestTestResponse r)
    {
        r.setStatusCode( ( (RANDOM.nextInt(4) + 1) * 100) + RANDOM.nextInt(2) );
        int headers = RANDOM.nextInt(HEADERS.length);
        for (int i=0;i<headers; i++)
            r.getRequiredHeaders().add(random(HEADERS));

        headers = RANDOM.nextInt(HEADERS.length);
        for (int i=0;i<headers; i++)
            r.getBannedHeaders().add(random(HEADERS));

        headers = RANDOM.nextInt(HEADERS.length);
        for (int i=0;i<headers; i++)
            r.getHeaders().put(random(HEADERS),random(HEADER_VALUES));
    }
}
