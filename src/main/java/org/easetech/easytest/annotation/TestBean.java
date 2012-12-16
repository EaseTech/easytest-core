package org.easetech.easytest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A Method level annotation to identify a test bean.
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
