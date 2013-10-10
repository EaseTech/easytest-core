package org.easetech.easytest.reports.data;


import org.easetech.easytest.annotation.Duration;

import org.easetech.easytest.annotation.Intercept;

import java.lang.reflect.Method;

/**
 * Class encapsulating the run information
 * of a particular method of a class that is being tested by a particular Test Methoid.
 * This class is instantiated when a user wants to intercept the calls to the methods
 *  of a class under test using {@link Intercept} or {@link Duration} annotation.
 *  
 *  For example the below code specifies the annotation {@link Duration} at the field ItemService. This means
 *  that this service will be proxied by EasyTest and EasyTest will try to capture the duration it took 
 *  for the method of the class ItemService and compare it with the expectedTimeInMillis attribute.
 *  All this information is captured by EasyTest inside this class's instance in order to provide the reporting functionality 
 *  with all the requisite information it requires to produce a report.
 *  <pre>
 *   public class ItemServiceTest {
 *      
 *      {@literal}@Duration(timeInMillis=30)
 *      private ItemService itemService
 *      
 *      public Item testGetItem({@literal}@Param(name="itemType") String itemType , {@literal}@Param(name="expectedItem")Item expectedItem) {
 *          Item item = itemService.getItem(itemType);
 *          Assert.assertNotNull(item);
 *          Assert.assertEquals(expectedItem , item);
 *          return item;
 *      }
 *</pre>
 *
 *@author anuj kumar
 */
public class MethodUnderTestDuration {
    
    /**
     * The class under test. It is different from the test class.
     */
    private Class<?> classUnderTest;
    
    /**
     * Method of the {@link #classUnderTest} whose information is being captured for reporting purposes.
     */
    private Method methodUnderTest;
    
    /**
     * The expected duration in milliseconds that the {@link #methodUnderTest} should have taken
     */
    private Long expectedDurationinMillis;
    
    /**
     * The actual duration in milliseconds that the {@link #methodUnderTest} took
     */
    private Long actualDurationinMillis;
    
    /**
     * The arguments to the {@link #methodUnderTest}
     */
    private Object[] methodArguments;
    
    /**
     * The result returned by {@link #methodUnderTest}
     */
    private Object methodResult;
    
    

    /**
     * Get the arguments to the {@link #methodUnderTest}
     * @return the methodArguments
     */
    public Object[] getMethodArguments() {
        return methodArguments;
    }

    /**
     * Set the arguments to the {@link #methodUnderTest}
     * @param methodArguments the methodArguments to set
     */
    public void setMethodArguments(Object[] methodArguments) {
        this.methodArguments = methodArguments;
    }

    /**
     * Get the result returned by {@link #methodUnderTest}
     * @return the methodResult
     */
    public Object getMethodResult() {
        return methodResult;
    }

    /**
     * Set the result returned by {@link #methodUnderTest}
     * @param methodResult the methodResult to set
     */
    public void setMethodResult(Object methodResult) {
        this.methodResult = methodResult;
    }

    /**
     * @see #classUnderTest
     * @return the classUnderTest
     */
    public Class<?> getClassUnderTest() {
        return classUnderTest;
    }

    /**
     * @see #classUnderTest
     * @param classUnderTest the classUnderTest to set
     */
    public void setClassUnderTest(Class<?> classUnderTest) {
        this.classUnderTest = classUnderTest;
    }

    /**
     * @see #methodUnderTest
     * @return the methodUnderTest
     */
    public Method getMethodUnderTest() {
        return methodUnderTest;
    }

    /**
     * @see #methodUnderTest
     * @param methodUnderTest the methodUnderTest to set
     */
    public void setMethodUnderTest(Method methodUnderTest) {
        this.methodUnderTest = methodUnderTest;
    }
    
    
    /**
     * @see #expectedDurationinMillis
     * @return the expectedDurationinMillis
     */
    public Long getExpectedDurationinMillis() {
        return expectedDurationinMillis;
    }

    /**
     * @see #expectedDurationinMillis
     * @param expectedDurationinMillis the expectedDurationinMillis to set
     */
    public void setExpectedDurationinMillis(Long expectedDurationinMillis) {
        this.expectedDurationinMillis = expectedDurationinMillis;
    }

    /**
     * @see #actualDurationinMillis
     * @return the actualDurationinMillis
     */
    public Long getActualDurationinMillis() {
        return actualDurationinMillis;
    }

    /**
     *  @see #actualDurationinMillis
     * @param actualDurationinMillis the actualDurationinMillis to set
     */
    public void setActualDurationinMillis(Long actualDurationinMillis) {
        this.actualDurationinMillis = actualDurationinMillis;
    }

    /**
     * Get the name of the method under test
     * @return the name of the method under test
     */
    public String getMethodName() {
        return methodUnderTest == null ? null : methodUnderTest.getName();
    }
    
    /**
     * Get the name of the class under test
     * @return the name of the class under test
     */
    public String getClassName() {
        return classUnderTest == null ? null : classUnderTest.getSimpleName();
    }

    /**
     * @return
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((classUnderTest == null) ? 0 : classUnderTest.hashCode());
        result = prime * result + ((methodUnderTest == null) ? 0 : methodUnderTest.hashCode());
        return result;
    }

    /**
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MethodUnderTestDuration other = (MethodUnderTestDuration) obj;
        if (classUnderTest == null) {
            if (other.classUnderTest != null)
                return false;
        } else if (!classUnderTest.getName().equals(other.classUnderTest.getName()))
            return false;
        if (methodUnderTest == null) {
            if (other.methodUnderTest != null)
                return false;
        } else if (!methodUnderTest.getName().equals(other.methodUnderTest.getName()))
            return false;
        return true;
    }
    
    

}
