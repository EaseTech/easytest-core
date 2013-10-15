package org.easetech.easytest.converter;

import java.util.Map;

/**
 * 
 * A generic interface responsible for converting a Map object into a user defined object.
 * The Map object is of type Map&lt;String,List&lt;Map&lt;String,Object&gt;&gt;&gt;
 * It can be read as Map of MethodName as KEY and List of Map of MethodAttributes/MethodAttributeValues as Value.
 * Users should normally extend {@link AbstractConverter} instead of implementing this interface as the abstract converter
 * has implementation to methods {@link #convertTo()} and {@link #instanceOfType()}
 * <br>
 * For an example of writing a converter, look at : 
 * https://github.com/EaseTech/easytest-core/blob/master/src/test/java/org/easetech/easytest/example/ItemConverter.java
 *  
 * @param <TO> the type of object to convert to from a map.
 * 
 * @author Anuj Kumar
 */
public interface Converter<TO> extends BaseConverter<Map<String , Object>, TO>{
    
    /**
     * The class of the generic type argument to which the data should be converted to.
     * @return the class of the object to convert to
     */
    Class<TO> convertTo();
    
    /**
     * Convert the Map into a user defined object.
     * @param convertFrom the {@link Map} to convert from
     * @return the object to convert to identified by {@link #convertTo()} class.
     */
    TO convert(Map<String , Object> convertFrom);
    
    /**
     * Method responsible for returning an instance of the provided Generic Type argument.
     * Look at {@link AbstractConverter#instanceOfType()} methods Javadoc for details on the default implementation.  
     * @return an instance of the provided Generic Type argument. 
     */
    TO instanceOfType();

}
