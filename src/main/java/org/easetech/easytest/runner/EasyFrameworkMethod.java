package org.easetech.easytest.runner;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import org.easetech.easytest.annotation.Repeat;
import org.easetech.easytest.reports.data.TestResultBean;
import org.junit.runners.model.FrameworkMethod;
/**
 * An extension of {@link FrameworkMethod} to introduce custom method name getters and setters.
 * 
 * @author Anuj Kumar
 *
 */
public class EasyFrameworkMethod extends FrameworkMethod {
    
    private List<EasyFrameworkMethod> childMethods;

    /**
     * @return the childMethods
     */
    public List<EasyFrameworkMethod> getChildMethods() {
        return childMethods;
    }

    /**
     * @param childMethods the childMethods to set
     */
    public void setChildMethods(List<EasyFrameworkMethod> childMethods) {
        this.childMethods = childMethods;
    }

    /**
     * The name of the method
     */
    private String methodName;
    
    /**
     * The test data associated with the Method
     */
    private final Map<String, Object> testData; 
    
    /**
     * The {@link TestResultBean} associated with the method
     */
    private final TestResultBean testResult;
    
    /** The name of the method to fetch test data. This field had to be introduced
     * when we started the support for {@link Repeat} annotation. the way the lgic works for repeat,
     * mandated us to separate the name of the test method with the name of the method 
     * that acted as an ID to fetch the correct data set for a particular run. 
     */
    private final String methodNameForTestData;
    
    /**
     * 
     * Construct a new EasyFrameworkMethod
     * @param method the method
     */
    public EasyFrameworkMethod(Method method , Map<String, Object> testData , TestResultBean testResult , String methodNameForTestData) {
        super(method);
        this.methodName = method.getName();
        this.testData = testData;
        this.testResult = testResult;
        this.methodNameForTestData = methodNameForTestData;
        
    }
    
    /**
     * @return the methodNameForTestData
     */
    public String getMethodNameForTestData() {
        return methodNameForTestData;
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
