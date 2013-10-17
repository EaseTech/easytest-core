package org.easetech.easytest.internal;

public class DateTimeFormat {
    
    private String[] dateFormat = {"dd/MM/yyyy", "dd-MM-yyyy"};
    
    private String[] timeFormat = {"HH:MM:SS"};
    
    private String[] dateTimeFormat = {"dd/MM/yyyy HH:MM:SS" , "dd-MM-yyyy HH:MM:SS"};

    /**
     * @return the dateFormat
     */
    public String[] getDateFormat() {
        return dateFormat;
    }

    /**
     * @param dateFormat the dateFormat to set
     */
    public void setDateFormat(String[] dateFormat) {
        this.dateFormat = dateFormat;
    }

    /**
     * @return the timeFormat
     */
    public String[] getTimeFormat() {
        return timeFormat;
    }

    /**
     * @param timeFormat the timeFormat to set
     */
    public void setTimeFormat(String[] timeFormat) {
        this.timeFormat = timeFormat;
    }

    /**
     * @return the dateTimeFormat
     */
    public String[] getDateTimeFormat() {
        return dateTimeFormat;
    }

    /**
     * @param dateTimeFormat the dateTimeFormat to set
     */
    public void setDateTimeFormat(String[] dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }

    /**
     * @return
     */
    @Override
    public String toString() {
        return "DateTimeFormat [dateFormat=" + dateFormat + ", timeFormat=" + timeFormat + ", dateTimeFormat="
            + dateTimeFormat + "]";
    }
    
    

}
