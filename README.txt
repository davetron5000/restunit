#summary Features of RestUnit
#labels Phase-Requirements,Featured

= Overview =

RESTUnit is a set of Java classes that can be used to test REST services.  The data structure classes shall be serializable to and from a simple format, making the simplest way to create tests a matter of creating text files.  

Main features:

  * Simple, human-readable/editable format for tests
  * Programmatic, JUnit, TestNG, and ANT interface to testing process
  * Results in JUnit format as option
  * Ability to derive tests based on the conventions of REST as well as tester-supplied custom derivations
  * Ability to customize result comparison with minimal custom code

= Details =

== Test Form ==

A REST test is much simpler than an arbitrary unit test.  It is a matter of an HTTP request and an HTTP response.

The request can be as simple as:
  * URL
  * Method (GET/POST/PUT/DELETE/HEAD)
  * parameters
  * headers
The response is equally simple:
  * Status Code
  * Response content
  * Response headers

From this, we can derive tests:
  * A GET test can be used to automatically create the same test that uses the HEAD method but expects no body in return
  * A GET test can check to see if `Last-Modified` was sent; if so, a new test can be derived that sends `If-Last-Modified` and checks that a 304 was returned and not a body
  * Same for `ETag` and `If-None-Match`
  * If tunneling is allowed, we can run all PUT and DELETE tests over POST setting `X-HTTP-Method-Override` (or some tester-configured mechanism)

Further, there is no reason to express these tests in Java code.

=== Test File Format ===

[http://en.wikipedia.org/wiki/YAML YAML] is a good format for the tests.  It's superior to XML for this purpose, mainly because of it's readability and ease with which XML can be embedded (embedding XML in XML is a monumental pain). 

Unfortunately, the two YAML serializers for Java seem to be dead.  The seemingly superior one, JYAML, has some annoyances regarding deserialization (seems to require explicit class naming, or it uses a hashtable).

An option would be to write a minimal YAML parser that serializes only the subset of YAML needed for the data structures here.

== Comparing Results ==

In a lot of cases, simple byte-for-byte comparisons of results could work.  Out of the box, we can provide:
  * Ensure equality of status codes
  * Check that certain headers are included (regardless of value)
  * Check that certain headers are *not* included
  * Check that certain headers are included and have a specific value
  * byte-for-byte comparison of body received and body expected.

Further, we can provide a means of customizing the comparison.  For example, a user may be expecting XML that has temporal data in it.  If this can be ignored for the sake of the test, this should be easy to accomplish.

== Output Format ==

JUnit's XML results file seems to be ubiquitous.  RESTUnit must output that so that integration with tools like Bamboo are possible.  Support for TestNG's output format may also be desirable, though this could be most easily achieved by allow RESTUnit to run as a TestNG test. Making up a new output format is probably NOT desirable.

== Unit vs. Functional Test and Test Order ==

The most complete test of a REST service is against the actual service itself, backed by actual data.  This avoids the need to stub or mock the service and gives the most solid results.  Accomplishing this requires coordination with the backing store that may be difficult.

There should be support for this in a few different ways:

  * Ability to indicate which tests have no dependents
  * Ability to chain tests via dependency
  * Ability to specify an ordered group of tests that are run as a "functional" test

The idea is that we assume some sort of database/server reset prior to a group of tests, and that we can ensure tests run in a particular order.  This allows the developer to test the creation of data, and then, using the REST service, clean up that data.

=== Mocking the backing store ===

Presumably, a REST service has three layers:

  # REST endpoint
  # Business logic
  # Data

REST tests will ideally test the first two layers; As mentioned above, we wish to support using the actual database with known test data.  This may be inconvenient or impossible.  Tools exist to mock the database so as to still be able to test business logic.  Integrating (or rather, not preventing) this is desirable.  This may require some investigation into database mocking technology.