package org.easetech.easytest.annotation;

import java.util.Properties;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A Class level annotation that is applied of Config classes that defines the {@link TestBean}s.
 * This annotation is used to load the configuration properties, if any, for the test configuration.
 * This annotation can be used both on the Config classes as well as on the actual Test classes.
 * When you use this annotation, be sure to provide a field of type {@link Properties} 
 * in the class on which the annotation is used. This property will be provided the value of the loaded Properties
 * at runtime by the framework.
 *
 *@author Anuj Kumar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface TestProperties {
    
    /**
     * The array of file paths from where to load the resource.
     * 
     */
    String[] value();
}
