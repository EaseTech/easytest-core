package org.easetech.easytest.io;

public interface ResourceLoader {
    
    String CLASSPATH_PREFIX = "classpath:";
    
    String FILE_PREFIX = "file:";
    
    String URL_PREFIX = "url:";
    
    Resource getResource(String location);
    
    ClassLoader getClassLoader();

}
