package com.gliffy.test.restunit.servlet;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;

/** A servlet that provides a very basic REST interface.
 * Useful in deploying into a container for the purposes of testing
 * actual HTTP calls and not just mocked ones.
 * It works as follows:
 * <ul>
 * <li>On startup, init params in <tt>web.xml</tt> map URLs to data. 
 * <li>Any data mapped to a URL can get "gotten", and the etag and last-update date will be set based on the
 * time the data was inserted</li>
 * <li>An unmapped URL can be PUT to</li>
 * <li>A mapped URL can be DELETEed from</li>
 * <li>A POST to a mapped URL will change the data</li>
 * <li>A POST to an unampped URL will create a new URL based on the URL and the "name" attribute<li>
 * <li>HEAD does a GET but returns no body</li>
 * <li>A DELETE to any URL with the parameter "reset" set to "true" re-initializes the data</li>
 * </ul>
 * Further, the service as the following bugs:
 * <ul>
 * <li>If the data PUT or POSTed is all numeric, an exception is thrown</li>
 * <li>A DELETE to a URL with one part (e.g. "/blah") will not have an effect</li>
 * <li>A GET requesting "text/html" will return "text/xml"</li>
 * <li>HEAD requests omit the ETag header</li>
 * </ul>
 */
public class TestServlet extends HttpServlet
{
    private Map<String,String> itsDatabase;
    private Map<String,Date> itsModificationDates;
    private Map<String,String> itsETags;
    private Map<String,String> itsInitialDatabase;
    private Log itsLogger = LogFactory.getLog(getClass());

    public void init()
    {
        itsInitialDatabase = new HashMap<String,String>();
        ServletConfig config = getServletConfig();
        Enumeration e = config.getInitParameterNames();
        while (e.hasMoreElements())
        {
            String name = (String)e.nextElement();
            if (name.startsWith("/"))
            {
                String url = name;
                String value = config.getInitParameter(name);
                itsLogger.debug("Mapping " + url + " to data " + value);
                itsInitialDatabase.put(url,value);
            }
        }
        initializeData();
    }

    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        itsLogger.debug("DELETE of " + getPath(request));
        String data = findData(request);
        if (data != null)
        {
            deleteData(request);
            response.setStatus(HttpServletResponse.SC_OK);
        }
        else
        {
            itsLogger.warn("No data at " + getPath(request));
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        itsLogger.debug("GET of " + getPath(request));
        doGetHead(request,response,false);
    }

    protected void doHead(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        itsLogger.debug("HEAD of " + getPath(request));
        doGetHead(request,response,true);
    }

    private boolean clientCacheIsUsable(HttpServletRequest request)
    {
        Enumeration e = request.getHeaderNames();
        while (e.hasMoreElements())
        {
            String header = (String)e.nextElement();
            itsLogger.debug("'" + header + "' == '" + request.getHeader(header) + "'");
        }
        long clientLastModTime = request.getDateHeader("If-Modified-Since") / 1000;
        long dataLastModTime = getLastModifiedDate(request) == null ? 0 : getLastModifiedDate(request).getTime() / 1000;

        if (clientLastModTime != 0)
        {
            itsLogger.debug("Client's last mod time : " + clientLastModTime);
            itsLogger.debug("Data's last mod time : " + dataLastModTime);
            if (clientLastModTime >= dataLastModTime)
                return true;
        }
        itsLogger.debug("Comparing etags");

        String clientETag = request.getHeader("If-None-Match");
        String dataETag = getETag(request);

        if ( (clientETag != null) && (dataETag != null) )
        {
            itsLogger.debug("Comparing '" + clientETag + "'  to our '" + dataETag + "'");
            return clientETag.equals(dataETag);
        }
        else
        {
            itsLogger.debug("one of the etags was null " + (clientETag == null ? "client's" : "our's"));
        }

        return false;
    }

    /** Post does two things.
     * <ul>
     * <li>If posting to an existing URL, it replaces the data there with the 'data' parameter</li>
     * <li>If posting to a non-existing URL, it puts data at post_url/name_param with the contents of the data param</li>
     * </ul>
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        itsLogger.debug("POST to " + getPath(request));
        String newData = request.getParameter("data");
        if (newData == null)
        {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,"'data' is required");
            return;
        }

        String data = findData(request);
        if (data == null)
        {
            itsLogger.debug("Creating data");
            String name = request.getParameter("name");
            if (name == null)
            {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,"'name' is required");
                return;
            }
            else
            {
                String path = saveNewUrl(request,newData,name);
                itsLogger.debug(path + " created");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(path);
            }
        }
        else
        {
            itsLogger.debug("Updating data");
            saveData(request,newData);
            response.setStatus(HttpServletResponse.SC_OK);
        }
    }

    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
        throws IOException
    {
        itsLogger.debug("PUT to " + getPath(request));
        String data = findData(request);
        if (data == null)
        {
            data = readBody(request);
            itsLogger.debug("Putting " + data);
            saveData(request,data);
            response.setStatus(HttpServletResponse.SC_CREATED);
        }
        else
        {
            itsLogger.warn("Data already exists at " + getPath(request));
            response.sendError(HttpServletResponse.SC_FORBIDDEN,"There is data there already");
        }
    }

    private String readBody(HttpServletRequest request)
        throws IOException
    {
        BufferedReader r = request.getReader();
        StringBuilder b = new StringBuilder();
        String line = r.readLine();
        while (line != null)
        {
            b.append(line);
            line = r.readLine();
        }
        return b.toString();
    }

    private String findData(HttpServletRequest request)
    {
        return findData(request,null);
    }

    private String findData(HttpServletRequest request, String extra)
    {
        if (extra == null)
            return itsDatabase.get(getPath(request));
        else
            return itsDatabase.get(getPath(request) + "/" + extra);
    }

    private String getData(HttpServletRequest request)
    {
        String path = getPath(request);
        return itsDatabase.get(path);
    }

    private void saveData(HttpServletRequest request, String data)
    {
        try
        {
            Long.parseLong(data);
            throw new RuntimeException("Dunno what just happened?!?!??");
        }
        catch (NumberFormatException e) { }
        String path = getPath(request);
        itsDatabase.put(path,data);
        updateHashes(path,data);
    }

    private void deleteData(HttpServletRequest request)
    {
        String path = getPath(request);
        if (path.lastIndexOf("/") <= 0)
            return;
        itsDatabase.remove(path);
        itsModificationDates.remove(path);
        itsETags.remove(path);
    }

    private String saveNewUrl(HttpServletRequest request, String data, String name)
    {
        String path = getPath(request) + "/" + name;
        itsDatabase.put(path,data);
        updateHashes(path,data);
        return path;
    }

    private void initializeData()
    {
        itsModificationDates = new HashMap<String,Date>();
        itsETags = new HashMap<String,String>();
        itsDatabase = new HashMap<String,String>(itsInitialDatabase);
        for (String url: itsDatabase.keySet())
        {
            String value = itsDatabase.get(url);
            updateHashes(url,value);
        }
    }

    private void updateHashes(String url, String value)
    {
        itsModificationDates.put(url,new Date());
        itsETags.put(url,String.valueOf(calculateETag(value)));
    }

    private Date getLastModifiedDate(HttpServletRequest request)
    {
        String path = getPath(request);
        return itsModificationDates.get(path);
    }

    private String getETag(HttpServletRequest request)
    {
        String path = getPath(request);
        return itsETags.get(path);
    }

    private String calculateETag(String data)
    {
        if (data == null)
            return null;
        return String.valueOf(data.hashCode());
    }


    private void doGetHead(HttpServletRequest request, HttpServletResponse response, boolean head)
        throws IOException
    {
        String reset = request.getParameter("reset");
        if ("true".equals(reset))
        {
            initializeData();
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        else
        {
            itsLogger.debug("'reset' was " + reset);
        }
        String data = getData(request);
        if (data != null)
        {
            itsLogger.debug("Data at the url");
            if (clientCacheIsUsable(request))
            {
                itsLogger.debug("Client data is good");
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
                return;
            }
            else
            {
                itsLogger.debug("Client data is out of date");
                if (!head)
                {
                    response.setHeader("ETag",getETag(request));
                    itsLogger.debug("Setting ETag header to " + getETag(request));
                }
                Date lastMod = getLastModifiedDate(request);
                if (lastMod != null)
                    response.setDateHeader("Last-Modified",lastMod.getTime());
                response.setStatus(HttpServletResponse.SC_OK);
                String acceptHeader = request.getHeader("Accept");
                if (acceptHeader.indexOf("text/html") != -1)
                {
                    response.setContentType("text/xml;charset=UTF-8");
                }
                else if (acceptHeader.indexOf("text/xml") != -1)
                {
                    response.setContentType("text/xml;charset=UTF-8");
                }
                else if (acceptHeader.indexOf("text/plain") != -1)
                {
                    response.setContentType("text/plain;charset=UTF-8");
                }
                else if (acceptHeader.indexOf("*/*") != -1)
                {
                    response.setContentType("text/plain;charset=UTF-8");
                }
                else
                {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND,acceptHeader + " is not a supported mime type");
                    return;
                }
                if (!head)
                    response.getWriter().print(data);
            }
        }
        else
        {
            itsLogger.debug(getPath(request) + " not found");
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    private String getPath(HttpServletRequest request)
    {
        String path = request.getPathInfo();
        if (path.endsWith("/"))
            return path.substring(0,path.length() - 1);
        else
            return path;
    }

}
