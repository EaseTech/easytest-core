package org.easetech.easytest.io;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public interface Resource {
    
    boolean exists();
    
    InputStream getInputStream() throws IOException;
    
    OutputStream getOutputStream() throws IOException;
    
    URL getURL();
    
    File getFile() throws Exception;
    
    String getResourceName();
    
    

}
