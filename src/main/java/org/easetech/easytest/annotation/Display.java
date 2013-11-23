package org.easetech.easytest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that defines what all fields should be used in the display name of the test method.
 * EasyTest shows the input test data as part of the test method name in the IDE.
 * But sometimes the input test data is too much to be displayed and a mechanism was required 
 * to restrict the input fields appearing in the test method name. Display annotation is that mechanism.
 * A user can say in the {@link #fields()} attribute what all fields should be part of the test method name.
 * Note that if the input test data does not contain any field specified by the {@link #fields()}, EasyTest will 
 * default back to its original behaviour, which is showing all the input fields as part of test method name. 
 *
 *@author Anuj Kumar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD , ElementType.TYPE})
public @interface Display {

    /**
     * The input fields that should be part of the test method display name
     */
    String[] fields();
}
