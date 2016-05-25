
package org.easetech.easytest.loader;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.easetech.easytest.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link Loader} for the CSV based files. This Loader is responsible for reading and writing to a list of CSV
 * based files and converting them into a data structure which is understandable by the EasyTest framework. It expects
 * the format of the CSV file to be like this :<br>
 * <code>
 * <B>testGetItems,LibraryId,itemType,searchText</B>
 * <br>
 * ,4,journal,batman
 * ,2,ebook,spiderman
 * <br>
 * </code>
 * where <B>testGetItems</B> represents the name of the test method for which the test data is being defined,<br>
 * <B>LibraryId,itemType,searchText</B> represents the test data fields for the test method, and</br>
 * <B>,4,journal,batman (and ,2,ebook,spiderman)</B> represents the actual test data to be passed to the test method.
 * Each row in the CSV file represents a single set of test data.<br>
 * 
 * Note the leading "," in the test data row. it is mandatory to use and tells the 
 * framework that testGetItems is just a method name and does not have any value.<br>
 * 
 * A CSV cannot have a blank line in between test data whether it is for a single test or for multiple tests.
 * The framework is capable of handling multiple test datas for multiple test methods in a single CSV file. 
 * Although a user can choose to define the test data in multiple files as well.
 * <br>
 * If you want to pass a Collection to the test method, just separate each instance with a ":". For eg. to pass
 * a list of Itemids , pass them as a colon separated list like this -> 12:34:5777:9090 <br>
 * 
 * This {@link Loader} can also write the data back to the file, either for the given method names or for all
 * the methods in the file. This is handy when we want to write the output data back to the file. 
 *  
 * 
 * @author Anuj Kumar
 * 
 */
public class CSVDataLoader implements Loader {

    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(CSVDataLoader.class);

    /** Separator between columns in a CSV file */
    private static final char COMMA_SEPARATOR = ',';

    /**
     * 
     * Construct a new CSVDataLoader
     */
    public CSVDataLoader() {
        super();
    }

    /**
     * Construct a new CSVDataLoader and also load the data.
     * 
     * @param csvInputStreams the input stream to load the data from
     * @throws IOException if an IO Exception occurs
     */
    public CSVDataLoader(final List<InputStream> csvInputStreams) throws IOException {
        Map<String, List<Map<String, Object>>> data = null;
        Map<String, List<Map<String, Object>>> finalData = new HashMap<String, List<Map<String, Object>>>();
        for (InputStream stream : csvInputStreams) {
            data = loadFromSpreadsheet(stream);
            finalData.putAll(data);
        }

    }
    

    /**
     * Load the data for the given Resource
     * @param resource
     * @return
     */
    public Map<String, List<Map<String, Object>>> loadData(Resource resource) {
        Map<String, List<Map<String, Object>>> result = null;
        try {
            result = loadFromSpreadsheet(resource.getInputStream());
        } catch (IOException e) {
            LOG.error("IOException occured while trying to Load the resource {} . Moving to the next resource.", resource.getResourceName(), e);
        }
        if(result != null){
            LOG.debug("Loading data from resource {} succedded and the data loaded is {}", resource.getResourceName(),
                result);
        }
        
        return result;
    }
    
    /**
     * Load data from SpreadSheet
     * 
     * @param csvFile the csv file to load the data from
     * @return a map of data
     * @throws IOException if an IO exception occurs
     */
    private static Map<String, List<Map<String, Object>>> loadFromSpreadsheet(final InputStream csvFile)
        throws IOException {
        Map<String, List<Map<String, Object>>> data = new HashMap<String, List<Map<String, Object>>>();
        CsvReader csvReader = new CsvReader(new InputStreamReader(csvFile), COMMA_SEPARATOR);
        Boolean isKeyRow = true;
        List<Map<String, Object>> dataValues = null;
        Map<Integer, String> tempData = new HashMap<Integer, String>();
        data = new HashMap<String, List<Map<String, Object>>>();
        
        while (csvReader.readRecord()) {
            StringBuilder logBuffer = new StringBuilder("Record being read is :");
            Map<String, Object> actualData = new HashMap<String, Object>();   
            String[] splitValues = csvReader.getValues();
            if (splitValues.length > 0 && "".equals(splitValues[0])) {
                isKeyRow = false;
            } else {
                isKeyRow = true;
            }
            if (isKeyRow) {
                dataValues = new ArrayList<Map<String, Object>>();
                for (int i = 0; i < splitValues.length; i++) {
                    tempData.put(i, splitValues[i]);
                    logBuffer.append(":" + splitValues[i]);
                }

                data.put(tempData.get(0), dataValues);
            } else {
                for (int i = 1; i < splitValues.length; i++) {
                    
                    actualData.put(tempData.get(i), normalize(splitValues[i]));
                    logBuffer.append(":" + splitValues[i]);
                }
            }
            if (!isKeyRow) {
                dataValues.add(actualData);
            }
            LOG.debug(logBuffer.toString());
        }
        return data;

    }
    
    /**
     * Normalize the string that is read from a CSV file for JSON conversion if required
     * @param value the string to normalize
     * @return the normalized string
     */
    private static String normalize(String value){
        return value.replaceAll(SINGLE_QUOTE, DOUBLE_QUOTE).replaceAll(AMPERSAND, COMMA);
    }
    
    /**
     * Write the data back to the file that is represented by the Resource instance
     * @param resource the resource instance to which teh data needs to be written
     * @param actualData the actual data that needs to be written
     * @param methodNames OPTIONAL names of methods for which the data needs to be written. If the method 
     * names are not provided, then the data is written for all the test methods ofr which teh data is present 
     * in the actualData parameter
     */
    public void writeData(Resource resource, Map<String, List<Map<String, Object>>> actualData, String... methodNames) {
        if (methodNames == null || methodNames.length == 0) {
    	    writeFullDataToCSV(resource, actualData);
		} else {
		    for (String methodName : methodNames) {
		    	writeDataToCSV(resource, actualData,methodName);
		    }

		}

    }
    
    private void writeFullDataToCSV(Resource resource,
			Map<String, List<Map<String, Object>>> actualData) {
    	
  
        List<String[]> writableData = new ArrayList<String[]>();
        try {  
            for (String methodName : actualData.keySet()) {
                boolean isHeaderWritten = false;
                Map<String, Integer> parameterIndexMap = new LinkedHashMap<String, Integer>();
                int noOfColumns = 0;
                for (Map<String, Object> methodData : actualData.get(methodName)) {
                    // rowNum increment by one to proceed with next record of the method.
                    LOG.debug("methodData.keySet().size" + methodData.keySet().size());
                    LOG.debug("methodData" + methodData);                    

                    if (!isHeaderWritten) {
                        int columnIndex = 0;
                        noOfColumns = methodData.keySet().size()+1;
                        String[] headerValues = new String[noOfColumns];
                        // Write the method name and parameter names in header.
                        //writeDataToCell(sheet, rowNum, columnIndex++, methodName);
                        headerValues[columnIndex++] = methodName;
                        for (String parameterName : methodData.keySet()) {
                        	headerValues[columnIndex] = parameterName;                             
                         // capturing column index so that corresponding values will be placed at same column
                            parameterIndexMap.put(parameterName, columnIndex);
                            columnIndex++;
                        }
                        //Next write the Duration header
                        parameterIndexMap.put(DURATION, columnIndex++);
                        // incrementing row after writing header
                        //rowNum++;
                        isHeaderWritten = true;
                        writableData.add(headerValues);
                    }

                    // Write the actual result and test status values.
                    if (isHeaderWritten) {
                        int columnIndex = 0;
                        String[] parameterValues = new String[noOfColumns];
                        // we need to put empty cell in first column as per easytest csv structure.

                        parameterValues[columnIndex++] = null;
                        for (String parameter : methodData.keySet()) {
                        	parameterValues[parameterIndexMap.get(parameter)] = methodData.get(parameter)!=null?methodData.get(parameter).toString():null;
                        }
                        //Finally put the Duration value
                        parameterValues[parameterIndexMap.get(DURATION)] = methodData.get(DURATION).toString();
                        writableData.add(parameterValues);
                    }

                }
            }
  
            CsvWriter csvWriter = new CsvWriter(resource.getOutputStream(), COMMA_SEPARATOR,Charset.defaultCharset());
            // finally we have the values in order to be written to the CSV file.
            for (String[] data : writableData) {
                csvWriter.writeRecord(data);
                
            }
            csvWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch(Exception ex){
        	ex.printStackTrace();
            throw new RuntimeException(ex);
        }
		
	}

	/**
     * Write the Data to the given Resource
     * @param resource the resource representing the CSV file to which teh data should be written
     * @param actualData the actual data to write back
     * @param methodNames the optional names of methods for which the data shouuld be written. If this varargs is empty,
     * then the data will be written back for all the methods.
     */
    private void writeDataToCSV(Resource resource, Map<String, List<Map<String, Object>>> actualData, String... methodNames) {
        Boolean isKeyRow = true;
        List<String[]> writableData = new ArrayList<String[]>();
        try {
            CsvReader csvReader = new CsvReader(new InputStreamReader(resource.getInputStream()), COMMA_SEPARATOR);
            // use FileWriter constructor that specifies open for overriding

            String currentMethodName = "";
            int dataRowIndex = 0;
            String[] dataKeys = null;
            while (csvReader.readRecord()) {
                
                String[] splitValues = csvReader.getValues();
                String[] newSplitValues = (String[])Array.newInstance(String[].class.getComponentType(), splitValues.length);
                System.arraycopy(splitValues, 0, newSplitValues, 0, Math.min(splitValues.length, newSplitValues.length));
                if (splitValues.length > 0 && "".equals(splitValues[0])) {
                    isKeyRow = false;

                } else {
                    dataKeys = splitValues;
                    isKeyRow = true;
                    currentMethodName = splitValues[0];
                    if (!writeDataForMethod(currentMethodName, methodNames)) {
                        writableData.add(splitValues);
                        continue;
                    }
                }

                if (isKeyRow) {
                    dataRowIndex = 0;
                    List<Map<String, Object>> currentMethodData = actualData.get(currentMethodName);
                    int length = splitValues.length;
                    if (currentMethodData != null && !currentMethodData.isEmpty()) {
                        
                        if (currentMethodData.get(0).keySet().contains(Loader.TEST_STATUS)) {
                            //This means we have to write 3 extra fields to the CSV file: ActualResult, TestStatus and Duration
                            
                            newSplitValues = (String[])Array.newInstance(String[].class.getComponentType(), length + 3);
                            System.arraycopy(splitValues, 0, newSplitValues, 0, Math.min(splitValues.length, newSplitValues.length));
                            newSplitValues[length] = Loader.ACTUAL_RESULT;
                            newSplitValues[length + 1] = TEST_STATUS;
                            newSplitValues[length + 2] = DURATION;
                        }else if(currentMethodData.get(0).keySet().contains(ACTUAL_RESULT)){
                            //This means that method is returning data but user has not specified expected result param.
                            //Thus we only write back actual result and duration
                            newSplitValues = (String[])Array.newInstance(String[].class.getComponentType(), length + 2);
                            System.arraycopy(splitValues, 0, newSplitValues, 0, Math.min(splitValues.length, newSplitValues.length));
                            newSplitValues[length] = Loader.ACTUAL_RESULT;
                            newSplitValues[length + 1] = DURATION;
                        }else{
                            //Write only the Duration of the method
                            newSplitValues = (String[])Array.newInstance(String[].class.getComponentType(), length + 1);
                            System.arraycopy(splitValues, 0, newSplitValues, 0, Math.min(splitValues.length, newSplitValues.length));
                            newSplitValues[length] = DURATION;
                        }
                        dataKeys = newSplitValues;
                    }
                    writableData.add(newSplitValues);
                    if(length == 1){
                        //implies that the test method does not take any input parameters
                        //We should then handle writing the output data specific to easyTest here and now.
                        if(! currentMethodData.isEmpty()) {
                            Map<String, Object> currentRowData = currentMethodData.get(dataRowIndex++);
                            String[] finalValues = new String[dataKeys.length];
                            finalValues[0] = EMPTY_STRING;
                            writeOutputData(currentRowData, finalValues, dataKeys);
                            writableData.add(finalValues);
                        }
                        
                    }
                } else {
                    if (!writeDataForMethod(currentMethodName, methodNames)) {
                        writableData.add(splitValues);
                        continue;
                    }
                    List<Map<String, Object>> currentMethodData = actualData.get(currentMethodName);
                    Map<String, Object> currentRowData = currentMethodData.get(dataRowIndex++);
                    String[] finalValues = new String[dataKeys.length];
                    finalValues[0] = EMPTY_STRING;
                    for(int i=1 ;i<dataKeys.length ; i++){
                        finalValues[i] = currentRowData.get(dataKeys[i]) == null ? "null" :currentRowData.get(dataKeys[i]).toString();
                    }
                    writeOutputData(currentRowData, finalValues, dataKeys);
                    writableData.add(finalValues);
                }

            }
            FileWriter fileWriter = new FileWriter(resource.getFile(), false);
            CsvWriter csvWriter = new CsvWriter(fileWriter, COMMA_SEPARATOR);
            // finally we have the values in order to be written to the CSV file.
            for (String[] data : writableData) {
                csvWriter.writeRecord(data);
                
            }
            csvWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }catch(Exception ex){
            throw new RuntimeException(ex);
        }

    }
    
    
    private void writeOutputData(Map<String, Object> currentRowData, String[] finalValues , String[] dataKeys ){
        if(currentRowData.get(TEST_STATUS) != null){
            //Write Actual Result, Test Status and Duration fields
            finalValues[dataKeys.length - 3] = currentRowData.get(ACTUAL_RESULT).toString();
            finalValues[dataKeys.length - 2] = currentRowData.get(TEST_STATUS).toString();
            finalValues[dataKeys.length - 1] = currentRowData.get(DURATION).toString();
        }else if(currentRowData.get(ACTUAL_RESULT) != null){
            //Write Actual Result and Duration
            finalValues[dataKeys.length - 2] = currentRowData.get(ACTUAL_RESULT).toString();
            finalValues[dataKeys.length - 1] = currentRowData.get(DURATION).toString();
        }else{
            //Write Only Duration
            finalValues[dataKeys.length - 1] = currentRowData.get(DURATION).toString();
        }
    }
    /**
     * Method determining whether the data for a given method should be written or not
     * @param currentMethod
     * @param methodNames
     * @return
     */
    private Boolean writeDataForMethod(String currentMethod , String... methodNames){
        //If the methodNames is null or empty it means the data needs to be written for each
        //of the methods
        if(methodNames == null || methodNames.length == 0){
            return true;
        }
        for(String methodName : methodNames){
            if(methodName.equals(currentMethod)){
                return true;
            }
        }
        //We want to write data for specific methods but the not for the currentMethod
        return false;
    }

}
