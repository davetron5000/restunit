package com.gliffy.test.restunit;

import java.util.*;

import com.gliffy.restunit.*;
import com.gliffy.restunit.http.*;

import org.testng.*;

public class CallFactory
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

    private static final String CONTENT_TYPES[] = 
    {
        "text/xml",
        "text/plain",
        "text/html",
        "image/png",
        "image/jpeg",
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

    /** You can use this and be sure that random tests and responses will
     * not use these
     */
    public static final String HEADERS_WE_WONT_USE[] = 
    {
        "WONTUSEfoo",
        "WONTUSEbar",
        "WONTUSEblah",
        "WONTUSEbaz",
        "WONTUSEquux",

    };

    private static final String BANNED_HEADERS[] = 
    {
        "BannedHeader1",
        "BannedHeader2",
        "BannedHeader3",
        "BannedHeader4",
        "BannedHeader5",
    };

    private static final String REQUIRED_HEADERS[] = 
    {
        "RequiredHeader1",
        "RequiredHeader2",
        "RequiredHeader3",
        "RequiredHeader4",
        "RequiredHeader5",
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

    public static RestCall getRandomCall()
    {
        RestCall t = new RestCall();
        populate(t);
        return t;
    }

    public static GetCall getRandomGetCall()
    {
        GetCall t = new GetCall();
        populate(t);
        popualteGet(t);
        t.setResponse(getRandomBodyResponse());
        return t;
    }

    private static void popualteGet(GetCall t)
    {
        t.setRespondsToIfNoneMatch(RANDOM.nextBoolean());
        t.setRespondsToIfModified(RANDOM.nextBoolean());
        t.setRespondsToHead(RANDOM.nextBoolean());
    }

    private static void populate(RestCall t)
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
        t.setName("Pretend Call");
        t.setDescription("A pretend Call");
        int ssl = RANDOM.nextInt(SSLS.size());
        t.setSSLRequirement(SSLS.get(ssl));
        t.setResponse(getRandomResponse());
    }

    public static RestCallResponse getRandomResponse(int status)
    {
        RestCallResponse r = getRandomResponse();
        r.setStatusCode(status);
        return r;
    }

    public static RestCallResponse getRandomResponse()
    {
        RestCallResponse r = new RestCallResponse();
        populate(r);
        return r;
    }

    public static BodyResponse getRandomBodyResponse(int status)
    {
        BodyResponse b = getRandomBodyResponse();
        b.setStatusCode(status);
        return b;
    }

    public static BodyResponse getRandomBodyResponse()
    {
        BodyResponse r = new BodyResponse();
        populate(r);
        r.setContentType(random(CONTENT_TYPES));
        byte bytes[] = new byte[RANDOM.nextInt(256)];

        RANDOM.nextBytes(bytes);

        r.setBody(bytes);
        return r;
    }

    public static ExecutionResult getSuccessfulResult(RestCall test)
    {
        ExecutionResult success = new ExecutionResult();
        success.setResult(Result.PASS);
        success.setCall(test);
        success.setExecutionTime(1L);
        success.setExecutionDate(new java.util.Date());

        return success;
    }

    /** Given a RestCallResponse, returns an HttpResponse that, if received, should indicate
     * that the two response match
     */
    public static HttpResponse createMatchingResponse(RestCallResponse testResponse)
    {
        HttpResponse response = new HttpResponse();
        response.setStatusCode(testResponse.getStatusCode());
        if (testResponse instanceof BodyResponse)
        {
            byte responseBytes[] = ((BodyResponse)testResponse).getBody();
            byte copy[] = new byte[responseBytes.length];
            System.arraycopy(responseBytes,0,copy,0,copy.length);
            response.setBody(copy);
        }
        // set the headers to the exact values
        response.setHeaders(new HashMap<String,String>(testResponse.getHeaders()));
        // set some value for required headers
        for (String header: testResponse.getRequiredHeaders())
        {
            response.getHeaders().put(header,"foo");
        }
        return response;
    }

    private static void populate(RestCallResponse r)
    {
        r.setStatusCode( ( (RANDOM.nextInt(4) + 1) * 100) + RANDOM.nextInt(2) );
        int headers = RANDOM.nextInt(HEADERS.length - 1) + 1;
        for (int i=0;i<headers; i++)
            r.getRequiredHeaders().add(random(REQUIRED_HEADERS));

        headers = RANDOM.nextInt(HEADERS.length - 1) + 1;
        for (int i=0;i<headers; i++)
            r.getBannedHeaders().add(random(BANNED_HEADERS));

        headers = RANDOM.nextInt(HEADERS.length - 1) + 1;
        for (int i=0;i<headers; i++)
            r.getHeaders().put(random(HEADERS),random(HEADER_VALUES));
    }
}
