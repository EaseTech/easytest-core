
package org.easetech.easytest.converter;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * A class that manages the registered Converters and makes them available to the framework. This class can be used to
 * locate a {@link Converter} for any given type name.
 * 
 */
@SuppressWarnings("rawtypes")
public class ConverterManager {

    /**
     * A thread local variable that will hold the set of {@link Converter} for easy consumption by the test cases.
     */
    public static final ThreadLocal<Set<Converter>> converters = new ThreadLocal<Set<Converter>>();

    /**
     * Find the registered Converter for the given class type
     * @param targetType the class type to find teh converter for.
     * @return an instance of registered converter or Null if not found.
     */
    public static Converter<?> findConverter(Class<?> targetType) {

		Set<Converter> cnvrtrs = converters.get();
		Converter result = null;
		if(cnvrtrs!=null){
			Iterator<Converter> itr = cnvrtrs.iterator();
	        
	        while (itr.hasNext()) {
	            Converter converter = itr.next();
	            if (converter.convertTo().equals(targetType)) {
	                result = converter;
	                break;
	            }
	        }
		}
        
        return result;
    }

    /**
     * Register the converter with the ConverterManager
     * @param converterClass the class object identifying the concrete {@link Converter} class.
     */
    public static void registerConverter(Class converterClass) {
        Set<Converter> cnvrtrs = converters.get();
        if (cnvrtrs == null) {
            cnvrtrs = new HashSet<Converter>();
        }
        if (converterClass != null && Converter.class.isAssignableFrom(converterClass)) {
            Converter converter = null;
            try {
                converter = (Converter) converterClass.newInstance();
                cnvrtrs.add(converter);
                converters.set(cnvrtrs);
            } catch (InstantiationException e) {
                throw new RuntimeException(
                    "InstantiationException occured while trying to register a converter with class : "
                        + converterClass, e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(
                    "IllegalAccessException occured while trying to register a converter with class : "
                        + converterClass, e);
            }

        } else {
            System.out.println("Converter with class :" + converterClass + " not registered");
        }

    }

    /**
     * Clean the thread local variable
     */
    public static void cleanConverters() {
        converters.remove();
    }

}
