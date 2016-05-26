package org.easetech.easytest.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * A single instance of {@link EasyParamSignature} 
 * identifies a single test method parameter associated with the test method
 * 
 * @author Anuj Kumar
 *
 */
public class EasyParamSignature {
    
    public static ArrayList<EasyParamSignature> signatures(Method method) {

        return signatures(method.getGenericParameterTypes(), method
                .getParameterAnnotations());
    }

    public static List<EasyParamSignature> signatures(Constructor<?> constructor) {
        return signatures(constructor.getParameterTypes(), constructor
                .getParameterAnnotations());
    }

    private static ArrayList<EasyParamSignature> signatures(
            Type[] parameterTypes, Annotation[][] parameterAnnotations) {
        ArrayList<EasyParamSignature> sigs= new ArrayList<EasyParamSignature>();
        Class<?> parameterType;
        Class<?> genericParameterArgType = null;
        for (int i= 0; i < parameterTypes.length; i++) {
            Type genericParameterType = parameterTypes[i];
            if(ParameterizedType.class.isAssignableFrom(genericParameterType.getClass())){
                ParameterizedType aType = (ParameterizedType) genericParameterType;  
                parameterType = (Class)aType.getRawType();
                Type[] parameterArgTypes = aType.getActualTypeArguments();
                for(Type parameterArgType : parameterArgTypes){
                    genericParameterArgType = (Class) parameterArgType;
                }
            }else{
                //It is not a generic parameter but a simple parameter
                parameterType = (Class)genericParameterType;
            }
            
            sigs.add(new EasyParamSignature(parameterType, parameterAnnotations[i], genericParameterArgType));
            
        }
        return sigs;
    }

    /**
     * Identifies whether the Type of the parameter passed to the test method is of Generic Type or not.
     * For example if the method argument is of type List<String> then this boolean value will be True and the value of {@link #genericParameterArgType} will be String.
     * The value of {@link #parameterType} in this case will be {@link java.util.List}
     */
    private final Boolean isGenericParameter;
    
    private final Class<?> genericParameterArgType;
    
    private final Class<?> parameterType;

    private final Annotation[] parameterAnnotations;

    private EasyParamSignature(Class<?> parameterType, Annotation[] parameterAnnotations , Class<?> genericParameterArgType) {
        this.parameterType= parameterType;
        this.parameterAnnotations= parameterAnnotations;
        if(genericParameterArgType != null){
            this.isGenericParameter = true;
            this.genericParameterArgType = genericParameterArgType;
        }else{
            this.isGenericParameter = false;
            this.genericParameterArgType = null;
        }
    }

    public boolean canAcceptType(Class<?> candidate) {
        return parameterType.isAssignableFrom(candidate);
    }

    public Boolean getIsGenericParameter() {
        return isGenericParameter;
    }

    public Class<?> getGenericParameterArgType() {
        return genericParameterArgType;
    }

    public Class<?> getParameterType() {
        return parameterType;
    }

    public Annotation[] getParameterAnnotations() {
        return parameterAnnotations;
    }

    public boolean canAcceptArrayType(Class<?> type) {
        return type.isArray() && canAcceptType(type.getComponentType());
    }

    public boolean hasAnnotation(Class<? extends Annotation> type) {
        return getAnnotation(type) != null;
    }

    public <T extends Annotation> T findDeepAnnotation(Class<T> annotationType) {
        Annotation[] annotations2= parameterAnnotations;
        return findDeepAnnotation(annotations2, annotationType, 3);
    }

    private <T extends Annotation> T findDeepAnnotation(
            Annotation[] annotations, Class<T> annotationType, int depth) {
        if (depth == 0)
            return null;
        for (Annotation each : annotations) {
            if (annotationType.isInstance(each))
                return annotationType.cast(each);
            Annotation candidate= findDeepAnnotation(each.annotationType()
                    .getAnnotations(), annotationType, depth - 1);
            if (candidate != null)
                return annotationType.cast(candidate);
        }

        return null;
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        for (Annotation each : getParameterAnnotations())
            if (annotationType.isInstance(each))
                return annotationType.cast(each);
        return null;
    }

}
