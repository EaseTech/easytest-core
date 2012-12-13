package org.easetech.easytest.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

/**
 * 
 * An empty implementation of the resource, useful in cases where the user loads the data from Java Class
 * and thus does not require to load a resource explicitly
 *
 */
public class EmptyResource implements Resource {

    public boolean exists() {
        
        return false;
    }

    public InputStream getInputStream() throws IOException {
        
        return null;
    }

    public OutputStream getOutputStream() throws IOException {
        
        return null;
    }

    public URL getURL() {
        
        return null;
    }

    public File getFile() throws Exception {
        
        return null;
    }

    public String getResourceName() {
       
        return null;
    }

}
