package org.easetech.easytest.interceptor;

import java.lang.reflect.Method;
import java.util.Observable;
import junit.framework.Assert;
import org.easetech.easytest.annotation.Duration;
import org.easetech.easytest.reports.data.MethodUnderTestDuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A common class for both {@link InternalInterceptor} as well as {@link InternalInvocationhandler}
 * 
 * @author Anuj Kumar
 *
 */
public class CommonProxyInterceptor  extends Observable {
    
    /** Logger implementation*/
    protected static final Logger LOG = LoggerFactory.getLogger(CommonProxyInterceptor.class);
    
    /**
     * User provided {@link MethodIntercepter}
     */
    private MethodIntercepter userIntercepter;
    
    /**
     * The instance on which to call the Method
     */
    private Object targetInstance;
    
    /**
     * The expected Run time of the method as specified in the {@link Duration} annotation
     */
    private Long expectedRunTime;
    
    /**
     * Get the User supplied {@link MethodIntercepter} implementation.
     * Defaults to {@link DefaultMethodIntercepter}
     * @return the userIntercepter user supplied method interceptor
     */
    public MethodIntercepter getUserIntercepter() {
        return userIntercepter;
    }

    /**
     * the User supplied {@link MethodIntercepter} implementation.
     * Defaults to {@link DefaultMethodIntercepter} 
     * @param userIntercepter the userIntercepter to set
     */
    public void setUserIntercepter(MethodIntercepter userIntercepter) {
        this.userIntercepter = userIntercepter;
    }

    /**
     * The actual instance of the class that is being proxied
     * @return the targetInstance instance of the class that is being proxied
     */
    public Object getTargetInstance() {
        return targetInstance;
    }

    /**
     * Set instance of the class that is being proxied
     * @param targetInstance the targetInstance to set
     */
    public void setTargetInstance(Object targetInstance) {
        this.targetInstance = targetInstance;
    }

    /**
     * Get the maximum time in milliseconds that a method under test should take to run.
     * 
     * @return the expectedRunTime
     */
    public Long getExpectedRunTime() {
        return expectedRunTime;
    }

    /**
     * Set the maximum time in milliseconds that a method under test should take to run.
     * @param expectedRunTime the expectedRunTime to set
     */
    public void setExpectedRunTime(Long expectedRunTime) {
        this.expectedRunTime = expectedRunTime;
    }
    
    /**
     * Get the expected time in Nano seconds
     * @param timeInMillis the given time in millis
     * @return the time in Nanos
     */
    public Long getExpectedTimeInNano(Long timeInMillis) {
        Long result = null;
        Long nanoBase = (long)(1000 * 1000) ;
        if(timeInMillis != null && timeInMillis != 0 && timeInMillis != Long.MAX_VALUE) {
            if (nanoBase > Long.MAX_VALUE / timeInMillis) {
                Assert.fail("EasyTest tries to convert the time (specified in Millisecond using Duration annotation) into nano seconds for precise comparisons." +
                		"But in this particular case, you specified a value that would ultimately overflow and will not fit in the long datatype. " +
                		"The value is : " + timeInMillis + "(ms). Please specify a smaller time unit.");
            } else {
               result = timeInMillis * nanoBase; 
            }
        }
        return result;
        
    }
    
    /**
     * Compare the time taken by the method to the expected runtime.
     * Fail if the time taken is more, else log the time taken.
     * @param timeTakenInNanos time taken by the method under test
     * @param method Name of the method
     * @param durationBean the instance of {@link MethodUnderTestDuration}
     */
    public void compareTime(Long timeTakenInNanos , Method method , MethodUnderTestDuration durationBean) {
        Long expectedTimeInNano = getExpectedTimeInNano(getExpectedRunTime());
        Long expectedTimeInMillis = Long.valueOf(0), timeTakenInMillis, expectedTimeinMicros, timeTakenInMicros;
        timeTakenInMicros = timeTakenInNanos / 1000 ;
        timeTakenInMillis = (timeTakenInNanos / 1000)/1000;
        if(expectedTimeInNano!= null && timeTakenInNanos > expectedTimeInNano) {
            
            expectedTimeInMillis = (expectedTimeInNano / 1000)/1000;
            expectedTimeinMicros = expectedTimeInNano / 1000 ;
            
            Assert.fail("Total time taken by method " + method.getName() +" ("+ timeTakenInNanos + " nanosec/"+ timeTakenInMicros +" microsec/"+ timeTakenInMillis+" millisec) is greater than the " +
            		"expected time("+expectedTimeInNano+" nenosec/" +expectedTimeinMicros+" microsec/"+expectedTimeInMillis+" millisec)");
        } else {
            System.out.println("Method " + method.getName() + " on " + getTargetInstance().getClass()+ " took " + timeTakenInNanos + " nanosec/"+ timeTakenInMicros +" microsec/"+ timeTakenInMillis+" millisec" );
            LOG.debug("Method " + method.getName() + " on " + getTargetInstance().getClass()+ " took " + timeTakenInNanos + " nanosec/"+ timeTakenInMicros +" microsec/"+ timeTakenInMillis+" millisec" );
        }
        durationBean.setActualDurationinMillis(timeTakenInMillis);
        durationBean.setExpectedDurationinMillis(expectedTimeInMillis);
        notifyObservers(durationBean);
    }
    
    /**
     * A common Interceptor method for both {@link InternalInvocationhandler} and
     * {@link InternalInterceptor} that handles delegation to a user defined interceptor
     * and also compares the time taken by the method to the time a user expects a method 
     * to be completed
     * @param method the method to invoke
     * @param args the arguments to the method
     * @return result of method execution
     * @throws Throwable if any exception occurs
     */
    public Object intercept(Method method , Object[] args) throws Throwable {
        Long startTime = System.nanoTime();
        Object result = getUserIntercepter().intercept(method, getTargetInstance(), args);
        Long timeTaken = System.nanoTime() - startTime;
        MethodUnderTestDuration durationBean = getMethodDurationBean(method, args, result);
        compareTime(timeTaken , method , durationBean);
        return result;
    }
    
    /**
     * Method to get hte {@link MethodUnderTestDuration} instance filled with requisite information
     * @param method the method under test
     * @param args the arguments to the method under test
     * @param result the result returned by the method under test
     * @return an instance of {@link MethodUnderTestDuration}
     */
    private MethodUnderTestDuration getMethodDurationBean(Method method, Object[] args , Object result) {
        MethodUnderTestDuration durationBean = new MethodUnderTestDuration();
        durationBean.setClassUnderTest(getTargetInstance().getClass());
        durationBean.setMethodUnderTest(method);
        durationBean.setMethodArguments(args);
        durationBean.setMethodResult(result);
        return durationBean;
        
    }

}
