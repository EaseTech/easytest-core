package org.easetech.easytest.converter;

import java.util.Map;

/**
 * 
 * A generic interface responsible for converting a Map object into a user defined object.
 *
 * @param <Type> the type of object to convert to from a map.
 */
public interface Converter<Type> {
    
    /**
     * The class of the object to convert to
     * @return the class of the object to convert to
     */
    Class<Type> convertTo();
    
    /**
     * Convert the Map into a user defined object.
     * @param convertFrom the {@link Map} to convert from
     * @return the object to convert to identified by {@link #convertTo()} class.
     */
    Type convert(Map<String , Object> convertFrom);

}
