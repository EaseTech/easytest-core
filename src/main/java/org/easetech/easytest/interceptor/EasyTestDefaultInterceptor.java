
package org.easetech.easytest.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * A default interceptor that simply Logs the time taken by a method in nano seconds to the attached logger.
 *
 */
public class EasyTestDefaultInterceptor implements MethodInterceptor {
    
    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(EasyTestDefaultInterceptor.class);



    /**
     * Invoke the method with the advice
     * @param invocation
     * @return result of invoking the method
     * @throws Throwable
     */
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long startTime = System.nanoTime();
        Object result = invocation.getMethod().invoke(invocation.getThis(), invocation.getArguments());
        long duration = System.nanoTime() - startTime;
        LOG.debug("Method {} took {} nanoseconds", invocation.getMethod().getName(), duration);
        return result;

    }

    


}
