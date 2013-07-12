package org.easetech.easytest.runner;

import org.easetech.easytest.reports.data.TestResultBean;

import java.util.Map;

import java.lang.reflect.Method;

import org.junit.runners.model.FrameworkMethod;
/**
 * An extension of {@link FrameworkMethod} to introduce custom method name getters and setters.
 * 
 * @author Anuj Kumar
 *
 */
public class EasyFrameworkMethod extends FrameworkMethod {

    /**
     * The name of the method
     */
    private String methodName;
    
    private final Map<String, Object> testData; 
    
    private final TestResultBean testResult;
    
    /**
     * 
     * Construct a new EasyFrameworkMethod
     * @param method the method
     */
    public EasyFrameworkMethod(Method method , Map<String, Object> testData , TestResultBean testResult) {
        super(method);
        this.methodName = method.getName();
        this.testData = testData;
        this.testResult = testResult;
        
    }
    
    /**
     * Returns the method's name
     */
    
    public String getName() {
        return this.methodName;
    }
    
    /**
     * Set the method's name
     * @param name the name of the method to set.
     */
    public void setName(String name){
        this.methodName = name;
    }

    /**
     * @return the testData
     */
    public Map<String, Object> getTestData() {
        return testData;
    }

    /**
     * @return the testResult
     */
    public TestResultBean getTestResult() {
        return testResult;
    }
    
    

}
