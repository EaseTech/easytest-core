package org.easetech.easytest.internal;

import org.easetech.easytest.annotation.Format;

/**
 * The date time formats to be used to convert raw data into date/time objects.
 * The class is instantiated with the date and time formats specified by the user using {@link Format}
 * annotation. If the user does not specify any format, then the default formats are used.
 * <br>The default formats for date type object are :
 * <ul>
 * <li>dd/MM/yy
 * <li>dd/MM/yyyy
 * <li>dd-MM-yy
 * <li>dd-MM-yyyy
 * </ul>
 *The default format for time type object is :
 *<ul>
 *<li>HH:MM:SS
 *</ul>
 *The default formats for date time type objetcs are :
 *<ul>
 * <li>dd/MM/yy HH:MM:SS
 * <li>dd/MM/yyyy HH:MM:SS
 * <li>dd-MM-yy HH:MM:SS
 * <li>dd-MM-yyyy HH:MM:SS
 * </ul>
 * 
 * @author Anuj Kumar
 */
public class DateTimeFormat {
    
    /**
     * The date formats to use as specified by the user or the default formats
     */
    private String[] dateFormat = {"dd/MM/yy" , "dd/MM/yyyy", "dd-MM-yy" , "dd-MM-yyyy"};
    
    /**
     * The time formats to use as specified by the user or the default formats
     */
    private String[] timeFormat = {"HH:MM:SS"};
    
    /**
     * The date/time formats to use as specified by the user or the default formats
     */
    private String[] dateTimeFormat = {"dd/MM/yy HH:MM:SS" ,"dd/MM/yyyy HH:MM:SS", "dd-MM-yy HH:MM:SS", "dd-MM-yyyy HH:MM:SS"};

    /**
     * Get the date formats to use as specified by the user or the default formats
     * @return the dateFormat
     */
    public String[] getDateFormat() {
        return dateFormat;
    }

    /**
     * Set the date formats to use as specified by the user or the default formats
     * @param dateFormat the dateFormat to set
     */
    public void setDateFormat(String[] dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * Get the time formats to use as specified by the user or the default formats
     * @return the timeFormat
     */
    public String[] getTimeFormat() {
        return timeFormat;
    }

    /**
     * Set the time formats to use as specified by the user or the default formats
     * @param timeFormat the timeFormat to set
     */
    public void setTimeFormat(String[] timeFormat) {
        this.timeFormat = timeFormat;
    }

    /**
     * Get the date/time formats to use as specified by the user or the default formats
     * @return the dateTimeFormat
     */
    public String[] getDateTimeFormat() {
        return dateTimeFormat;
    }

    /**
     * Set the date/time formats to use as specified by the user or the default formats
     * @param dateTimeFormat the dateTimeFormat to set
     */
    public void setDateTimeFormat(String[] dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    /**
     * Override toString
     * @return toString representation
     */
    @Override
    public String toString() {
        return "DateTimeFormat [dateFormat=" + dateFormat + ", timeFormat=" + timeFormat + ", dateTimeFormat="
            + dateTimeFormat + "]";
    }
    
    

}
