package org.easetech.easytest.converter;


/**
 * 
 * A base Converter for all the converters in EasyTest.
 *
 * @param <FROM> the data to convert from
 * @param <TO> the data to convert to
 * 
 * @author Anuj Kumar
 */
public interface BaseConverter<FROM, TO> {
    
    /**
     * Constant for Empty String
     */
    public static final String EMPTY_STRING = "";
    

    
    public static final String COLON = ":";
    
    /**
     * Convert the passed object into a user defined data.
     * @param convertFrom the object to convert from
     * @return the object to convert to 
     */
    TO convert(FROM convertFrom);

}
