
package org.easetech.easytest.strategy;

import org.easetech.easytest.annotation.ParallelSuite;

import org.easetech.easytest.internal.SystemProperties;

import org.easetech.easytest.annotation.Parallel;
import org.junit.runners.model.RunnerScheduler;

/**
 * A simple Strategy class that decides the Scheduler to use to run the tests. Scheduler at the moment can be serial or
 * parallel.<br>
 * A user can specify {@link Parallel} annotation at the class level to run tests in that class in parallel. If the
 * Parallel annotation is not specified then the scheduler is not overridden and JUnits strategy is used to run the
 * tests.
 * 
 * @author Anuj Kumar
 * 
 */
public class SchedulerStrategy {

    /**
     * Get the correct Scheduler requested by the user based on the {@link Parallel} annotation.
     * 
     * @param testClass the Class under test
     * @return an implementation of {@link RunnerScheduler}
     */
    public static RunnerScheduler getScheduler(Class<?> testClass) {
        Parallel parallelAnnotation = testClass.getAnnotation(Parallel.class);
        String parallelThreadsStr = System.getProperty(SystemProperties.PARALLEL_THREAD_COUNT.getValue());
        Integer parallelThreads = null;
        if (parallelThreadsStr != null) {
            parallelThreads = Integer.valueOf(parallelThreadsStr);
        }

        if (parallelThreads != null) {
            if (parallelThreads <= 0) {
                return new ParallelScheduler();
            } else {
                return new ParallelScheduler(parallelAnnotation.threads());
            }
        } else {
            if (parallelAnnotation != null) {
                if (parallelAnnotation.threads() <= 0) {
                    return new ParallelScheduler();
                } else {
                    return new ParallelScheduler(parallelAnnotation.threads());
                }

            }
        }
        return new SerialScheduler();

    }
    
    /**
     * Utility method to get the Scheduler for Suite class
     * @param suiteClass the suiteClass
     * @return an instance of {@link RunnerScheduler}
     */
    public static RunnerScheduler getSchedulerForSuite(Class<?> suiteClass) {
        ParallelSuite parallelSuite = suiteClass.getAnnotation(ParallelSuite.class);
        if(parallelSuite != null) {
            if(parallelSuite.threads() <=0) {
                return new ParallelScheduler();
            } else {
                return new ParallelScheduler(parallelSuite.threads());
            }
        }
        return new SerialScheduler();
    }

}
