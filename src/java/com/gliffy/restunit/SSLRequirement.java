package com.gliffy.restunit;

/** Indicates requirements for a test regarding SSL support.  You may want your services to require (or deny) SSL.  You may not care.
 */
public enum SSLRequirement
{
    /** Either SSL or non-SSL is OK */
    OPTIONAL,
    /** SSL is required, non-SSL is an error */
    REQUIRED,
    /** SSL should never be used and is an error */
    NEVER,
};
