package org.easetech.easytest.annotation;

import javax.inject.Inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A field level annotation that can be used in the test classes 
 * to load the values from outside the test class, for example from 
 * a configuration file. The configuration file is provided to the test class using the 
 * {@link TestConfigProvider} annotation at the class level.
 * The Test Config Provider class should have methods annotated with {@link TestBean} annotations and 
 * should be public. It is a convenient annotation to support IoC. A user can also use {@link Inject} annotation 
 * to achieve the same behavior.
 * 
 * @author Anuj Kumar
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface Provided {
    
    /** The OPTIONAL name of the bean that should be loaded. If not 
     * provided then EasyTest framework tries to load the bean using
     * the field's type. If the bean with Type is also not found, 
     * then the framework searches for bean which has the same name as the name of the field
     */
    String value() default "";
    
    //Class<? extends FieldProvider> providerClass() default null;

}
