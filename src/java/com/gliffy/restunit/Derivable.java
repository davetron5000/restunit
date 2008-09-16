// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

import com.gliffy.restunit.http.*;

/** This derives a test from a given test.  An example where this is useful
 * is for a test of a <tt>GET</tt> method to a URL.  A REST framework should respond to <tt>HEAD</tt> requests by returning the exact
 * same headers, but with no content. 
 */
public interface Derivable
{
    /** Given a test, return a derived version based on whatever the interface claims it will derive.  Implementing this method <b>is implementing recursion</b>.
     * The tests returned by your implementation will be put through derivers, probably including the one you are implementing.  Be sure that there is some
     * case where you return null, or you will have infinite recursion on your hands.
     * @param test the test from which to derive
     * @param response the response received when the test was executed.  This is so you can examine the actual results returned from
     * the test in creating the derived test.
     * @return a new instance of RestTest derived from the parameter, or null if no such derivation was possible, given the test.
     */
    RestTest derive(RestTest test, HttpResponse response);
}
