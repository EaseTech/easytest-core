
package org.easetech.easytest.strategy;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.runners.model.RunnerScheduler;

/**
 * {@link RunnerScheduler} implementation to run tests in Parallel.
 * @author Anuj Kumar
 *
 */
public class ParallelScheduler implements RunnerScheduler {

    /**
     * An instance of {@link ExecutorService} to run tests in Parallel
     */
    private final ExecutorService executorService;
    
    /**
     * Construct a new ParallelScheduler.
     * This creates a Cached Thread Pool Executor Service
     */
    public ParallelScheduler() {
        this.executorService = Executors.newCachedThreadPool();
    }
    
    /**
     * 
     * Construct a new ParallelScheduler
     * This creates a fixed thread pool executor Service,
     * where the user passes the number of threads he wants to start 
     * @param threadsToStart the number of threads in the thread pool
     */
    public ParallelScheduler(int threadsToStart) {
        this.executorService = Executors.newFixedThreadPool(threadsToStart);
    }

    /**
     * Schedule a Runnable {@link org.junit.runners.model.Statement}
     * @param childStatement a runnable implementation
     */
    public void schedule(Runnable childStatement) {
        executorService.submit(childStatement);

    }

    /**
     * Method called when all the test methods have been executed
     * by the {@link ExecutorService} instance.
     * This method is used to shutdown and await for termination of all threads.
     */
    public void finished() {
        try {
            executorService.shutdown();
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace(System.err);
        }

    }

    /**
     * 
     * @return the executorService
     */
    public ExecutorService getExecutorService() {
        return executorService;
    }
    

}
