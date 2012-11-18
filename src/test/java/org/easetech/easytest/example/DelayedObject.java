package org.easetech.easytest.example;

import java.util.concurrent.TimeUnit;

import java.util.concurrent.Delayed;

public class DelayedObject implements Delayed {
    
    private Long value;
    

    @Override
    public String toString() {
        return "DelayedObject [value=" + value + "]";
    }

    public Long getValue() {
        return value;
    }

    public void setValue(Long value) {
        this.value = value;
    }

    /**
     * Construct a new DelayedObject
     * @param value
     */
    public DelayedObject(Long value) {
        super();
        this.value = value;
    }

    @Override
    public int compareTo(Delayed o) {
        
        return 1;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        
        return 0;
    }

}
