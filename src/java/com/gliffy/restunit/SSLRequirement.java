// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

/** Indicates requirements for a test regarding SSL support.  You may want your services to require (or deny) SSL.  You may not care.
*/
public enum SSLRequirement

{

    /** Either SSL or non-SSL is OK. */
    OPTIONAL,
        /** SSL is required, non-SSL is an error. */
        REQUIRED,
        /** SSL should never be used and is an error. */
        NEVER,

};
