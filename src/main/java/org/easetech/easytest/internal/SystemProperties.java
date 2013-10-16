package org.easetech.easytest.internal;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * A convenient class to define all the system properties that EasyTest expects.
 * 
 * @author Anuj Kumar
 *
 */
@XmlRootElement
public enum SystemProperties {
    
    /**
     * System Property that a user can set at runtime using the -D option, to provide 
     * test data file paths at runtime.
     */
    TEST_DATA_FILES("test.dataFiles"),
    
    /**
     * System Property that a user can set at runtime using the -D option, 
     * to specify whether reports should be generated or not.
     */
    GENERATE_REPORT("reports.generate"),
    
    /**
     * System Property that a user can set at runtime using the -D option, 
     * to specify the report format.
     */
    REPORT_FORMAT("reports.format"),
    
    /**
     * System Property that a user can set at runtime using the -D option, 
     * to specify the report location.
     */
    REPORT_LOCATION("reports.location"),
    
    /**
     * System Property that a user can set at runtime using the -D option, 
     * to specify the report package.
     */
    REPORT_PACKAGES("reports.package"),
    
    /**
     * System Property to be used for specifying the number of times
     * a test should be repeated
     */
    REPEAT_COUNT("test.repeatCount"),
    
    /**
     * System property to specify the number of threads to run in parallel
     */
    PARALLEL_THREAD_COUNT("test.parallelThreads"),
    
    /**
     * System property to specify whethere the data should be written back to the file or not
     */
    WRITE_DATA("easytest.writeData");
    
    /**
     * the actual value of the system property
     */
    private String value;
    
    /**
     * 
     * Construct a new SystemProperties
     * @param value
     */
    private SystemProperties(String value) {
        this.value = value;
    }
    
    /**
     * Get the system property value
     * @return
     */
    public String getValue() {
        return value;
    }

}
