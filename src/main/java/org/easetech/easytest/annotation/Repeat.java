package org.easetech.easytest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import java.lang.annotation.Retention;

/**
 * Annotation to repeat the test method 'n' times where 'n' is identified by the attribute {@link #times()}
 * This annotation can be applied at the test method level and will 
 * repeat a given test the number of times as specified by the times attribute. 
 * The test will be executed with exactly the same set of parameters as specified for a single run of the test.
 * Note that in case you have specified 3 different input sets for the same test method and also have a Repeat annotation
 * on that test method with the {@link #times()} attribute set to 5, then the test will be executed for a total of 15(3 * 5) times.
 * 
 * Currently, there is no way to specify the repeat for a given set of test input. 
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Repeat {
    
    /** The number of times a given test method must be repeated*/
    int times();

}
