package org.easetech.easytest.converter;

import java.lang.reflect.ParameterizedType;

/**
 * 
 * An abstract class that can be used by the user to define their converters. 
 * It hides behind it the convertTo method implementation to get the converted object's class
 *
 * @param <Type> the type of object to convert to.
 */
public abstract class AbstractConverter<Type> implements Converter<Type> {

    /**
     * Get the Class variable representing the Type object
     * 
     * @return the Class variable representing the Type object
     */
    @Override
    public Class<Type> convertTo() {
        @SuppressWarnings("unchecked")
        Class<Type> type = (Class<Type>) ((ParameterizedType) getClass().getGenericSuperclass())
            .getActualTypeArguments()[0];
        return type;
    }

}