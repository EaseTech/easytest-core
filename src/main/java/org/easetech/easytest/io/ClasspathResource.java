
package org.easetech.easytest.io;

import com.sun.org.apache.bcel.internal.classfile.ClassParser;

import java.io.FileNotFoundException;

import java.io.FileOutputStream;

import java.io.OutputStream;

import java.net.URISyntaxException;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import org.junit.Assert;

/**
 * An instance of {@link Resource} interface for handling Classpath resources
 *
 */
public class ClasspathResource implements Resource {

    /**
     * The {@link ClassLoader} to load the class path resource
     */
    private ClassLoader classLoader;

    /**
     * The path of the classpath resource
     */
    private String path;

    /**
     * The Class object that can be used to load the resource
     */
    private Class<?> classObj;

    /**
     * 
     * Construct a new ClasspathResource and defaults the {@link #classLoader} with current Threads ContextClassLoader
     * @see (Thread#currentThread())'s getContextClassLoader
     * @param path The path of the classpath resource
     */
    public ClasspathResource(String path) {
        if (path == null || path.isEmpty()) {
            Assert.fail("The supplied path must be a non empty and Not Null value");
        }
        this.path = path;
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    /**
     * 
     * Construct a new ClasspathResource instance such that the resource 
     * will be loaded using the given ClassLoader
     * @param path the path of the classpath resource
     * @param classLoader The {@link ClassLoader} to load the class path resource
     */
    public ClasspathResource(String path, ClassLoader classLoader) {
        if (path == null || path.isEmpty()) {
            Assert.fail("The supplied path must be a non empty and Not Null value");
        }
        this.path = path;
        this.classLoader = classLoader;
    }

    /**
     * 
     * Construct a new ClasspathResource instance such that the resource
     * will be loaded using the given Class object.
     * @param path the path of the classpath resource
     * @param classObj The {@link Class} to load the class path resource
     */
    public ClasspathResource(String path, Class<?> classObj) {
        if (path == null || path.isEmpty()) {
            Assert.fail("The supplied path must be a non empty and Not Null value");
        }
        this.path = path;
        this.classObj = classObj;
    }

    public ClasspathResource(String path, ClassLoader classLoader, Class<?> classObj) {
        if (path == null || path.isEmpty()) {
            Assert.fail("The supplied path must be a non empty and Not Null value");
        }
        this.path = path;
        this.classObj = classObj;
        this.classLoader = classLoader;
    }

    /**
     * @return the classLoader
     */
    public ClassLoader getClassLoader() {
        return this.classLoader != null ? this.classLoader : this.classObj != null ? this.classObj.getClassLoader()
            : Thread.currentThread().getContextClassLoader();
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the classObj
     */
    public Class<?> getClassObj() {
        return classObj;
    }

    public boolean exists() {
        return getClassObj() != null ? (getClassObj().getResource(getPath()) != null ? true : getClassLoader().getResource(
            getPath()) != null) : getClassLoader().getResource(
                getPath()) != null;
    }

    public InputStream getInputStream() {
        InputStream is = null;
        if (getClassObj() != null) {
            is = getClassObj().getResourceAsStream(getPath());
            if (is == null) {
                is = getClassLoader().getResourceAsStream(getPath());
            }
        } else {
            is = getClassLoader().getResourceAsStream(getPath());
        }
        if (is == null) {
            throw new RuntimeException("File : " + getPath() + " cannot be opened because it does not exist");
        }
        return is;

    }

    public URL getURL() {
        URL url = null;
        if (getClassObj() != null) {
            url = getClassObj().getResource(getPath());
            if (url == null) {
                url = getClassLoader().getResource(getPath());
            }
        } else {
            url = getClassLoader().getResource(getPath());
        }
        if (url == null) {
            throw new RuntimeException("File : " + getPath() + " cannot be opened because it does not exist");
        }
        return url;
    }

    public File getFile() {
        File file = null;
        URL url = getURL();
        if (url != null) {
            try {
                file = new File(url.toURI());
            } catch (URISyntaxException e) {
                Assert
                    .fail("URISyntaxException occured while trying to get a URI from a given URL : "
                        + url.toString()
                        + " . This mainly occurs if this URL is not formatted strictly according to to RFC2396 and thus cannot be converted to a URI. Exception message is : "
                        + e.getMessage());
            }
            
        }
        return file;
    }

    public String getResourceName() {
        if(getURL() != null){
            return getURL().getPath();
        }
        return getPath();
    }

    
    public OutputStream getOutputStream() {
        OutputStream outputStream = null;
        if(getFile() != null){
            try {
                outputStream = new FileOutputStream(getFile());
            } catch (FileNotFoundException e) {
                try {
                    outputStream = new FileOutputStream(getResourceName());
                } catch (FileNotFoundException e1) {
                    throw new RuntimeException(getResourceName() + " cannot be opened because it does not exist");
                }
                
            }
        }
        return outputStream;
    }

}
