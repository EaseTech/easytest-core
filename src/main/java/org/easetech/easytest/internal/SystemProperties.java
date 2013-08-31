package org.easetech.easytest.internal;

/**
 * 
 * A convenient class to define all the system properties that EasyTest expects.
 * 
 * @author Anuj Kumar
 *
 */
public enum SystemProperties {
    
    /**
     * System Property that a user can set at runtime using the -D option, to provide test data at runtime.
     */
    TEST_DATA_FILES("testDataFiles"),
    
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
    REPORT_PACKAGES("reports.package");
    
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
