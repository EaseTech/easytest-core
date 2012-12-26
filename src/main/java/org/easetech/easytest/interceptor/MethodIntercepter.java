package org.easetech.easytest.interceptor;

import org.easetech.easytest.runner.DataDrivenTestRunner;

import org.easetech.easytest.annotation.Intercept;

import java.lang.reflect.Method;

/**
 * Interface that provides the users with the ability to intercept 
 * methods on class instances that are marked with {@link Intercept} annotations.
 * The user can provide the implementation of this interface as attribute to the {@link Intercept} annotation.
 * Note: If you are using {@link DataDrivenTestRunner}, then CGLib is being used to intercept method invocation behind the scene. 
 * Look at {@link InternalInterceptor} class for more detail.
 * If you are using SpringTestRunner from the easytest-spring module, 
 * then Spring AOP is being used to intercept method invocation behind the scene.You can look at SpringInternalIntercepter class 
 * in the easytest-spring module for more detail.
 * 
 * @author Anuj Kumar
 *
 */
public interface MethodIntercepter {
    
    /**
     * Intercept the method invocation on the target class. 
     * To be used for providing before and after advice when a given method is executed.
     * @param methodToIntercept the method to intercept
     * @param targetInstance the target instance on which to call the method
     * @param methodArgs the arguments to the method
     * @return object 
     * @throws Throwable if any exception occurs
     */
    public Object intercept(Method methodToIntercept , Object targetInstance , Object[] methodArgs ) throws Throwable;

}
