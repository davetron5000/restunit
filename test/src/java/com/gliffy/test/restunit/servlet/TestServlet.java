package com.gliffy.test.restunit.servlet;

import java.io.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;

import org.apache.commons.logging.*;

/** A servlet that provides a very basic REST interface.
 * Useful in deploying into a container for the purposes of testing
 * actual HTTP calls and not just mocked ones.
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
        doGetHead(request,response,false);
    }

    protected void doHead(HttpServletRequest request, HttpServletResponse response)
        throws IOException
    {
        doGetHead(request,response,true);
    }

    private boolean clientCacheIsUsable(HttpServletRequest request)
    {
        long clientLastModTime = request.getDateHeader("If-Modified-Since") / 1000;
        long dataLastModTime = getLastModifiedDate(request) == null ? 0 : getLastModifiedDate(request).getTime() / 1000;

        if (clientLastModTime != -1)
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
            return clientETag.equals(dataETag);

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
        String path = getPath(request);
        itsDatabase.put(path,data);
        updateHashes(path,data);
    }

    private void deleteData(HttpServletRequest request)
    {
        String path = getPath(request);
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
        String data = getData(request);
        if (data != null)
        {
            if (clientCacheIsUsable(request))
            {
                itsLogger.debug("Client data is good");
                response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            }
            else
            {
                itsLogger.debug("Client data is out of date");
                response.setHeader("ETag",getETag(request));
                itsLogger.debug("Setting ETag header to " + getETag(request));
                Date lastMod = getLastModifiedDate(request);
                if (lastMod != null)
                    response.setDateHeader("Last-Modified",lastMod.getTime());
                response.setStatus(HttpServletResponse.SC_OK);
                if (data.startsWith("<?xml"))
                    response.setContentType("text/xml");
                else
                    response.setContentType("text/plain");
                if (!head)
                    response.getWriter().println(data);
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
