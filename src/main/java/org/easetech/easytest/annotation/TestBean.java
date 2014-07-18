package org.easetech.easytest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A Method level annotation to identify a test bean.
 * Since EasyTest supports Inversion of Control, A user can specify 
 * his test beans in a config class and use the Injection mechanism of easytest to inject these test beans in the test classes.
 * Look at {@link Provided} annotation for details. These beans should be defined in the class that is identified by the {@link TestConfigProvider}
 * 
 * @author Anuj Kumar
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface TestBean {
    /**
     * Optional name of the bean. If not provided, then
     * the return type of the method is used to identify the bean.
     * @return the name of the bean
     */
    String value() default "";

}
