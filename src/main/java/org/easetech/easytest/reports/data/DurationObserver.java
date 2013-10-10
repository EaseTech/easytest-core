package org.easetech.easytest.reports.data;



import java.util.ArrayList;

import java.util.Collections;

import java.util.List;
import java.util.Observable;
import java.util.Observer;

/**
 * The {@link Observer} implementation to observe the method under test and the time it took to run as well as the expected time, if any
 * This class is instantiated during the initialization phase of the EasyTest's runner.
 * It has a synchronized list in which data is added for each run of the test method. 
 * Thus in case of parallel execution of tests multiple threads can try to add the data at the same time, thus the synchronized list.
 * A user of this list should take care of synchronizing the iteration process on this list if multiple threads are involved.
 * 
 * @author Anuj Kumar
 *
 */
public class DurationObserver implements Observer {

    /**
     * A synchronized list in which data is added for each run of the test method.
     */
    private final List<MethodUnderTestDuration> durationList = Collections.synchronizedList(new ArrayList<MethodUnderTestDuration>());
    
    /**
     * Method responsible for updating the duration list and adding an instance of {@link MethodUnderTestDuration}
     * for each execution of the test. 
     * @param o the observable instance
     * @param obj an instance of {@link MethodUnderTestDuration}
     */
    public void update(Observable o, Object obj) {
        if(obj instanceof MethodUnderTestDuration) {
            MethodUnderTestDuration data = (MethodUnderTestDuration)obj;
            durationList.add(data);
        }

    }

    /**
     * Return the List of {@link MethodUnderTestDuration}
     * @return the durationBeanList
     */
    public List<MethodUnderTestDuration> getDurationList() {
        return durationList;
    }
    

}
