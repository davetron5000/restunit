package com.gliffy.test.restunit.http;

import java.util.*;

import com.gliffy.restunit.http.*;
import com.gliffy.restunit.*;

/** A simple tree can respond to HTTP requests.
 */
public class RESTTree
{
    private Set<RESTTree> itsChildren;
    private String itsName;
    private Http itsHttp;
    private Http itsDefaultHttp;

    public RESTTree()
    {
        itsChildren = new HashSet<RESTTree>();
        itsDefaultHttp = new RESTTreeHttp()
        {
            public HttpResponse get(HttpRequest request)
            {
                boolean xml = false;
                if ("text/xml".equals(request.getHeaders().get("Accept")) )
                    xml = true;
                StringBuilder b = new StringBuilder();
                for (RESTTree child: getChildren())
                {
                    if (xml)
                        b.append("<child>");
                    b.append(child.getName());
                    if (xml)
                        b.append("</child>");
                    else
                        b.append("\n");
                }
                return createBytesGetResponse(b.toString().getBytes());
            }
        };
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
        if (itsHttp == null)
            return itsDefaultHttp;
        return itsHttp; 
    }

    public void setHttp(Http i) 
    {
        itsHttp = i; 
    }
}
