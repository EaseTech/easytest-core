package org.easetech.easytest.annotation;

import java.lang.annotation.Inherited;

import java.util.concurrent.Executors;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class level Annotation that is used
 * to run tests in Parallel.
 * A user can use the annotation as it is without specifying
 * any threads value in which case {@link Executors#newCachedThreadPool()} method is used
 * to create a Thread Pool.
 * In case the value of threads attribute is specified and it is greater than 0,
 * {@link Executors#newFixedThreadPool(int)} method is used to create a fixed thread pool
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface Parallel {
    
    /** Number of threads to start while executing the test methods in the given class 
     * If the value is ZERO or a NEGATIVE number, {@link Executors#newCachedThreadPool()} is used
     * to create thread pools else {@link Executors#newFixedThreadPool(int)} is used with the supplied thread count*/
    int threads() default 0;

}
