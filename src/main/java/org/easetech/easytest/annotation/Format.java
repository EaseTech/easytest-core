package org.easetech.easytest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that can be used by the user to specify various formating options to be used by EasyTest while 
 * converting the raw data to a specific object. Currently user has the option to specify the format of date, time and datetime type fields.
 *  
 * @author Anuj Kumar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD , ElementType.TYPE})
public @interface Format {
    
    /**
     * The date formats to be used
     */
    String[] date() default "dd/MM/yyyy";
    
    /** The date time formats to be used*/
    String[] dateTime() default "dd/MM/yyyy HH:MM:SS";
    
    /** The time formats to be used*/
    String[] time() default "HH:MM:SS";

}
