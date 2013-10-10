
package org.easetech.easytest.interceptor;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

/**
 * 
 * An internal intercepter implementation of the CGLIB provided {@link MethodInterceptor} interface, that 
 * actually calls the {@link MethodIntercepter#intercept(Method, Object, Object[])} method internally.
 * This is done so as to hide the implementation details of intercepting method calls from the user.
 * A user can extend this class to modify the behavior of the default 
 * {@link MethodInterceptor#intercept(Object, Method, Object[], MethodProxy)} method implementation
 * 
 * @author Anuj Kumar
 *
 */
public class InternalInterceptor extends CommonProxyInterceptor implements MethodInterceptor {

    /**
     * Intercept the method with the advice
     * @param object the object on which to invoke the method
     * @param method the method to invoke
     * @param args arguments to the method
     * @param methodProxy the method proxy
     * @return returned value
     * @throws Throwable
     */
    public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        return intercept(method, args);
    }


}
