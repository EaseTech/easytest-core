package org.easetech.easytest.io;

/**
 * 
 * ResourceLoader interface consisting of methods to get hold of a {@link Resource} object.
 * @see ResourceLoaderStrategy
 * 
 * @author Anuj Kumar
 * 
 */
public interface ResourceLoader {
    
    /**
     * The classpath prefix
     */
    String CLASSPATH_PREFIX = "classpath:";
    
    /**
     * The file prefix
     */
    String FILE_PREFIX = "file:";
    
    /**
     * The URL prefix
     */
    String URL_PREFIX = "url:";
    
    /**
     * Get the {@link Resource} instance based on the location
     * @param location the location of the resource
     * @return {@link Resource} instance
     */
    Resource getResource(String location);
    
    /**
     * Get the class loader associated with the Resource Loader
     * @return the class loader associated with the Resource Loader
     */
    ClassLoader getClassLoader();

}
