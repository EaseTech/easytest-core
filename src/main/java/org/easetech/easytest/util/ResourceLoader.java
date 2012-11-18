
package org.easetech.easytest.util;

import java.io.FileWriter;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class to load the resource from classpath.
 * 
 * @author Anuj Kumar
 * 
 */
public class ResourceLoader {

    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(ResourceLoader.class);
    /**
     * The path to the file that needs to be loaded
     */
    private String filePath;

    /**
     * An instance of {@link ClassLoader}. This can be provided by the user and if it is not present, current threads
     * classloader is used.
     */
    private ClassLoader classLoader;

    /**
     * 
     * Construct a new ResourceLoader
     * 
     * @param filePath the path to the file that needs to be loaded
     * @param classLoader An instance of {@link ClassLoader}
     */
    public ResourceLoader(String filePath, ClassLoader classLoader) {
        this.filePath = filePath;
        this.classLoader = classLoader;
    }

    /**
     * 
     * Construct a new ResourceLoader
     * 
     * @param filePath the path to the file that needs to be loaded
     */
    public ResourceLoader(String filePath) {
        this.filePath = filePath;
        this.classLoader = null;
    }

    /**
     * Return an instance of Input stream for the provided {@link #filePath}
     * 
     * @return an instance of Input stream for the provided {@link #filePath}
     * @throws IOException if an I/O exception occurs
     */
    public InputStream getInputStream() throws IOException {
        InputStream is = null;
        ClassLoader classLoader = this.classLoader;
        if (this.classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        URL resource = classLoader.getResource(this.filePath);
        String path = null;
        if (resource != null) {
            path = resource.getPath();
        }

        LOG.debug("getInputStream() File absolute path:" + path);

        if (path == null) {
            throw new FileNotFoundException(filePath + " cannot be opened because it does not exist");
        }
        is = new FileInputStream(path);

        return is;
    }

    /**
     * Return an instance of FileOutputStream for the provided {@link #filePath}
     * 
     * @return an instance of FileWriter for the provided {@link #filePath}
     * @throws IOException if an I/O exception occurs
     */
    public FileOutputStream getFileOutputStream() throws IOException {
        FileOutputStream fos = null;
        ClassLoader classLoader = this.classLoader;
        if (this.classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        String path = classLoader.getResource(this.filePath).getPath();
        LOG.debug("getFileOutputStream File absolute path:" + path);
        if (path == null) {
            throw new FileNotFoundException(filePath + " cannot be opened because it does not exist");
        }

        fos = new FileOutputStream(path);
        return fos;
    }
    
    public FileWriter getFileWriter(Boolean appendData) throws IOException{
        FileWriter writer = null;
        ClassLoader classLoader = this.classLoader;
        if (this.classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
        }
        String path = classLoader.getResource(this.filePath).getPath();
        if (path == null) {
            throw new FileNotFoundException(filePath + " cannot be opened because it does not exist");
        }
        writer = new FileWriter(path, appendData);
        return writer;
        
    }

}
