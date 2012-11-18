package org.easetech.easytest.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A test class level annotation providing reporting parameters.
 * 
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Report {
	
	public enum EXPORT_FORMAT {
		PDF, HTML, XLS
	}
	
	/**
	 * The output path where the reports should be written to. prefix with file:
	 * will output to filesystem's directory prefix with classpath: will output
	 * to target classpath
	 */
	String outputLocation() default "";

	/** The output format. Defaults to PDF. */
	EXPORT_FORMAT[] outputFormats() default { EXPORT_FORMAT.PDF };

}
