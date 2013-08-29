package org.easetech.easytest.interceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import org.easetech.easytest.annotation.Provided;

/**
 * implementation of the {@link InvocationHandler} interface for internal usage.
 * This is used when the user has {@link Provided} annotation on a field whose type is an Interface.
 * EasyTest uses JDK dynamic proxies to intercept calls to the method on Interface type fields.
 * For Concrete classes, EasyTest uses CGLIB.
 * 
 * @author Anuj Kumar
 *
 */
public class InternalInvocationhandler extends CommonProxyInterceptor implements InvocationHandler {
    
    

    /**
     * Invoke the method on the provided {@link #getTargetInstance}.
     * {@link #getUserIntercepter}'s intercept method is invoked with the right arguments.
     * Note that this implementation does not use the proxy instance passed to this method.
     * @param proxy NOT USED
     * @param method the method to invoke
     * @param args the arguments to the method
     * @return object
     * @throws Throwable if any exception occurs
     */
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        return intercept(method, args);
    }

    
    
    
    
    

}
