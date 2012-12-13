
package org.easetech.easytest.io;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of the {@link ResourceLoader} that defines a strategy to load 
 * different types of resources based on user provided values.
 * It loads three different types of resources :
 * <li> {@link ClasspathResource} - Resource representing the Classpath based resources(prefixed with classpath:)</li>
 * <li> {@link FileSystemResource} - Resource based on FileSystem (for eg. in your C: drive)</li>
 * <li> {@link UrlResource} - Resource based on URL (http for eg)
 * 
 * @author Anuj Kumar
 *
 */
public class ResourceLoaderStrategy implements ResourceLoader {
    

    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(ResourceLoaderStrategy.class);

    /**the class loader instance */
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
     * @param classObj the class object
     */
    public ResourceLoaderStrategy(Class classObj) {
        this.classObj = classObj;

    }

    /**
     * 
     * Construct a new ResourceLoaderStrategy
     * @param classLoader the class loader 
     */
    public ResourceLoaderStrategy(ClassLoader classLoader) {
        this.classLoader = classLoader;

    }
    
    /**
     * 
     * Construct a new ResourceLoaderStrategy
     * @param classLoader
     * @param classObj
     */
    public ResourceLoaderStrategy(ClassLoader classLoader, Class classObj) {
        this.classLoader = classLoader;
        this.classObj = classObj;

    }


    /**
     * Get the resource based on the location.
     * The strategy works as follows:
     * <li> The method first looks whether the resource path has a <i>classpath:</i> prefix.
     * If it finds one, it creates a {@link ClasspathResource} instance based on the provided location.</li>
     * If it doesnt find one, it then tries to create a {@link UrlResource}. If the {@link UrlResource} cannot be created due to {@link MalformedURLException}
     * then we return a {@link FileSystemResource} instance and pray to God that it works :)
     * @param location the location of the resource
     * @return {@link Resource} instance.
     */
    public Resource getResource(String location) {
        if(location == null || location.isEmpty()){
            Assert.fail("The location specified can not be Null or empty");
        }
        if(location.startsWith(CLASSPATH_PREFIX)){
            return new ClasspathResource(location.substring(CLASSPATH_PREFIX.length()), getClassLoader(), this.classObj);
        }else{
            try{
             // Try to parse the location as a URL...
                URL url = new URL(location);
                return new UrlResource(url);
            }catch(MalformedURLException ex){
                return new FileSystemResource(location);
            }
        }
    }

    /**
     * Get the associated ClassLoader
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
