package org.easetech.easytest.util;

import java.util.Iterator;

import java.util.List;
import java.util.Map;

/**
 * Data Context Holder for the test data and the corresponding test method.
 * 
 * @author Anuj Kumar
 *
 */
public final class DataContext {
    
    /**
     * Private constructor
     */
    private DataContext(){
        //do nothing
    }
    
    
    
    /**
     * DataContext thread local variable that will hold the data for easy consumption by the test cases.
     * Look at {@link #setConvertedData(Map)} for details.
     */
    public static final ThreadLocal<Map<String, List<Map<String , Object>>>> convertedDataThreadLocal = new ThreadLocal<Map<String, List<Map<String , Object>>>>();
    
    /**
     * DataContext thread local variable that will hold the data for easy consumption by the test cases.
     * Look at {@link #setData(Map)} for details.
     */
    public static final ThreadLocal<Map<String, List<Map<String , Object>>>> dataContextThreadLocal = new ThreadLocal<Map<String, List<Map<String , Object>>>>();
    
    /**
     * Test Method Name Context thread local variable that will hold the name of the test method currently executing.
     */
    public static final ThreadLocal<String> nameContextThreadLocal = new ThreadLocal<String>();
    
    /**
     * Sets the Data.
     * The data in this set is of the form :
     * <br><br>org.easetech.easytest.example.TestExcelDataLoader:getExcelTestDataWithDouble=[{libraryId=0009, itemId=0008}]<br>
     * where :
     * <ul>
     * <li><B>org.easetech.easytest.example.TestExcelDataLoader</B> : is the name of the TestClass</li>
     * <li><B>getExcelTestDataWithDouble</B> : is the name of the Test Method to run along with the data that it will be run with</li>
     * <li><B>[{libraryId=0009, itemId=0008}] </B> : is the actual array of test data 
     * 
     * @param  data the test data in the form :<br><br>
     * Map&lt;MethodName , List&lt;Map&lt;ParamName , ParamValue&gt;&gt; <br><br>
     * If we take the above example, then it is constructed like this:<br>
     * <code><br>
     * <I>Map&lt;String,Object&gt; attributeData = new HashMap&lt;String,Object&gt;();<br>
     * attributeData.put("libraryId",0009);<br>
     * attributeData.put("itemId",0008);<br>
     * Map&lt;String,List&lt;Map&lt;String,Object&gt;&gt; actualData = new HashMap&lt;String,List&lt;Map&lt;String,Object&gt;&gt;();<br>
     * actualData.put("org.easetech.easytest.example.TestExcelDataLoader:getExcelTestDataWithDouble" , Collections.singletonList(attributeData));<br>
     * 
     * Note that this is different from {@link #setConvertedData(Map)} in the sense that the method name does not have the attributes at the end.
     * 
     */
    public static void setData(Map<String, List<Map<String , Object>>> data) {
        Map<String, List<Map<String , Object>>> testData = dataContextThreadLocal.get();
        if(testData == null || testData.isEmpty()){
            dataContextThreadLocal.set(data);
        }else{  
            for(String key : data.keySet()){  
                testData.put(key, data.get(key));
            }
            dataContextThreadLocal.set(testData);
        }
        
    }
    
    /**
     * Sets the converted Data.
     * The data in this set is of the form :
     * <br><br>org.easetech.easytest.example.TestExcelDataLoader:getExcelTestDataWithDouble{libraryId=0009, itemId=0008}=[{libraryId=0009, itemId=0008}]<br>
     * where :
     * <ul>
     * <li><B>org.easetech.easytest.example.TestExcelDataLoader</B> : is the name of the TestClass</li>
     * <li><B>getExcelTestDataWithDouble{libraryId=0009, itemId=0008}</B> : is the name of the Test Method to run along with the data that it will be run with</li>
     * <li><B>[{libraryId=0009, itemId=0008}] </B> : is the actual array of test data 
     * 
     * @param  data the test data in the form :<br><br>
     * Map&lt;MethodName , List&lt;Map&lt;ParamName , ParamValue&gt;&gt; <br><br>
     * If we take the above example, then it is constructed like this:<br>
     * <code><br>
     * <I>Map&lt;String,Object&gt; attributeData = new HashMap&lt;String,Object&gt;();<br>
     * attributeData.put("libraryId",0009);<br>
     * attributeData.put("itemId",0008);<br>
     * Map&lt;String,List&lt;Map&lt;String,Object&gt;&gt; actualData = new HashMap&lt;String,List&lt;Map&lt;String,Object&gt;&gt;();<br>
     * actualData.put("org.easetech.easytest.example.TestExcelDataLoader:getExcelTestDataWithDouble{libraryId=0009, itemId=0008}" , Collections.singletonList(attributeData));
     * 
     */
    public static void setConvertedData(Map<String, List<Map<String , Object>>> data) {
        Map<String, List<Map<String , Object>>> testData = convertedDataThreadLocal.get();
        if(testData == null || testData.isEmpty()){
            convertedDataThreadLocal.set(data);
        }else{ 
            boolean removedOldKeys = false;
            for(String key : data.keySet()){
                if(!removedOldKeys){
                    String newKeyMethod = key.substring(0 , key.indexOf("{"));
                    Iterator<Map.Entry<String,List<Map<String,Object>>>> testDataItr = testData.entrySet().iterator();
                    while(testDataItr.hasNext()){
                        Map.Entry<String,List<Map<String,Object>>> entry = testDataItr.next();
                        String oldKey = entry.getKey();
                        String oldKeyMethod = oldKey.substring(0 , oldKey.indexOf("{"));
                        if(oldKeyMethod.equals(newKeyMethod)){
                            testDataItr.remove();                        
                        }
                        removedOldKeys = true;
                    }
                }
                testData.put(key, data.get(key));
            }
            convertedDataThreadLocal.set(testData);
        }
        
    }

    /**
     * Returns the data.
     * Look at {@link #setConvertedData(Map)} for details of the content in the returned map.
     * 
     * @return The data
     */
    public static Map<String, List<Map<String , Object>>> getConvertedData() {
        return convertedDataThreadLocal.get();
    }
    /**
     * Returns the data
     * Look at {@link #setData(Map)} for details of the content in the returned map.
     * 
     * @return The data
     */
    public static Map<String, List<Map<String , Object>>> getData() {
        return dataContextThreadLocal.get();
    }

    /**
     * Clears the data
     */
    public static void clearData() {
        dataContextThreadLocal.remove();
    }
    
    /**
     * Clears the data
     */
    public static void clearConvertedData() {
    	convertedDataThreadLocal.remove();
    }
    
    public static String getMethodName(){
        return nameContextThreadLocal.get();
    }

    /**
     * Sets the data
     * 
     * @param  name to set
     */
    public static void setMethodName(String name) {
        nameContextThreadLocal.set(name);
    }
    
    /**
     * Clears the data
     */
    public static void clearNameData() {
        nameContextThreadLocal.remove();
    }

}
