
package org.easetech.easytest.loader;

import org.easetech.easytest.runner.DataDrivenTestRunner;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.easetech.easytest.util.DataContext;

/**
 * 
 * A Utility class that helps {@link DataDrivenTestRunner} and {@link DataContext} classes to store the input test data in a
 * format that is easy to display to the user as well as easy to interpret.
 * 
 * @author Anuj Kumar
 * 
 */
public final class DataConverter {
    
    private DataConverter(){
        //private constructor
    }

    /**
     * Converts/normalize the input test data such that the name of the method is: <li>prepended with the name of the
     * Class that the method is associated with, and <li>appended with the input test data that the method will take.
     * 
     * @param from the original input test data
     * @param currentTestClass the class of the methods that this input test data belongs to
     * @return a Normalized input test data.
     */
    public static Map<String, List<Map<String, Object>>> convert(Map<String, List<Map<String, Object>>> from,
        Class<?> currentTestClass) {
        Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
        for (String method : from.keySet()) {
            List<Map<String, Object>> value = from.get(method);
            for (Map<String, Object> singleTestMethod : value) {
                result.put(getFullyQualifiedTestName(method, currentTestClass).concat(singleTestMethod.toString()),
                    Collections.singletonList(singleTestMethod));
            }

        }
        return result;
    }

    /**
     * Returns the fully qualified name of the test method such that: <li>if the input testClass is null, return the
     * test method name, else <li>prepend the test method name with the name of the class and return.
     * 
     * @param testMethod the name of the test method. Cannot be null
     * @param testClass the name of the test class, can be null.
     * @return Fully qualified test method name
     */
    public static String getFullyQualifiedTestName(String testMethod, Class testClass) {
        return testClass == null ? testMethod : testClass.getName().concat(":").concat(testMethod);
    }

    /**
     * Get truncated method name such that: <li>if the testMethod name starts with the testClass name, remove the name
     * of the class and return the result, else <li>return the test method
     * 
     * @param testMethod the name of the test method. Cannot be null
     * @param testClass the name of the test class, can be null.
     * @return the truncated method name
     */
    public static String getTruncatedMethodName(String testMethod, Class testClass) {
        return testMethod.startsWith(testClass.getName()) ? testMethod.replaceAll(testClass.getName(), "") : testMethod;

    }

    /**
     * Prepend the className to the name of the method
     * 
     * @param from the input test data
     * @param currentTestClass the currently executing test class
     * @return the converted test data
     */
    public static Map<String, List<Map<String, Object>>> appendClassName(Map<String, List<Map<String, Object>>> from,
        Class<?> currentTestClass) {
        Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
        for (String method : from.keySet()) {
            result.put(getFullyQualifiedTestName(method, currentTestClass), from.get(method));
        }
        return result;
    }

}
