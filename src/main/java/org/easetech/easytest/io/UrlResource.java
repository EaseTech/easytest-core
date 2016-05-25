package org.easetech.easytest.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import org.easetech.easytest.util.CommonUtils;
import org.jfree.util.Log;
import org.junit.Assert;

/**
 * A {@link Resource} implementation for the URL based resources.
 * 
 * @author Anuj Kumar
 *
 */
public class UrlResource implements Resource {

    /**
     * The path of the resource
     */
    private String path;
    
    /**
     * The URL of the resource
     */
    private URL url;

    /**
     * 
     * Construct a new UrlResource from the given path
     * @param path
     * @throws MalformedURLException
     */
    public UrlResource(String path) throws MalformedURLException {
        if (path == null || path.length() <= 0) {
            Assert.fail("The supplied path must be a non empty and Not Null value");
        }
        this.path = path;
        this.url = new URL(path);
    }
    
    /**
     * 
     * Construct a new UrlResource from the given URL
     * @param url
     */
    public UrlResource(URL url) {
        if (url == null || url.toString().length() <= 0) {
            Assert.fail("The supplied path must be a non empty and Not Null value");
        }
        this.path = url.getPath();
        this.url = url;
    }
    


    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }


    /**
     * Checks whether the URL resource exists or not.
     * 
     * @return true if exists, false otherwise
     */
    public boolean exists() {
        try {
            HttpURLConnection.setFollowRedirects(false);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("HEAD");
            return (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            Log.debug("Exception occured while trying to find whether the resource exists or not ", e);
            return false;
        }
    }

    /**
     * Get the input stream associated with the resource
     * @return input stream associated with the resource
     * @throws IOException
     */
    public InputStream getInputStream() throws IOException{
        URLConnection con = this.url.openConnection();
        con.setUseCaches(false);
        return con.getInputStream();
        
    }

    /**
     * Get the URL associated with the resource
     * @return the URL associated with the resource
     */
    public URL getURL() {
        return url;
    }

    /**
     * Get the File associated with the resource
     * @return the File associated with the resource
     * @throws Exception
     */
    public File getFile() throws Exception {
        URI uri = CommonUtils.toURI(getURL());
        File file;
        if (uri != null) {
            file = CommonUtils.getFile(uri, uri.toString());
        } else {
            file = CommonUtils.getFile(getURL(), null);
        }
        return file;
    }
    
    /**
     * Get the resource name
     * @return
     */
    public String getResourceName(){
        try {
            return getFile().getName();
        } catch (Exception e) {
            Log.debug("Exception occured while trying to get the name of the resource",e);
            return this.path;
        }
    }

    /**
     * Get the {@link OutputStream} associated with the resource
     */
    public OutputStream getOutputStream() throws IOException{
        URLConnection con = this.url.openConnection();
        con.setUseCaches(false);
        return con.getOutputStream();
    }

    /**
     * @return
     */
    public String toString() {
        return "UrlResource [path=" + path + ", URL=" + url + "]";
    }

}
