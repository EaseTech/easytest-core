package org.easetech.easytest.loader;

import org.easetech.easytest.annotation.DataLoader;

import java.util.List;
import java.util.Map;
import org.easetech.easytest.io.Resource;

/**
 * An interface for different types of loader. 
 * This would ultimately be used by the users of EasyTest as well to provide their custom Loaders.
 * Currently, EasyTest supports three Loaders internally :
 * <li> {@link ExcelDataLoader} - To load data from XLS files</li>
 * <li> {@link XMLDataLoader} - To load data from XML files. Look at the Schema for XML at : 
 * https://github.com/EaseTech/easytest-core/blob/master/src/main/resources/testDataSchema.xsd</li>
 * <li> {@link CSVDataLoader} - To load data from a CSV file
 * 
 * In addition users can define their own custom Loaders and use them in their test 
 * classes by specifying the {@link LoaderType#CUSTOM} loader type in their {@link DataLoader} annotation
 * 
 * @author Anuj Kumar
 *
 */
public interface Loader {
    /**
     * The key identifying the actual output result that needs to be written to the file.
     */
    String ACTUAL_RESULT = "ActualResult";
    
    /**
     * The key identifying the expected output that needs to be compared with actual result
     */
    String EXPECTED_RESULT = "ExpectedResult";
    
    /**
     * Constant for empty string
     */
    String EMPTY_STRING = " ";
    
    /**
     * Double Quote String
     */
    String DOUBLE_QUOTE = "\"";
    
    /**
     * Single Quote
     */
    String SINGLE_QUOTE = "'";
    
    /**
     * The ampersand sign
     */
    String AMPERSAND = "&";
    
    /**
     * Comma
     */
    String COMMA = ",";
    /**
     * The key identifying the Test Status either PASSED/FAILED 
     * determined after comparing expected and actual results, and written to the file.
     */
    String TEST_STATUS = "TestStatus";
    
    /**
     * The key identifying the Test Duration in milli seconds
     */
    String DURATION = "Duration(ms)";
    
    /**
     * The constants for test status PASSED/FAILED 
     */    
    final String TEST_PASSED = "PASSED";
    final String TEST_FAILED = "FAILED";
    
    /**
     * Method responsible to Load the test data from the list of files passed as parameter
     * @param resource from which to load the data
     * @return a Map consisting of the methodName as key and a List of Key/value pairs as the value of the Map.
     * This is currently not a user friendly way of exposing the test data. 
     */
    Map<String, List<Map<String, Object>>> loadData(Resource resource);
    
    /**
     * Method responsible for writing the test data and actual result back to the file
     * @param resource the resource to which data needs to be written
     * @param methodNames the names of the method to write the data for
     * @param actualData a Map consisting of the methodName as key and a List of Key/value pairs as the value of the Map. 
     * This Map contains the input as well as output data 
     * This is currently not a user friendly way of exposing the test data. 
     */
    void writeData(Resource resource, Map<String, List<Map<String, Object>>> actualData, String... methodNames);

}
