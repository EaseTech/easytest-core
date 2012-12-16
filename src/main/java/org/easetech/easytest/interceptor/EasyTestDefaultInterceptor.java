
package org.easetech.easytest.interceptor;

import java.lang.reflect.Method;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * A default interceptor that simply Logs the time taken by a method in nano seconds to the attached logger.
 * 
 * @author Anuj Kumar
 *
 */
public class EasyTestDefaultInterceptor implements MethodInterceptor {
    
    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(EasyTestDefaultInterceptor.class);

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
        long startTime = System.nanoTime();
        Object result =  methodProxy.invokeSuper(object, args);
        long duration = System.nanoTime() - startTime;
        LOG.debug("Method {} took {} nanoseconds", method.getName(), duration);
        return result;
    }



    


}
