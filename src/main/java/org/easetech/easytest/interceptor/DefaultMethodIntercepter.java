package org.easetech.easytest.interceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * A default implementation of {@link MethodIntercepter} to log the time taken by a method
 * 
 * @author Anuj Kumar
 *
 */
public class DefaultMethodIntercepter implements MethodIntercepter {
    
    /** Logger implementation*/
    protected static final Logger LOG = LoggerFactory.getLogger(DefaultMethodIntercepter.class);

    /**
     * Intercept the method invocation for logging the time taken by the method.
     * @param methodToIntercept the method to intercept the call for
     * @param targetInstance the target class on which the method will be called
     * @param methodArgs the arguments to the method
     * @return the object that is returned after method invocation
     * @throws Throwable if any exception occurs
     */
    public Object intercept(Method methodToIntercept, Object targetInstance, Object[] methodArgs) throws Throwable{
        long startTime = System.nanoTime();
        Object result = methodToIntercept.invoke(targetInstance, methodArgs);
        long duration = System.nanoTime() - startTime;
        LOG.debug("Method {} took {} nanoseconds", methodToIntercept.getName(), duration);
        return result;
    }

}
