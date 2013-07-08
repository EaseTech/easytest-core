package org.easetech.easytest.strategy;

import org.junit.runners.model.RunnerScheduler;

/**
 * A simple Scheduler that runs all the test methods in Serial Order
 *
 */
public class SerialScheduler implements RunnerScheduler {

    /**
     * Schedule the execution of a test method, which in this case is immediate
     * @param childStatement the statement to execute
     */
    public void schedule(Runnable childStatement) {
        childStatement.run();

    }

    /**
     * Method called after all the test methods have been executed.
     * In case of Serial execution, nothing extra is required. 
     */
    public void finished() {
        // TODO Auto-generated method stub

    }

}
