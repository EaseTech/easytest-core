package org.easetech.easytest.loader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * An interface for different types of loader. 
 * This would ultimately be used by the users of JUnit as well to provide their custom Loaders.
 * The work for that will begin soon. 
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
     * The key identifying the Test Status either PASSED/FAILED 
     * determined after comparing expected and actual results, and written to the file.
     */
    String TEST_STATUS = "TestStatus";
    
    /**
     * The constants for test status PASSED/FAILED 
     */    
    final String TEST_PASSED = "PASSED";
    final String TEST_FAILED = "FAILED";
    
    /**
     * Method responsible to Load the test data from the list of files passed as parameter
     * @param filePaths the list of files from which to load the data
     * @return a Map consisting of the methodName as key and a List of Key/value pairs as the value of the Map.
     * This is currently not a user friendly way of exposing the test data. 
     */
    Map<String, List<Map<String, Object>>> loadData(String[] filePaths);
    
    /**
     * Method responsible for writing the test data and actual result back to the file
     * @param filePaths the paths to the file to which data needs to be written
     * @param methodName the name of the method to write the data for
     * @param actualData a Map consisting of the methodName as key and a List of Key/value pairs as the value of the Map. 
     * This Map contains the input as well as output data 
     * This is currently not a user friendly way of exposing the test data. 
     */
    void writeData(String[] filePaths, String methodName, Map<String, List<Map<String, Object>>> actualData);
    
    /**
     * Method responsible for writing the test data and actual result back to the file
     * @param filePath the path to the file to which data needs to be written
     * @param actualData a Map consisting of the methodName as key and a List of Key/value pairs as the value of the Map. 
     * This Map contains the input as well as output data 
     * This is currently not a user friendly way of exposing the test data. 
     */
    void writeFullData(FileOutputStream fos, Map<String, List<Map<String, Object>>> actualData);
    
    /**
     * Load the Data from Excel spreadsheet.It uses Apache POI classes to load the data.
     * 
     * @param excelFile the excel file input stream to load the data from
     * @return the loaded data.
     * @throws IOException if an exception occurs while loading the data
     */
    public Map<String, List<Map<String, Object>>> loadFromInputStream(final InputStream file);

}
