package com.gliffy.restunit;

import java.io.*;
import java.util.*;

/** An expected response */
public class RestTestResponse implements Serializable, Cloneable
{
   private int itsStatusCode;
   /** The expected status code */
   public int getStatusCode() { return itsStatusCode; }
   public void setStatusCode(int i) { itsStatusCode = i; }

   private Set<String> itsRequiredHeaders;
   /** Entries here indicate that the header <b>must</b> be part of the response.  If a header is in the Map returned
    * by getHeaders, you don't need to duplicate it here.  Only use this if you just care that the header was included
    */
   public Set<String> getRequiredHeaders() 
   { 
       if (itsRequiredHeaders == null)
           itsRequiredHeaders = new HashSet<String>();
       return itsRequiredHeaders; 
   }
   public void setRequiredHeaders(Set<String> i) { itsRequiredHeaders = i; }

   private Set<String> itsBannedHeaders;
   /** Entries here indicate that the header <b>must not</b> be part of the response */
   public Set<String> getBannedHeaders() 
   { 
       if (itsBannedHeaders == null)
           itsBannedHeaders = new HashSet<String>();
       return itsBannedHeaders; 
   }
   public void setBannedHeaders(Set<String> i) { itsBannedHeaders = i; }

   private Map<String,String> itsHeaders;
   /** Entries here indicate that a header is required and that it should have the given value in order for the test to pass */
   public Map<String,String> getHeaders() 
   { 
       if (itsHeaders == null)
           itsHeaders = new HashMap<String,String>();
       return itsHeaders; 
   }
   public void setHeaders(Map<String,String> i) { itsHeaders = i; }

   public Object clone()
   {
       try
       {
           RestTestResponse clone = (RestTestResponse)super.clone();
           clone.setHeaders(new HashMap<String,String>(getHeaders()));
           clone.setBannedHeaders(new HashSet<String>(getBannedHeaders()));
           clone.setRequiredHeaders(new HashSet<String>(getRequiredHeaders()));
           return clone;
       }
       catch (CloneNotSupportedException e)
       {
           throw new RuntimeException(e);
       }
   }
}
