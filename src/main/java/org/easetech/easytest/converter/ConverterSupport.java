package org.easetech.easytest.converter;

import org.easetech.easytest.internal.DateTimeFormat;

/**
 * A convenient support class that gives users access to various defined 
 * parameters that may be useful during data conversion
 */
public class ConverterSupport {
    
    /** The name of the parameter to which this converter is serving */
    private String paramName;
    
    /**
     * Whether empty values should be converted to Null values or not
     */
    private Boolean convertEmptyToNull;
    
    /**
     * The user specified date time format to use
     */
    private DateTimeFormat dateTimeFormat;

    /**
     * The name of the parameter to which this converter is serving
     * @return the paramName
     */
    public String getParamName() {
        return paramName;
    }

    /**
     * The name of the parameter to which this converter is serving
     * @param paramName the paramName to set
     */
    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    /**
     * Whether empty values should be converted to Null values or not
     * @return the convertEmptyToNull
     */
    public Boolean getConvertEmptyToNull() {
        return convertEmptyToNull;
    }

    /**
     * Whether empty values should be converted to Null values or not
     * @param convertEmptyToNull the convertEmptyToNull to set
     */
    public void setConvertEmptyToNull(Boolean convertEmptyToNull) {
        this.convertEmptyToNull = convertEmptyToNull;
    }

    /**
     * The user specified date time format to use
     * @return the dateTimeFormat
     */
    public DateTimeFormat getDateTimeFormat() {
        return dateTimeFormat;
    }

    /**
     * The user specified date time format to use
     * @param dateTimeFormat the dateTimeFormat to set
     */
    public void setDateTimeFormat(DateTimeFormat dateTimeFormat) {
        this.dateTimeFormat = dateTimeFormat;
    }
    

}
