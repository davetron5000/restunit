
# Overview 

RESTUnit is a set of Java classes that can be used to test REST services.  The data structure classes shall be serializable to and from a simple format, making the simplest way to create tests a matter of creating text files.  

Main features:

* Simple, human-readable/editable format for tests
* Programmatic, JUnit, TestNG, and ANT interface to testing process
* Results in JUnit format as option
* Ability to derive tests based on the conventions of REST as well as tester-supplied custom derivations
* Ability to customize result comparison with minimal custom code

# Details 

## Test Form 

A REST test is much simpler than an arbitrary unit test.  It is a matter of a HTTP requests and responses.

A test of a REST service endpoint (which would be a resource, identified by a URL) is a series of REST calls.  This could be simply one call (a GET to a resource that should return known data), or a more complex interaction of PUTing a new resource to the server, GETing that resource to see if it was received and DELETEing it to leave the remote data store in a known state.

### REST Call

We'll use the term "Call" to be one HTTP request and response cycle.  

The request can be as simple as:

* URL
* Method (GET/POST/PUT/DELETE/HEAD)
* parameters
* headers

The response is equally simple:

* Status Code
* Response content
* Response headers

We could represendt such a call in [YAML](http://en.wikipedia.org/wiki/YAML)  as so:

    url: /accounts/Initech/users
    method: GET
    parameters:
        - name: *bob*
        - sort: { ascending: true, by: last name}
    headers:
        If-None-Match: 71783837e5a4c543bce456
    response:
        status: 200
        body: <users><user id="34"><name>Bob 1</name></user><users id="556"><name>Bob 2</name></user></users>
        headers:
            Content-Type: { value: text/xml }
            Last-Modified: { required: true }

#### Why not just use HttpClient or HttpUnit?

These require programmatic construction of the tests.  They also don't have definitive meta-data that we can use to derive other tests (see below).  Further, describing tests in a declarative format
allows them to be used by other testing frameworks and not just Java.  If you were building a REST-based service, various programming languages could be used to interact with it.  Tests described as data, and not code, could be fed to any number of language libraries to test your service.  If you are writing client libraries for your service, this would save significant testing time.

#### Why YAML?

For starters, It's much more readable and editable than XML.  Further, embedding XML (or YAML, for that matter) is much simpler than with XML.  This allows the tests to be readable.

### Rest Test

A full-on test would be a series of calls.  This could easily be described using the format above.  Further, since a series of calls is made against the same URL, we can promote the URL up to the test level.
Calls could override the URL as needed (as for a POST that would create a new URL):

    url: /accounts/Initech/users/bolton
    - POST:
        - url: /accounts/Initech/users
        - stuff to put data
    - GET: # uses the parent URL
        - stuff for the get test
    - DELETE
    - GET
        response:
            status: 404

So far, this just looks like a simplistic way to test any HTTP endpoint.  However, we can use this tests to derive new ones, based upon the conventions of REST (or conventions your REST service provides), such as:
* URLs responding to GET should respond to HEAD in the same way, save for the body
* URLs responding to GET should send `ETag` and `Last-Modified` headers to allow for conditional gets
* URLs responding to GET should send a 304 if `If-None-Match` or `If-Modified-Since` headers are set to indicate the client has up-to-date data.
* PUT and DELETE methods may be tunneled over POST for clients/configurations that don't support it

Such derived tests would be virtually identical to their base counterparts, so why copy them?  Consider the URL `/accounts/Initech/users/bob/profile.xml` and supposed your REST service only sends ETags for JPEGs.  You could test this via:

    url: /accounts/Initech/users/bob/profile.xml
    - GET:
        respondsToIfNonMatch: false
        response:
            status: 200
            content: <profile id="234"><name>Mike Bolton</name></profile>
            contentType: text/xml
            
We could then derive this test:

    url: /accounts/Initech/users/bob/profile.xml
    - GET:
        respondsToIfNonMatch: false
        response:
            status: 200
            content: <profile id="234"><name>Mike Bolton</name></profile>
            contentType: text/xml
            headers:
                Last-Modified {required: true}
    - GET:
        respondsToIfNonMatch: false
        respondsToIfModifiedSince: false
        headers:
            If-Modified-Since: $Last-Modified$ # indicates to use the last tests response header
        response:
            status: 304
            content: <profile id="234"><name>Mike Bolton</name></profile>
            contentType: text/xml
    - HEAD:
        response:
            status: 200
            # body omitted means no body should be returned 
    - HEAD:
        headers:
            If-Modified-Since: 2008-01-01
        response:
            status: 304

## Comparing Results 

In a lot of cases, simple byte-for-byte comparisons of results could work.  Out of the box, we can provide:
* Ensure equality of status codes
* Check that certain headers are included (regardless of value)
* Check that certain headers are *not* included
* Check that certain headers are included and have a specific value
* byte-for-byte comparison of body received and body expected.

Further, we can provide a means of customizing the comparison.  For example, a user may be expecting XML that has temporal data in it.  If this can be ignored for the sake of the test, this should be easy to accomplish.

## Output Format 

JUnit's XML results file seems to be ubiquitous.  RESTUnit must output that so that integration with tools like Bamboo are possible.  Support for TestNG's output format may also be desirable, though this could be most easily achieved by allow RESTUnit to run as a TestNG test. Making up a new output format is probably NOT desirable.

