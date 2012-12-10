
package org.easetech.easytest.converter;

import java.lang.reflect.Modifier;

import junit.framework.Assert;

import java.lang.reflect.ParameterizedType;

/**
 * 
 * An abstract class that can be used by the user to define their converters. It hides behind it the convertTo method
 * implementation to get the converted object's class
 * 
 * @param <Type> the type of object to convert to.
 */
public abstract class AbstractConverter<Type> implements Converter<Type> {

    /**
     * Get the Class variable representing the Type object
     * 
     * @return the Class variable representing the Type object
     */
    public Class<Type> convertTo() {
        @SuppressWarnings("unchecked")
        Class<Type> type = (Class<Type>) ((ParameterizedType) getClass().getGenericSuperclass())
            .getActualTypeArguments()[0];
        return type;
    }

    /**
     * Method responsible for returning an instance of the provided Generic Type argument.
     * The default implementation assumes that the generic type argument is a concrete type with a default 
     * no arg constructor. It calls the {@link Class#newInstance()} method to return the instance.
     * If the passed generic type argument is an interface or an abstract type, then the method calls the {@link Assert#fail()}
     * telling the user that the passed Class object was not a Concrete class.
     * 
     * <br>
     * A user can always extend this behavior to return specific instances in case the passed instance is an interface or an abstract class.
     * It is recommended to extend the Abstract Converter instead of implementing the Converter interface directly.
     * @return an instance of the generic type argument passed to the {@link AbstractConverter}
     */
    public Type instanceOfType() {
        Type type = null;
        Class<Type> classType = convertTo();
        try {
            type = classType.newInstance();
        } catch (InstantiationException e) {
            if (classType.isInterface()) {
                Assert
                    .fail(classType.getCanonicalName()
                        + " is not of Concrete type. EasyTest cannot instantiate an interface. Please provide a Concrete type as Generic Parameter Type while extending "
                        + AbstractConverter.class.getSimpleName());
            } else if (Modifier.isAbstract(convertTo().getModifiers())) {
                Assert
                    .fail(classType.getCanonicalName()
                        + " is not of Concrete type. EasyTest cannot instantiate an abstract class. Please provide a Concrete type as Generic Parameter Type while extending "
                        + AbstractConverter.class.getSimpleName());
            }
            else{
                Assert
                .fail("Error instantiating a class of type "+ classType.toString() + e.getMessage());
            }

        } catch (IllegalAccessException e) {
            Assert
            .fail("IllegalAccessException occured while instantiating a class of type "+ classType.toString() + e.getMessage());
        }
        return type;
    }

}