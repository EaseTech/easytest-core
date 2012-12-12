
package org.easetech.easytest.io;

import java.net.MalformedURLException;
import java.net.URL;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceLoaderStrategy implements ResourceLoader {
    

    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(ResourceLoaderStrategy.class);

    private ClassLoader classLoader;
    
    private Class<?> classObj;

    public ResourceLoaderStrategy() {
        this.classLoader = Thread.currentThread().getContextClassLoader();

    }

    public ResourceLoaderStrategy(Class classObj) {
        this.classObj = classObj;

    }

    public ResourceLoaderStrategy(ClassLoader classLoader) {
        this.classLoader = classLoader;

    }
    
    public ResourceLoaderStrategy(ClassLoader classLoader, Class classObj) {
        this.classLoader = classLoader;
        this.classObj = classObj;

    }


    @Override
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

    @Override
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
