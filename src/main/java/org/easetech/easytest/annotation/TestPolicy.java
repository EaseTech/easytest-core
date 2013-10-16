package org.easetech.easytest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An extremely useful annotation to define a test classe's policy.
 * A policy of the test class is basically its behavior that drives the test.
 * Currently , in EasyTest World, the Test Policy consists of the following annotations :
 * <ul> <li>{@link Parallel} 
 * <li> {@link TestConfigProvider}
 * <li> {@link Display}
 * <li> {@link DataLoader}
 * <li> {@link Report}
 *
 * Thus a user can define all the above annotation is a separate class and annotate the test class with {@link TestPolicy}
 * annotation and define all the class level annotations in the class referenced by the {@link TestPolicy} annotation.
 * 
 * Note that a user can always override the policy specific annotation by defining the individual 
 * annotations at the test class level. If the annotation is present both in the class referenced in {@link TestPolicy} 
 * annotation as well as at the test class level, then the annotation at the test class level takes priority over policy level annotation.
 * 
 * @author Anuj Kumar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface TestPolicy {
    
    /** The class that identifies the Policy associated with the test class */
    Class<?> value();

}
