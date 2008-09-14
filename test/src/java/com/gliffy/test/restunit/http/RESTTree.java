package com.gliffy.test.restunit.http;

import java.util.*;

import com.gliffy.restunit.http.*;
import com.gliffy.restunit.*;
public class RESTTree
{
    private Set<RESTTree> itsChildren;
    private String itsName;
    private Http itsHttp;

    public RESTTree()
    {
        itsChildren = new HashSet<RESTTree>();
    }

    public void addChild(RESTTree tree)
    {
        itsChildren.add(tree);
    }

    public Set<RESTTree> getChildren()
    {
        return itsChildren;
    }

    /** Returns the name of the URL part of this tree */
    public String getName() 
    {
        return itsName; 
    }

    public void setName(String i) 
    {
        itsName = i; 
    }

    /** Returns an HTTP implementation that describes how this part of the REST service responds */
    public Http getHttp() 
    {
        return itsHttp; 
    }

    public void setHttp(Http i) 
    {
        itsHttp = i; 
    }
}
