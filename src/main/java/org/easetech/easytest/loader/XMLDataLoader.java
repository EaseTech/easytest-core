
package org.easetech.easytest.loader;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.Binder;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.easetech.easytest._1.Entry;
import org.easetech.easytest._1.InputData;
import org.easetech.easytest._1.InputTestData;
import org.easetech.easytest._1.ObjectFactory;
import org.easetech.easytest._1.OutputData;
import org.easetech.easytest._1.TestMethod;
import org.easetech.easytest._1.TestRecord;
import org.easetech.easytest.io.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 * An implementation of {@link Loader} for the XML based files. This Loader is responsible for reading a list of XML
 * Files based on the testDataSchema.xsd file of EasyTest and converting them into a data structure which is
 * understandable by the EasyTest framework. The Loader is also responsible for writing the output data back to the file.<br>
 * The XML data can be provided by the user in the following format :<br><br>
 * <code>
 * 
 * &lt;easytest:InputTestData xmlns:easytest="urn:org:easetech:easytest:1.0"
 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 xsi:schemaLocation="urn:org:easetech:easytest:1.0 testDataSchema.xsd"&gt;<br>
 * <B>&lt;TestMethod name="getSimpleData"&gt;</B><br>
 * &nbsp;&nbsp;&lt;TestRecord id="1"&gt;<br>
 * &nbsp;&nbsp;&lt;InputData&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;Entry key="libraryId" value="91475" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;Entry key="itemId" value="12" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;Entry key="itemType" value="book" /&gt;<br>
 * &nbsp;&nbsp;&lt;/InputData&gt;<br>
 * &nbsp;&nbsp;&lt;/TestRecord&gt;<br>
 * &nbsp;&nbsp;&lt;TestRecord id="2"&gt;<br>
 * &nbsp;&nbsp;&lt;InputData&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;Entry key="libraryId" value="234" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;Entry key="itemId" value="1452" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;Entry key="itemType" value="journal" /&gt;<br>
 * &nbsp;&nbsp;&lt;/InputData&gt;<br>
 * &nbsp;&nbsp;&lt;/TestRecord&gt;<br>
 * <B>&lt;/TestMethod&gt;</B><br>
 * <B>&lt;TestMethod name="getAnotherData"&gt;</B><br>
 * &nbsp;&nbsp;&lt;TestRecord id="3"&gt;<br>
 * &nbsp;&nbsp;&lt;InputData&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;Entry key="picId" value="1111" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;Entry key="picNum" value="12" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;Entry key="picFormat" value="jpeg" /&gt;<br>
 * &nbsp;&nbsp;&lt;/InputData&gt;<br>
 * &nbsp;&nbsp;&lt;/TestRecord&gt;<br>
 * &nbsp;&nbsp;&lt;TestRecord id="4"&gt;<br>
 * &nbsp;&nbsp;&lt;InputData&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;Entry key="picId" value="1561" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;Entry key="picNum" value="178" /&gt;<br>
 * &nbsp;&nbsp;&nbsp;&lt;Entry key="picFormat" value="raw" /&gt;<br>
 * &nbsp;&nbsp;&lt;/InputData&gt;<br>
 * &nbsp;&nbsp;&lt;/TestRecord&gt;<br>
 * <B>&lt;/TestMethod&gt;</B><br>
 * <B>&lt;/easytest:InputTestData&gt;</B><br><br>
 * 
 * As you can guess, the root element is {@link InputTestData} that can have multiple {@link TestMethod} elements in it.<br> 
 * Each {@link TestMethod} element identifies a method to test with its name attribute.<br>
 * Each {@link TestMethod} can have many TestRecords. Each Record identifies data for a single test execution.<br>
 * Each {@link TestRecord} element has an id attribute. This id attribute has to be unique in the entire XML file.
 * So you should not have the id = 1 for more than one test record. If you do, then the behavior is undefined in such a scenario.
 * <br>
 * {@link TestRecord} contains {@link InputData} element as well as {@link OutputData} element. A user never specifies an {@link OutputData} element.
 * If it is specified, it will be ignored by the {@link Loader}. {@link OutputData} is used internally by the {@link XMLDataLoader} to write output data back to the file. 
 * Each Entry element identifies a method parameter.
 * 
 * @author Anuj Kumar
 * 
 */
public class XMLDataLoader implements Loader {

    /**
     * An instance of logger associated with the test framework.
     */
    protected static final Logger LOG = LoggerFactory.getLogger(XMLDataLoader.class);

    /**
     * A record Position identifier that identifies the exact position of a given test record. It is useful in cases
     * where we want to compare and identify the exact test record from two different sources of data. In this case, it
     * will be used to identify the record for which an output test data needs to be written.
     */
    private static final String RECORD_POSITION = "recordPosition";

    /**
     * Load the data from the given resource
     * @param resource the instance of the resource from which to load the data 
     * @return the loaded data
     */
    public Map<String, List<Map<String, Object>>> loadData(Resource resource) {
        Map<String, List<Map<String, Object>>> result = null;
        try {
            result = load(resource.getInputStream());
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
     * Load the XML data.
     * 
     * @param xmlFile inputStream representation of user provided XML file.
     * @return a Map of method name and the list of associated test data with that method name
     * @throws IOException if an IO Exception occurs
     */
    private Map<String, List<Map<String, Object>>> load(final InputStream xmlFile) throws IOException {
        Map<String, List<Map<String, Object>>> data = new HashMap<String, List<Map<String, Object>>>();
        JAXBContext context = getJAXBContext();
        try {
            if (context != null) {
                Unmarshaller unmarshaller = context.createUnmarshaller();
                InputTestData testData = (InputTestData) unmarshaller.unmarshal(xmlFile);
                convertFromInputTestData(testData, data);
            }
        } catch (JAXBException e) {
            LOG.error("JAXBException occured while trying to unmarshal the data.", e);
            throw new RuntimeException("JAXBException occured while trying to unmarshal the data.", e);
        }

        return data;

    }

    /**
     * Convert the data from {@link InputTestData} to a Map representation as understood by the EasyTest Framework
     * 
     * @param source an instance of {@link InputTestData}
     * @param destination an instance of {@link Map}
     */
    private void convertFromInputTestData(InputTestData source, Map<String, List<Map<String, Object>>> destination) {
        List<TestMethod> testMethods = source.getTestMethod();
        for (TestMethod method : testMethods) {
            List<Map<String, Object>> testMethodData = convertFromLIstOfTestRecords(method.getTestRecord());
            LOG.debug("Read record for method {} and the data read is {}", method.getName(),testMethodData);
            destination.put(method.getName(), testMethodData);

        }
    }
    /**
     * Convert the data from List of {@link TestRecord} to a List of map representation. The LIst of map represents the
     * list of test data for a single test method.
     * 
     * @param dataRecords an instance of List of {@link TestRecord}
     * @return an instance of {@link List} of Map
     */
    private List<Map<String, Object>> convertFromLIstOfTestRecords(List<TestRecord> dataRecords) {
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        if (dataRecords != null) {
            for (TestRecord record : dataRecords) {              
                Map<String, Object> singleTestData = convertFromListOfEntry(record.getInputData().getEntry());
                singleTestData.put(RECORD_POSITION, record.getId());
                result.add(singleTestData);
            }
        }
        return result;
    }

    /**
     * Returns a Map representation of a Single data set for a given method. This data is used to run the test method
     * once.
     * 
     * @param testEntry a list of {@link Entry} objects
     * @return a Map
     */
    Map<String, Object> convertFromListOfEntry(List<Entry> testEntry) {
        Map<String, Object> testData = new HashMap<String, Object>();
        if (testEntry != null) {
            for (Entry entry : testEntry) {
                testData.put(entry.getKey(), entry.getValue());
            }
        }
        return testData;
    }

    /**
     * Get the JAXBContext
     * 
     * @return an instance of {@link JAXBContext}
     */
    private JAXBContext getJAXBContext() {
        JAXBContext context = null;
        try {
            context = JAXBContext.newInstance(ObjectFactory.class);
        } catch (JAXBException e) {
            LOG.error("Error occured while creating JAXB COntext.", e);
            throw new RuntimeException("Error occurred while creating JAXB Context.", e);
        }
        return context;
    }

    /**
     * Write Data to the existing XML File.
     * @param resource to which the data needs to be written
     * @param methodNames the name of the methods to write data for
     * @param actualData the actual data that needs to be written to the file.
     */
    public void writeData(Resource resource, Map<String, List<Map<String, Object>>> actualData, String... methodNames) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document document = db.parse(resource.getInputStream());
            Binder<Node> binder = getJAXBContext().createBinder(Node.class);
            binder.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            InputTestData testData = (InputTestData) binder.unmarshal(document);
            if(methodNames == null || methodNames.length == 0){
                updateTestMethods(testData, null, actualData);
            }else{
                for(String methodName : methodNames){
                    updateTestMethods(testData, methodName, actualData);
                }
            }
            
            binder.updateXML(testData);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer t = tf.newTransformer();
            t.transform(new DOMSource(document), new StreamResult(resource.getOutputStream()));
        } catch (ParserConfigurationException e) {
            LOG.error("Ignoring the write operation as ParserConfigurationException occured while parsing the file : " + resource.getResourceName(), e);
        } catch (SAXException e) {
            LOG.error("Ignoring the write operation as SAXException occured while parsing the file : " + resource.getResourceName(), e);
        } catch (IOException e) {
            LOG.error("Ignoring the write operation as IOException occured while parsing the file : " + resource.getResourceName(), e);
        } catch (JAXBException e) {
            LOG.error("Ignoring the write operation as JAXBException occured while parsing the file : " + resource.getResourceName(), e);
        } catch (TransformerException e) {
            LOG.error("Ignoring the write operation as TransformerException occured while parsing the file : " + resource.getResourceName(), e);
        }

    }

    /**
     * This method is responsible for adding the {@link OutputData} element to the existing file.
     * This method determines the right position of the test record based on the id attribute 
     * {@link TestRecord#getId()} of the {@link TestRecord}.
     * 
     * @param inputTestData an Object representation of the XML data
     * @param actualData the data structure that contains the output data that needs to be written to the file. 
     * @param methodToWriteDataFor the method for which data needs to be written
     * The output data is identified by the key {@link Loader#ACTUAL_RESULT}
     */
    private void updateTestMethods(InputTestData inputTestData, String methodToWriteDataFor , Map<String, List<Map<String, Object>>> actualData) {
        Boolean isMethodNameAbsent = methodToWriteDataFor == null || methodToWriteDataFor.length() <= 0;
        for (String methodName : actualData.keySet()) {
            if(!isMethodNameAbsent && !methodName.equals(methodToWriteDataFor)){
                continue;
            }
            List<Map<String, Object>> testRecords = actualData.get(methodName);
            for (Map<String, Object> testRecord : testRecords) {
                Boolean outputDataAdded = false;
                if (testRecord.containsKey(ACTUAL_RESULT)) {
                    // The data needs to be written to the XML file.
                    // Find the right place to put the data.
                    for (TestMethod testMethod : inputTestData.getTestMethod()) {
                        List<TestRecord> originalTestRecords = testMethod.getTestRecord();
                        for (TestRecord originalTestRecord : originalTestRecords) {
                            if (originalTestRecord.getId().equals(testRecord.get(RECORD_POSITION))) {
                                OutputData outputData = new OutputData();
                                Entry outputEntry = new Entry();
                                outputEntry.setKey(ACTUAL_RESULT);
                                outputEntry.setValue(testRecord.get(ACTUAL_RESULT).toString());
                                outputData.getEntry().add(outputEntry);
                                originalTestRecord.setOutputData(outputData);
                                outputDataAdded = true;
                                break;
                            }
                        }
                        if (outputDataAdded) {
                            break;
                        }
                    }
                }
            }
        }

    }

    


}
