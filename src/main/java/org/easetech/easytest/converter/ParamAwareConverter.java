package org.easetech.easytest.converter;

import java.util.Map;

/**
 * Decorator of {@link Converter} to provide the name of the parameter
 * to the implementing class. Users who are extending from {@link AbstractConverter}
 * will have no effect on their existing converters. On top of that they will now
 * have access to the parameter name using the {@link AbstractConverter#getParamName()} method.
 * Users who are directly implementing {@link Converter} interface will have to switch to either 
 * {@link ParamAwareConverter} or {@link AbstractConverter} to get the parameter name.
 *
 * @param <Type> the type of object to convert to from a map.
 * 
 * @author Anuj Kumar
 */
public interface ParamAwareConverter<Type> extends Converter<Type> {
    
    /**
     * Convert the Map into a user defined object and also expect the param name
     * @param convertFrom the {@link Map} to convert from
     * @param paramName the name of the parameter for which this converter is called
     * @return the object to convert to identified by {@link #convertTo()} class.
     */
    Type convert(Map<String , Object> convertFrom , String paramName);

}
