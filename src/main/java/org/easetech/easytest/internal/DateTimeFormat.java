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
    private String[] dateFormats = {"dd/MM/yy" , "dd/MM/yyyy", "dd-MM-yy" , "dd-MM-yyyy"};
    
    /**
     * The time formats to use as specified by the user or the default formats
     */
    private String[] timeFormats = {"HH:MM:SS"};
    
    /**
     * The date/time formats to use as specified by the user or the default formats
     */
    private String[] dateTimeFormats = {"dd/MM/yy HH:MM:SS" ,"dd/MM/yyyy HH:MM:SS", "dd-MM-yy HH:MM:SS", "dd-MM-yyyy HH:MM:SS"};

    /**
     * Get the date formats to use as specified by the user or the default formats
     * @return the dateFormat
     */
    public String[] getDateFormats() {
        return dateFormats;
    }

    /**
     * Set the date formats to use as specified by the user or the default formats
     * @param dateFormats the dateFormat to set
     */
    public void setDateFormats(String[] dateFormats) {
        this.dateFormats = dateFormats;
    }

    /**
     * Get the time formats to use as specified by the user or the default formats
     * @return the timeFormat
     */
    public String[] getTimeFormats() {
        return timeFormats;
    }

    /**
     * Set the time formats to use as specified by the user or the default formats
     * @param timeFormats the timeFormat to set
     */
    public void setTimeFormats(String[] timeFormats) {
        this.timeFormats = timeFormats;
    }

    /**
     * Get the date/time formats to use as specified by the user or the default formats
     * @return the dateTimeFormat
     */
    public String[] getDateTimeFormats() {
        return dateTimeFormats;
    }

    /**
     * Set the date/time formats to use as specified by the user or the default formats
     * @param dateTimeFormats the dateTimeFormat to set
     */
    public void setDateTimeFormats(String[] dateTimeFormats) {
        this.dateTimeFormats = dateTimeFormats;
    }

    /**
     * Override toString
     * @return toString representation
     */
    @Override
    public String toString() {
        return "DateTimeFormat [dateFormats=" + dateFormats + ", timeFormats=" + timeFormats + ", dateTimeFormats="
            + dateTimeFormats + "]";
    }
    
    

}
