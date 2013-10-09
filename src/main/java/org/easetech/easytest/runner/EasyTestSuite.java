package org.easetech.easytest.runner;

import org.easetech.easytest.annotation.Parallel;
import org.easetech.easytest.internal.SystemProperties;
import org.easetech.easytest.strategy.SchedulerStrategy;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;

public class EasyTestSuite extends Suite {
    
    public EasyTestSuite(Class<?> klass) throws InitializationError {
        super(klass , new AllDefaultPossibilitiesBuilder(true));
        setSchedulingStrategy();
    }

    /**
     * Set whether the tests should be run in parallel or serial.
     */
    protected void setSchedulingStrategy() {
        Class<?> testClass = getTestClass().getJavaClass();
        super.setScheduler(SchedulerStrategy.getScheduler(testClass));
        Parallel parallelAnnotation = testClass.getAnnotation(Parallel.class);
        if(parallelAnnotation != null) {
            int threads = parallelAnnotation.threads();
            System.setProperty(SystemProperties.PARALLEL_THREAD_COUNT.getValue(), String.valueOf(threads));
        }
        
        
    }


}
