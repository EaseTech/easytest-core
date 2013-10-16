package org.easetech.easytest.runner;

import org.easetech.easytest.annotation.Parallel;
import org.easetech.easytest.internal.SystemProperties;
import org.easetech.easytest.strategy.SchedulerStrategy;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

/**
 * Extended Suite class for EasyTest.
 * This Suite class gives the users ability to run the Suite classes in Parallel.
 * It also provides a convenient place to define parallel threads per test class
 *
 */
public class EasyTestSuite extends Suite {
    
    /**
     * 
     * Construct a new EasyTestSuite
     * @param klass
     * @throws InitializationError
     */
    public EasyTestSuite(Class<?> klass) throws InitializationError {
        super(klass , new AllDefaultPossibilitiesBuilder(true));
        setSchedulingStrategy();
        setScheduler(SchedulerStrategy.getSchedulerForSuite(klass));
    }

    /**
     * Set whether the tests should be run in parallel or serial.
     */
    protected void setSchedulingStrategy() {
        Class<?> testClass = getTestClass().getJavaClass();
        super.setScheduler(SchedulerStrategy.getScheduler(testClass , false));
        Parallel parallelAnnotation = testClass.getAnnotation(Parallel.class);
        if(parallelAnnotation != null) {
            int threads = parallelAnnotation.threads();
            System.setProperty(SystemProperties.PARALLEL_THREAD_COUNT.getValue(), String.valueOf(threads));
        }
        
        
    }


}
