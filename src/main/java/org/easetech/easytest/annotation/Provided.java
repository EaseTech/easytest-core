package org.easetech.easytest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A field level annotation that can be used in the test classes 
 * to load the Filed values from out side the test class, for example from 
 * a configuration file. The configuration file is provided to the test class using the 
 * {@link TestConfigProvider} annotation at the class level.
 * The Test Config Provider class should have methods annotated with {@link TestBean} annotations and 
 * should be public
 * 
 * @author Anuj Kumar
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Provided {
    
    /** The OPTIONAL name of the bean that should be loaded. If not 
     * provided then EasyTest framework tries to load the bean using
     * the field's type. */
    String value() default "";

}
