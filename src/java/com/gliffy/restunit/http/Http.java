// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit.http;

/** Generic interface to HTTP protocol. */
public interface Http
{
    /** Performs an HTTP GET.
     * @param request the request describing the GET.
     * @return a response
     */
    HttpResponse get(HttpRequest request);
    /** Performs an HTTP HEAD. 
     * @param request the request describing the HEAD.
     * @return a response
     */
    HttpResponse head(HttpRequest request);
    /** Performs an HTTP PUT. 
     * @param request the request describing the PUT.
     * @return a response
     */
    HttpResponse put(HttpRequest request);
    /** Performs an HTTP POST.
     * @param request the request describing the POST.
     * @return a response
     */
    HttpResponse post(HttpRequest request);
    /** Performs an HTTP DELETE. 
     * @param request the request describing the DELETE.
     * @return a response
     */
    HttpResponse delete(HttpRequest request);
}
