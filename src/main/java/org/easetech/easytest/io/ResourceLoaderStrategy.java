package org.easetech.easytest.io;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link ResourceLoader} that defines a strategy to load different types of resources based on
 * user provided values. It loads three different types of resources : <li> {@link ClasspathResource} - Resource
 * representing the Classpath based resources(prefixed with classpath:)</li> <li> {@link FileSystemResource} - Resource
 * based on FileSystem (for eg. in your C: drive)</li> <li> {@link UrlResource} - Resource based on URL (http for eg)
 * 
 * @author Anuj Kumar
 * 
 */
public class ResourceLoaderStrategy implements ResourceLoader {

    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(ResourceLoaderStrategy.class);

    /** the class loader instance */
    private ClassLoader classLoader;

    /** the Class object */
    private Class<?> classObj;

    /**
     * 
     * Construct a new ResourceLoaderStrategy
     */
    public ResourceLoaderStrategy() {
        this.classLoader = Thread.currentThread().getContextClassLoader();

    }

    /**
     * 
     * Construct a new ResourceLoaderStrategy
     * 
     * @param classObj the class object
     */
    public ResourceLoaderStrategy(Class classObj) {
        this.classObj = classObj;

    }

    /**
     * 
     * Construct a new ResourceLoaderStrategy
     * 
     * @param classLoader the class loader
     */
    public ResourceLoaderStrategy(ClassLoader classLoader) {
        this.classLoader = classLoader;

    }

    /**
     * 
     * Construct a new ResourceLoaderStrategy
     * 
     * @param classLoader
     * @param classObj
     */
    public ResourceLoaderStrategy(ClassLoader classLoader, Class classObj) {
        this.classLoader = classLoader;
        this.classObj = classObj;

    }

    /**
     * Get the resource based on the location. The strategy works as follows: <li>The method first looks whether the
     * resource path has a <i>classpath:</i> prefix. If it finds one, it creates a {@link ClasspathResource} instance
     * based on the provided location.</li> If it doesnt find one, it then tries to create a {@link UrlResource}. If the
     * {@link UrlResource} cannot be created due to {@link MalformedURLException} then we return a
     * {@link FileSystemResource} instance and pray to God that it works :)
     * 
     * @param location the location of the resource
     * @return {@link Resource} instance.
     */
    public Resource getResource(String location) {
        Resource resource = null;
        
        if (location == null || location.length() <= 0) {
            Assert.fail("The location specified can not be Null or empty");
        }
        
        String locationWithoutClasspathPrefix = location.startsWith(CLASSPATH_PREFIX) ? location.substring(CLASSPATH_PREFIX.length()): location;
        
        resource = new ClasspathResource(locationWithoutClasspathPrefix, getClassLoader(), this.classObj) ;
        if (!resource.exists()) {
            LOG.debug(
                "Could not find the resource with path {} in the classpath. Trying to load the resource as a URL",
                location);
            try {
                // Try to parse the location as a URL...
                URL url = new URL(location);
                resource = new UrlResource(url);
                if(!resource.exists()){
                    LOG.debug(
                        "Could not load the resource with path {} as a URL. Trying to load the resource as a FileSystemResource",
                        location);
                    resource = new FileSystemResource(location);    
                }
            } catch (MalformedURLException ex) {
                resource = new FileSystemResource(location);
            }
        }
        if(resource == null || !resource.exists()){
            LOG.error("Could not load the resource with path {} as either a Classpath, FileSystem or a URL resource", location);
            Assert.fail("Could not load the resource with path " + location + " as either a Classpath, " +
                    "FileSystem or a URL resource. Please check the path and try again" );
        }
        return resource;

        
    }

    /**
     * Get the associated ClassLoader
     * 
     * @return the class loader
     */
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }

    /**
     * @return the classObj
     */
    public Class<?> getClassObj() {
        return classObj;
    }

}
