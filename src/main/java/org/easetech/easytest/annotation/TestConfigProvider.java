package org.easetech.easytest.annotation;

import java.lang.annotation.Inherited;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A Class level annotation used to identify the Test Configuration provider class.
 *
 *@author Anuj Kumar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface TestConfigProvider {
    
    /**
     * An array of Class objects identifying the test config provider class
     * @return An array of Class objects identifying the test config provider class 
     */
    Class<?>[] value();

}
