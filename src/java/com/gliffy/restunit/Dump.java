// Copyright (c) 2008 by David Copeland
// Licensed under the Apache License, Version 2.0
// Available in LICENSE.txt with this code or
// at http://www.apache.org/licenses/LICENSE-2.0

package com.gliffy.restunit;

import java.io.*;
import java.util.*;

import org.ho.yaml.*;

/** Utility prog that will go away someday. */
public class Dump
{
    private static final int OK = 200;
    private Dump() {}

    /** main method. 
     * @param args cli arguments.
     * @throws Exception for anything that goes wrong.
     * */
    public static void main(String args[])
        throws Exception
    {

        if (args.length == 0)

        {

            RestTest test = new RestTest();
            test.setURL("/rest/this/is/a/request");
            test.setMethod("GET");
            test.setParameters(new HashMap<String,List<String>>());
            List<String> params = new ArrayList<String>();
            params.add("foo");
            params.add("bar");
            test.getParameters().put("cruddo",params);
            params = new ArrayList<String>();
            params.add("yay");
            test.getParameters().put("bleorgh",params);

            test.setHeaders(new HashMap<String,String>());
            test.getHeaders().put("Accept","text/xml");
            test.getHeaders().put("X-HTTP-Method-Override","PUT");

            RestTestResponse response = new RestTestResponse();
            response.setStatusCode(OK);

            test.setResponse(response);
            test.setName("Dummy Test");
            test.setDescription("This is a dummy test to see what's what");
            test.setSSLRequirement(SSLRequirement.REQUIRED);

            String yaml = Yaml.dump(test,true);
            System.out.println(yaml);

        }
        else

        {

            File f = new File(args[0]);
            Object o = Yaml.load(f);
            System.out.println(o.getClass().getName().toString());
            System.out.println(o.toString());
            System.out.println(Yaml.dump(o,true));

        }

    }

}
