package org.easetech.easytest.interceptor;

import org.easetech.easytest.annotation.Provided;

import java.lang.reflect.Method;

import java.lang.reflect.InvocationHandler;

/**
 * implementation of the {@link InvocationHandler} interface for internal usage.
 * This is used when the user has {@link Provided} annotation on a field whose type is an Interface.
 * EasyTest uses JDK dynamic proxies to intercept calls to the method on Interface type fields.
 * For Concrete classes, EasyTest uses CGLIB.
 * 
 * @author Anuj Kumar
 *
 */
public class InternalInvocationhandler implements InvocationHandler {
    
    /**
     * User provided {@link MethodIntercepter}
     */
    MethodIntercepter userIntercepter;
    
    /**
     * The instance on which to call teh Method
     */
    Object targetInstance;

    /**
     * Invoke the method on the provided {@link #targetInstance}.
     * {@link #userIntercepter}'s intercept method is invoked with the right arguments.
     * Note that this implementation does not use the proxy instance passed to this method.
     * @param proxy NOT USED
     * @param method the method to invoke
     * @param args the arguments to the method
     * @return object
     * @throws Throwable if any exception occurs
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return userIntercepter.intercept(method, targetInstance, args);
    }

    /**
     * @return the userIntercepter
     */
    public MethodIntercepter getUserIntercepter() {
        return userIntercepter;
    }

    /**
     * @param userIntercepter the userIntercepter to set
     */
    public void setUserIntercepter(MethodIntercepter userIntercepter) {
        this.userIntercepter = userIntercepter;
    }

    /**
     * @return the targetInstance
     */
    public Object getTargetInstance() {
        return targetInstance;
    }

    /**
     * @param targetInstance the targetInstance to set
     */
    public void setTargetInstance(Object targetInstance) {
        this.targetInstance = targetInstance;
    }
    
    
    
    

}
