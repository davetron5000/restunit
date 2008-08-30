package com.gliffy.restunit;

/** This derives a test from a given test.  An example where this is useful
 * is for a test of a <tt>GET</tt> method to a URL.  A REST framework should respond to <tt>HEAD</tt> requests by returning the exact
 * same headers, but with no content. 
 *
 * Be careful imlementing this method; all tests encountered by the system will be put through configured Derivable instances, so your Derivable
 * implementation must return null if no such derivation is posisble.
 */
public interface Derivable
{
    /** Given a test, return a derived version based on whatever the interface claims it will derive.
     * @param test the test from which to derive
     * @return a new instance of RestTest derived from the parameter, or null if no such derivation was possible, given the test.
     */
    RestTest derive(RestTest test);
}
