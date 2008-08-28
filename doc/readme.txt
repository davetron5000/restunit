= RESTUnit =
David Copeland <davetron5000@NOSPAM.gmail.com>
v1.0, Nov 2007

RestUnit is a unit and functional testing framework for REST-based services.  Essentially, it is a simplified HTTP testing framework.  It allows you to specify a URL, HTTP method, parameters and headers, and verify a request against a response.

== Overview ==

REST is a simple method for creating web services.  As such, tests of REST services are canonical:
# Create a request
# Submit the request to a server
# Receive a response
# Examine the response for correctness

RESTUnit provides the scaffolding for this testing procedure and requires only that you specify the request and response, in the form of YAML files.

== Key Technologies ==

=== HTTP ===

HTTP is short for **H**yper**t**ext **T**ransfer **P**rotocol, and is the protocol used for interacting with web servers.  This is tested and known protocol that is well understood well-documented, widely supported, and easy to use.

=== REST ===

REST stands for **Re**presentational **S**tate **T**ransfer  and describes a way in which HTTP can be used to design and deploy web services.  a URL is used to identify a resource to be operated upon.  The HTTP method defines the operation to perform.  Headers and query string parameters can be used to configure the requests.  Responses are communicated via an HTTP status code and a body, encoded in XML, JSON, or any other encoding (often called the "representation").

=== YAML ===

An alternative to XML, YAML (**Y**AML's **A**in't a **M**arkup **L**anguage) purpots to be a more human readable data serialization scheme.  The key to RESTUnit being simple to use is to allow tests to be written, understood and maintained in the simplest manner possible.  Since may web services responde with XML, HTML, or JSON, and since encoding those languages in themselves is so painful, YAML is an obvious choice.

== Writing a Test ==

A simple RESTUnit test looks like so:

 url: http://www.google.com
 method: GET
 result:
    status: 200

This test means that an HTTP ++GET++ to the URL ++http://www.google.com++ should return an HTTP status code of 200


== Links ==

* link:http://vim.wikia.com/wiki/Vim_Doclet[Vim wikia entry]

