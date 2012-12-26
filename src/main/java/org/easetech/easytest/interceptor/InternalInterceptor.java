
package org.easetech.easytest.interceptor;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class InternalInterceptor implements MethodInterceptor {
    
    /**
     * User provided implementation of {@link MethodIntercepter} interface.
     * if none is provided, then {@link DefaultMethodIntercepter} is used.
     */
    MethodIntercepter UserIntercepter;
    
    /**
     * The target class on which to call the intercepted method
     */
    Object targetInstance;
    
    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(InternalInterceptor.class);

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
        return UserIntercepter.intercept(method, targetInstance, args);
    }

    /**
     * @return the userIntercepter
     */
    public MethodIntercepter getUserIntercepter() {
        return UserIntercepter;
    }

    /**
     * @param userIntercepter the userIntercepter to set
     */
    public void setUserIntercepter(MethodIntercepter userIntercepter) {
        UserIntercepter = userIntercepter;
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
