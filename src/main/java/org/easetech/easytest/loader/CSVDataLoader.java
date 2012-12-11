
package org.easetech.easytest.loader;

import java.lang.reflect.Array;

import com.csvreader.CsvReader;
import com.csvreader.CsvWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.easetech.easytest.util.ResourceLoader;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link Loader} for the CSV based files. This Loader is responsible for reading a list of CSV
 * based files and converting them into a data structure which is understandable by the EasyTest framework. It expects
 * the format of the CSV file to be like this :<br>
 * <code>
 * <B>testGetItems,LibraryId,itemType,searchText</B>
 * <br>
 * ,4,journal,batman
 * ,2,ebook,spiderman
 * <br>
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
 * a list of Itemids , pass them as a colon separated list like this -> 12:34:5777:9090 
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
     * Construct a new CSVDataLoader and also load the data.
     * 
     * @param dataFiles the list of input stream string files to load the data from
     * @return a Map of method name and the list of associated test data with that method name
     * @throws IOException if an IO Exception occurs
     */
    private Map<String, List<Map<String, Object>>> loadCSVData(final List<String> dataFiles) throws IOException {
        Map<String, List<Map<String, Object>>> data = null;
        Map<String, List<Map<String, Object>>> finalData = new HashMap<String, List<Map<String, Object>>>();
        for (String filePath : dataFiles) {
            try {
                ResourceLoader resource = new ResourceLoader(filePath);
                data = loadFromSpreadsheet(resource.getInputStream());
            } catch (FileNotFoundException e) {
                LOG.error("The specified file was not found. The path is : {}", filePath);
                LOG.error("Continuing with the loading of next file.");
                continue;
            } catch (IOException e) {
                LOG.error("IO Exception occured while trying to read the data from the file : {}", filePath);
                LOG.error("Continuing with the loading of next file.");
                continue;
            }
            finalData.putAll(data);
        }
        return finalData;

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
                }

                data.put(tempData.get(0), dataValues);
            } else {
                for (int i = 1; i < splitValues.length; i++) {
                    actualData.put(tempData.get(i), splitValues[i]);
                }
            }
            if (!isKeyRow) {
                dataValues.add(actualData);
            }
        }
        return data;

    }

    /**
     * Load the data from the specified list of filePaths
     * 
     * @param filePaths the list of File paths
     * @return the data
     */
    public Map<String, List<Map<String, Object>>> loadData(String[] filePaths) {
        Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
        try {
            result = loadCSVData(Arrays.asList(filePaths));
        } catch (IOException e) {
            Assert.fail("An I/O exception occured while reading the files from the path :" + filePaths.toString());
        }
        return result;
    }

    public void writeData(String[] filePaths, String methodName, Map<String, List<Map<String, Object>>> actualData) {
        Boolean isKeyRow = true;
        List<String[]> writableData = new ArrayList<String[]>();
        try {
            ResourceLoader resource = new ResourceLoader(filePaths[0]);
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
                    if (!currentMethodName.equals(methodName)) {
                        writableData.add(splitValues);
                        continue;
                    }
                }

                if (isKeyRow) {
                    dataRowIndex = 0;
                    List<Map<String, Object>> currentMethodData = actualData.get(currentMethodName);
                    if (currentMethodData != null && !currentMethodData.isEmpty()) {
                        if (currentMethodData.get(0).keySet().contains(Loader.ACTUAL_RESULT)) {
                            int length = splitValues.length;
                            newSplitValues = (String[])Array.newInstance(String[].class.getComponentType(), length + 1);
                            System.arraycopy(splitValues, 0, newSplitValues, 0, Math.min(splitValues.length, newSplitValues.length));
                            newSplitValues[length] = Loader.ACTUAL_RESULT;
                        }
                    }
                    writableData.add(newSplitValues);
                } else {
                    if (!currentMethodName.equals(methodName)) {
                        writableData.add(splitValues);
                        continue;
                    }
                    List<Map<String, Object>> currentMethodData = actualData.get(currentMethodName);
                    Map<String, Object> currentRowData = currentMethodData.get(dataRowIndex++);
                    String[] finalValues = new String[dataKeys.length];
                    finalValues[0] = EMPTY_STRING;
                    for(int i=1 ;i<dataKeys.length ; i++){
                        finalValues[i] = (currentRowData.get(dataKeys[i]) == null ? "null" :currentRowData.get(dataKeys[i]).toString());
                    }
                    writableData.add(finalValues);
                }

            }
            CsvWriter csvWriter = new CsvWriter(resource.getFileWriter(false), COMMA_SEPARATOR);
            // finally we have the values in order to be written to the CSV file.
            for (String[] data : writableData) {
                csvWriter.writeRecord(data);
                
            }
            csvWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

	public void writeFullData(FileOutputStream fos,
			Map<String, List<Map<String, Object>>> actualData) {
		// TODO Auto-generated method stub
		
	}

	public Map<String, List<Map<String, Object>>> loadFromInputStream(
			InputStream file) {
		// TODO Auto-generated method stub
		return null;
	}

}
