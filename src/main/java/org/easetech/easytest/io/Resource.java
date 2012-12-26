package org.easetech.easytest.io;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * Interface representing a generic Resource to be used by the EasyTEst framework.
 * Three implementations exists for this interface in the EasyTest framework:
 * <li> {@link ClasspathResource} - Resources that exists in the Classpath</li>
 * <li> {@link FileSystemResource} - Resources that exists in the FileSystem</li>
 * <li> {@link UrlResource} - Resource that exists as a URL(http , ftp) etc
 * 
 * @author Anuj Kumar
 */
public interface Resource {
    
    /**
     * Indicates whether the resource exists or not
     * @return boolean
     */
    boolean exists();
    
    /**
     * Get the {@link InputStream} associated with the resource
     * @return {@link InputStream}
     * @throws IOException if an IOException occurs
     */
    InputStream getInputStream() throws IOException;
    
    /**
     * Get the {@link OutputStream} associated with the resource
     * @return {@link OutputStream}
     * @throws IOException if an IOException occurs
     */
    OutputStream getOutputStream() throws IOException;
    
    /**
     * get the {@link URL} associated with the resource
     * @return {@link URL}
     */
    URL getURL();
    
    /**
     * Get the {@link File} associated with the resource
     * @return {@link File}
     * @throws Exception
     */
    File getFile() throws Exception;
    
    /**
     * Get the resource name
     * @return String
     */
    String getResourceName();
    
    

}
