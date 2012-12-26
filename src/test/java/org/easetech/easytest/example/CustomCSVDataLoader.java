
package org.easetech.easytest.example;

import com.csvreader.CsvReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.easetech.easytest.io.Resource;
import org.easetech.easytest.loader.Loader;
import org.junit.Assert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An implementation of {@link Loader} for the CSV based files. This Loader is responsible for reading a list of CSV
 * based files and converting them into a data structure which is understandable by the JUnit framework. It expects the
 * format of the CSV file to be like this :<br>
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
 * 
 * @author Anuj Kumar
 * 
 */
public class CustomCSVDataLoader implements Loader {

    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(CustomCSVDataLoader.class);

    /** Separator between columns in a CSV file */
    private static final char COMMA_SEPARATOR = ',';

    /**
     * 
     * Construct a new CSVDataLoader
     */
    public CustomCSVDataLoader() {
        super();
    }

    /**
     * Construct a new CSVDataLoader and also load the data.
     * 
     * @param csvInputStreams the input stream to load the data from
     * @throws IOException if an IO Exception occurs
     */
    public CustomCSVDataLoader(final List<InputStream> csvInputStreams) throws IOException {
        Map<String, List<Map<String, Object>>> data = null;
        Map<String, List<Map<String, Object>>> finalData = new HashMap<String, List<Map<String, Object>>>();
        for (InputStream stream : csvInputStreams) {
            data = loadFromSpreadsheet(stream);
            finalData.putAll(data);
        }

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


    public Map<String, List<Map<String, Object>>> loadData(Resource resource) {
        System.out.println("Using my custom Loader");
        Map<String, List<Map<String, Object>>> result = new HashMap<String, List<Map<String, Object>>>();
            try {
                result = loadFromSpreadsheet(resource.getInputStream());
            } catch (Exception e) {
                try {
                    Assert.fail("An I/O exception occured while reading the files from the path :" + resource.getResourceName());
                } catch (Exception e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }
        return result;
    }

    public void writeData(Resource resource, Map<String, List<Map<String, Object>>> actualData, String... methodName) {
        // TODO Auto-generated method stub
        
    }

}
