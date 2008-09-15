#summary Features of RestUnit
#labels Phase-Requirements,Featured

= Overview =

RESTUnit will be a set of Java classes that can be used to test REST services.  The data structure classes shall be serializable to and from a simple format, making the simplest way to create tests a matter of creating text files.  

Main features:

  * Simple, human-readable/editable format for tests
  * Programmatic interface to testing process
  * Results in JUnit format as option
  * Ability to easily run specific tests or groups of tests
  * Ability to run tests as TestNG and/or JUnit tests
  * Ability to easily run conditional get tests, head tests and SSL tests

= Details =

== Test Form ==

A REST test needs the following information:
  * Request
    * URL
    * Method
    * Parameters
    * headers
  * Expected Response
    * Status Code
    * Response content
    * Response headers
  * Meta-data
    * Name
    * Description
    * Groupings
    * Dependents

These don't need to be expressed in Java code.

== File Format ==

[http://en.wikipedia.org/wiki/YAML YAML] is a good format for the tests.  It's superior to XML for this purpose, mainly because of it's readability and ease with which XML can be embedded (embedding XML in XML is a monumental pain).

Unfortunately, the two YAML serializers for Java seem to be dead.  The seemingly superior one, JYAML, has some annoyances regarding deserializable (seems to require explicity class naming, or it uses a hashtable).

An option would be to write a minimal YAML parser that serializes only the subset of YAML needed for the data structures here.

== Programmatic Interface ==

Ideally, one wouldn't need to use a lot of code when testing a REST service; they should be able to create the test files and point the testing framework to them.  

If this is insufficient, there should be a few levels of programmatic interface.

The next most complex would be to customize the comparison of results.  By default, RESTUnit will require results to match exactly.  This may be inconvenient for results that contains temporal data, such as dates, or generated data such as database keys.  As such, a custom comparison algorithm should be employable to compare the meat of the results and ignore such irrelevant data.

If that isn't sufficient, a developer should be able to programmatically execute the Rest Test and examine the entirety of the results, however he sees fit.  This would be done inside a TestNG or JUnit test case, presumably.

== Output Format ==

JUnit's XML results file seems to be ubiquitous.  RESTUnit must output that so that integration with tools like Bamboo are possible.  Support for TestNG's output format may also be desirable. Making up a new output format is probably NOT desirable.

== Derived Tests ==

To avoid repeating ourselves, it would be ideal if there were support for deriving tests from user-created tests.  For example, if we decide that tunneling PUT and DELETE over POST is allowed via the {{{X-HTTP-Method-Override}}} header, RESTUnit should be able to run all PUT and DELETE tests twice: once using PUT or DELETE as the HTTP method and once using POST, and the header.

Further, test can be derived for SSL (to ensure that non-SSL requests that succeed also succeed over SSL, or not), performing a HEAD and ensuring it behaves as it should compared to GET, performing conditional GETs, checking for ETags, etc.

Finally, a developer may wish to provide their own derivation mechanism, to avoid repeating tests.

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
