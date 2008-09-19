// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

import com.gliffy.restunit.http.*;

/** This derives a call from a given call.  An example where this is useful
 * is for a call of a <tt>GET</tt> method to a URL.  A REST framework should respond to <tt>HEAD</tt> requests by returning the exact
 * same headers, but with no content. 
 */
public interface Derivable
{
    /** Given a call, return a derived version based on whatever the interface claims it will derive.  Implementing this method <b>is implementing recursion</b>.
     * The calls returned by your implementation will be put through derivers, probably including the one you are implementing.  Be sure that there is some
     * case where you return null, or you will have infinite recursion on your hands.
     * @param call the call from which to derive
     * @param response the response received when the call was executed.  This is so you can examine the actual results returned from
     * the call in creating the derived call.
     * @return a new instance of RestCall derived from the parameter, or null if no such derivation was possible, given the call. 
     */
    RestCall derive(RestCall call, HttpResponse response);
}
