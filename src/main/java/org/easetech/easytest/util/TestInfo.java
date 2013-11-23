
package org.easetech.easytest.util;

import java.util.Arrays;
import org.easetech.easytest.loader.Loader;
import org.junit.runners.model.TestClass;

/**
 * 
 * TestInfo class encapsulates the information about a given test method that could be used by various parties to get
 * the runtime information about the given test method. This class is currently being used by the
 * {@link RunAftersWithOutputData} to find and call the right Loader to write the data for a given method.
 * 
 */
public class TestInfo {

    /**
     * An instance of {@link TestClass} associated with the given test method info
     */
    private TestClass testClass;

    /**
     * The loader used by the method to load the data
     */
    private Loader dataLoader;

    /**
     * The files associated with the method's data
     */
    private String[] filePaths;

    /**
     * The name of the method
     */
    private String methodName;
    
    /**
     * Write the data back to the file
     */
    private Boolean writeData;

    /**
     * whether to write data back to the file or not
     * @return the writeData whether to write data back to the file or not
     */
    public Boolean getWriteData() {
        return writeData;
    }

    /**
     * whether to write data back to the file or not
     * @param writeData the writeData to set
     */
    public void setWriteData(Boolean writeData) {
        this.writeData = writeData;
    }

    /**
     * 
     * Construct a new TestInfo
     * @param testClass an instance of {@link TestClass}
     */
    public TestInfo(TestClass testClass) {
        this.testClass = testClass;

    }

    /**
     * Get Test Class instance
     * @return Test Class instance
     */
    public TestClass getTestClass() {
        return testClass;
    }

    /**
     * Get associated Data Loader
     * @return associated Data Loader
     */
    public Loader getDataLoader() {
        return dataLoader;
    }

    /**
     * Set associated Data Loader
     * @param dataLoader associated Data Loader
     */ 
    public void setDataLoader(Loader dataLoader) {
        this.dataLoader = dataLoader;
    }

    /**
     * Get filePaths
     * @return filePaths
     */
    public String[] getFilePaths() {
        return filePaths;
    }

    /**
     * Set filePaths
     * @param filePaths filePaths
     */
    public void setFilePaths(String[] filePaths) {
        this.filePaths = filePaths;
    }

    /**
     * Get method name
     * @return method name
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Set method name
     * @param methodName method name
     */
    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    /**
     * @return the overridden toString
     */
    @Override
    public String toString() {
        return "TestInfo [testClass=" + testClass + ", dataLoader=" + dataLoader + ", filePaths="
            + Arrays.toString(filePaths) + ", methodName=" + methodName + "]";
    }
    
    

}