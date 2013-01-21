
package org.easetech.easytest.annotation;

import java.sql.Time;

import java.sql.Timestamp;

import java.sql.Date;

import java.lang.reflect.InvocationTargetException;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.easetech.easytest.converter.AbstractConverter;
import org.easetech.easytest.converter.Converter;
import org.easetech.easytest.converter.ConverterManager;
import org.easetech.easytest.internal.EasyParamSignature;
import org.easetech.easytest.util.DataContext;
import org.easetech.easytest.util.GeneralUtil;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.experimental.theories.ParameterSignature;
import org.junit.experimental.theories.ParameterSupplier;
import org.junit.experimental.theories.PotentialAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A parameter level optional annotation that converts the data for EasyTest based test methods to consume. This
 * Annotation gives the ability to pass input parameters to the test method. EasyTest will automatically call the test
 * method as many times as there are number of data sets to be run against that particular method. For example, if the
 * user has specified 5 data set for a single test method, EasyTest will call the method five times, each time providing
 * the test data that was provided by the user. <br>
 * The annotation is used in conjunction with {@link DataLoader} annotation. {@link DataLoader} annotation is used to
 * provide test data to the test cases. </br> <br>
 * The annotation is optional. In case the name of the input parameter provided in the data file(XML, Excel, CSV or
 * custom) is same as the name of the input parameter type then this annotation can be omitted. For eg:<br>
 * <code><B>
 * public void testWithStrongParameters(LibraryId id ,
 * (@)Param(name="itemid") ItemId
 * itemId) { .... } </B>
 * </code> <br>
 * In the above example we have not provided @Param annotation to the input parameter LibraryId. In this case the test
 * parameter name in the test file should be LibraryId. You have to take care that in scenario where the input
 * parameters are of the same type, the names should be different. Thus if you have two input parameters of type
 * LibraryId then you should provide atleast @Param annotation on one of the input parameters.
 * 
 * The annotation contains a single mandatory field :
 * 
 * <li><B> name</B> : the name of the parameter(Mandatory) as is present in the input test data file. <li>In case the
 * param annotation is not specified and the Parameter type is Map, {@link DataSupplier} simply provides the HashMap
 * instance that was created while loading the data. This {@link HashMap} represents a single set of test data for the
 * test method.</li> <li>In case the param name is specified along with the {link @Param} annotation, the framework will
 * look for the parameter with the specified name in the loaded test data. <br>
 * 
 * Moreover, the framework supports PropertyEditors support for strongly typed objects. If you have a custom object and
 * its property editor in the same package, the EasyTest framework will convert the String value to your specified
 * custom object by calling the right property editor and pass an instance of custom object to your test case. This
 * provides the users facility to write test cases such as this : <br>
 * <code>
 * 
 * @Test
 * @DataLoader(filePaths ={ "getItemsData.csv" }) <br>public void testWithStrongParameters(LibraryId id
 *                       ,@Param(name="itemid") ItemId itemId) { .... } </code> <br>
 * <br>
 *                       <li>Example of using Map to get the entire data:</li></br> <br>
 *                       <code><br>
 * @Test (@)DataLoader(filePaths= {"getItemsData.csv" })<br> public void testGetItemsWithoutFileType( Map<String,
 *       Object> inputData) {<br> ........
 * 
 *       }</code>
 * 
 * 
 * <br>
 * <br>
 *       The EasyTest framework also supports custom {@link Converter}s. Converters are a mechanism for the user to
 *       translate a map of key/value pair into custom objects that can be passed as input parameters to the test cases.
 *       For example, if you want to pass a complex object as input parameter to a test case then you will simply write
 *       a custom converter that extends {@link AbstractConverter} and will override the
 *       {@link AbstractConverter#convert(Map)} method. You can look at example of CustomConverter here :
 *       https://github.
 *       com/EaseTech/easytest-core/blob/master/src/test/java/org/easetech/easytest/example/ItemConverter.java
 * 
 * <br>
 *       If you want to pass a Collection type, then EasyTest framework provides the functionality to instantiate the
 *       Collection class for you and pass in the right generic parameter if possible. For eg. if you have a test method
 *       like this :<br>
 * <br>
 *       <code>
 * 
 *  (At)Test<br>
 *   public void testArrayList(@Param(name="items") ArrayList&lt;ItemId> items){<br>
 *       Assert.assertNotNull(items);<br>
 *      for(ItemId item : items){<br>
 *          System.out.println("testArrayList : "+item);<br>
 *      &nbsp;&nbsp;}<br>
 *  }<br>
 * 
 * then all you have to do is :
 * <li> pass the list of itemIds as <B>":"</B> separated list in the test data file(XML, CSV,Excel or custom), for eg: 23:56:908:666</li><br>
 * <li> and register an editor or converter for converting the String data to object.<br>
 * In case the generic type argument to the Collection is a standard Java type(Date, Character, Timestamp, Long, Interger, Float, Double etc) 
 * then you don't have to do anything and the framework will take care of converting the String data to the requested type. 
 * <br><br>
 * Finally, even though the EasyTest framework is compiled with JDK 1.5, it does not stop you from using Java 6 Collection Types in case you are running your code on JRE 6.
 * For using Java 6 Collection type(like Deque , LinkedBlockingDeque etc) in your test cases, all you have to do is register an empty implementation of {@link AbstractConverter} in the {@link BeforeClass} method.
 * Note that you should pass only the Concrete type as parameter argument while extending the {@link AbstractConverter} and not the Abstract type or the interface type.
 * You can always override the default implementation of creating a specific type instance for use in your test cases by overriding the {@link AbstractConverter#instanceOfType()} method.
 * 
 * 
 * @author Anuj Kumar
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.PARAMETER })
public @interface Param {

    /** The name of the parameter for which value needs to be fetched from the data set */
    String name();

    /**
     * Static class that overrides the getValueSources method of {@link ParameterSupplier} to return the data in Junit
     * Format which is a list of {@link PotentialAssignment}. This is the place where we can specify what the data type
     * of the returned data would be. We can also specify different return types for different test methods.
     * 
     */
    static class DataSupplier {

        /**
         * Logger
         */
        protected final static Logger LOG = LoggerFactory.getLogger(DataSupplier.class);

        private static final String COLON = ":";

        private static final String EMPTY_STRING = "";

        /**
         * Method to return the list of data for the given Test method
         * 
         * @param signature the {@link ParameterSignature}
         * @return the list of {@link PotentialAssignment}
         */

        public List<PotentialAssignment> getValueSources(EasyParamSignature signature) {
            Param provider = signature.getAnnotation(Param.class);
            String value = DataContext.getMethodName();
            if (value == null) {
                Assert
                    .fail("The framework could not locate the test data for the test method. If you are using TestData annotation, make sure you specify the test method name in the data file. "
                        + "In case you are using ParametersSuppliedBy annotation, make sure you are using the right ParameterSupplier subclass.");
            }
            List<PotentialAssignment> listOfData = null;
            Map<String, List<Map<String, Object>>> data = DataContext.getConvertedData();
            List<Map<String, Object>> methodData = data.get(value);
            if (methodData == null) {
                Assert
                    .fail("Data does not exist for the specified method with name :"
                        + value
                        + " .Please check "
                        + "that the Data file contains the data for the given method name. A possible cause could be spelling mismatch.");
            }
            Class<?> parameterType = signature.getParameterType();
            if (Map.class.isAssignableFrom(parameterType)) {
                listOfData = convert(data.get(value), parameterType);
            } else if (Collection.class.isAssignableFrom(parameterType)) {
                listOfData = convertCollection(signature, provider != null ? provider.name() : null, data.get(value),
                    parameterType);
            } else {
                listOfData = convert(signature.getParameterType(), provider != null ? provider.name() : null,
                    data.get(value));
            }
            return listOfData;
        }

        /**
         * Method that returns a list of {@link PotentialAssignment} that contains map value. This is the map of values
         * that the user can use to fetch the values it requires on its own.
         * 
         * @param convertFrom the data to convert from
         * @param mapType The type of map
         * @return a list of {@link PotentialAssignment} that contains map value
         */
        @SuppressWarnings("unchecked")
        private List<PotentialAssignment> convert(List<Map<String, Object>> convertFrom, Class mapType) {
            List<PotentialAssignment> finalData = new ArrayList<PotentialAssignment>();
            for (Map<String, Object> map : convertFrom) {
                if (mapType.isInterface()) {
                    finalData.add(PotentialAssignment.forValue(EMPTY_STRING, map));
                } else {
                    Map dataValues;
                    try {
                        dataValues = (Map) mapType.newInstance();
                    } catch (InstantiationException e) {
                        LOG.error(
                            "InstantiationException occured while trying to convert the data to Map(using newInstance() method). "
                                + "The type of Map passed as input parameter is :" + mapType, e);
                        throw new RuntimeException(e);
                    } catch (IllegalAccessException e) {
                        LOG.error(
                            "IllegalAccessException occured while trying to convert the data to Map(using newInstance() method). "
                                + "The type of Map passed as input parameter is :" + mapType, e);
                        throw new RuntimeException(e);
                    }
                    dataValues.putAll(map);
                    finalData.add(PotentialAssignment.forValue(EMPTY_STRING, dataValues));
                }

            }
            return finalData;
        }

        /**
         * Method that returns a list of {@link PotentialAssignment} that contains the value as specified by idClass
         * parameter.
         * 
         * @param idClass the class object that dictates the type of data that will be present in the list of
         *            {@link PotentialAssignment}
         * @param paramName the optional name of the parameter with which to search for the data.
         * @param convertFrom the list of raw data read from the CSV file.
         * @return list of {@link PotentialAssignment}
         */
        private List<PotentialAssignment> convert(Class<?> idClass, String paramName,
            List<Map<String, Object>> convertFrom) {
            List<PotentialAssignment> finalData = new ArrayList<PotentialAssignment>();

            if (GeneralUtil.isStandardObjectInstance(idClass)) {
                for (Map<String, Object> object : convertFrom) {
                    finalData.add(PotentialAssignment.forValue(EMPTY_STRING,
                        GeneralUtil.convertToTargetType(idClass, object.get(paramName))));
                }
            } else {
                PropertyEditor editor = PropertyEditorManager.findEditor(idClass);
                if (editor != null) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Editor for class " + idClass + " found.");
                    }
                    for (Map<String, Object> object : convertFrom) {
                        if (paramName != null && !EMPTY_STRING.equals(paramName)) {
                            if (getStringValue(paramName, object) != null) {
                                editor.setAsText(getStringValue(paramName, object));
                            }

                        } else {
                            editor.setAsText(getStringValue(idClass.getSimpleName(), object));
                        }
                        // add data to PotentialAssignment even if it is null
                        finalData.add(PotentialAssignment.forValue(EMPTY_STRING, editor.getValue()));

                    }

                } else {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Editor for class " + idClass + " not found. Trying to find converter.");
                    }
                    // Try to find the Converter
                    Converter<?> converter = ConverterManager.findConverter(idClass);
                    if (converter != null) {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Converter for class " + idClass + "  found. ");
                        }
                        for (Map<String, Object> object : convertFrom) {
                            finalData.add(PotentialAssignment.forValue(EMPTY_STRING, converter.convert(object)));
                        }
                    } else {
                        if (LOG.isDebugEnabled()) {
                            LOG.debug("Converter for class " + idClass
                                + "  not found. Final try to resolve the object.");
                        }
                        try {
                            GeneralUtil.fillDataUsingConstructor(idClass, convertFrom, finalData, paramName);
                        } catch (IllegalArgumentException e) {
                            throw new RuntimeException(e);
                        } catch (InstantiationException e) {
                            throw new RuntimeException(e);
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException(e);
                        } catch (InvocationTargetException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            }

            return finalData;
        }


        /**
         * Method that returns a list of {@link PotentialAssignment} that contains the value as specified by idClass
         * parameter.
         * 
         * @param signature an instance of {@link EasyParamSignature} that dictates the type of data that will be
         *            present in the list of {@link PotentialAssignment}
         * @param paramName the optional name of the parameter with which to search for the data.
         * @param convertFrom the list of raw data read from the CSV file.
         * @param parameterType The Class of the parameter type
         * @return list of {@link PotentialAssignment}
         */

        @SuppressWarnings("unchecked")
        private List<PotentialAssignment> convertCollection(EasyParamSignature signature, String paramName,
            List<Map<String, Object>> convertFrom, Class parameterType) {
            Class<?> genericType = signature.getIsGenericParameter() ? signature.getGenericParameterArgType()
                : Object.class;
            Collection objectValues = getCollectionInstance(parameterType, genericType);
            List<PotentialAssignment> finalData = new ArrayList<PotentialAssignment>();

            if (!signature.getIsGenericParameter()) {
                LOG.debug("Collection is of Non generic type.Setting the same values as fetched from the test file.");
                for (Map<String, Object> object : convertFrom) {
                    String[] strValues = ((String) object.get(paramName)).split(COLON);
                    for (int i = 0; i < strValues.length; i++) {
                        objectValues.add(strValues[i]);
                    }
                    finalData.add(PotentialAssignment.forValue(EMPTY_STRING, objectValues));
                }
            } else if (GeneralUtil.isStandardObjectInstance(genericType)) {
                LOG.debug(
                    "parameter to the collection is a Standard Java Class {} . Using Internal Editors to resolve values",
                    genericType);
                if (objectValues == null) {
                    Assert.fail("Unable to identify the Collection with Class :" + parameterType);
                }

                for (Map<String, Object> object : convertFrom) {
                    String[] strValues = ((String) object.get(paramName)).split(COLON);
                    for (int i = 0; i < strValues.length; i++) {
                        objectValues.add(GeneralUtil.convertToTargetType(genericType, strValues[i]));
                    }
                    finalData.add(PotentialAssignment.forValue(EMPTY_STRING, objectValues));
                }
            } else {
                PropertyEditor editor = PropertyEditorManager.findEditor(genericType);
                if (editor != null) {
                    LOG.debug("Editor for class {} found", genericType);
                    for (Map<String, Object> object : convertFrom) {
                        String strValue = null;
                        if (paramName != null && !EMPTY_STRING.equals(paramName)) {
                            strValue = getStringValue(paramName, object);
                        } else {
                            strValue = getStringValue(genericType.getSimpleName(), object);
                        }
                        if (strValue != null) {
                            String[] values = strValue.split(COLON);
                            for (int i = 0; i < values.length; i++) {
                                editor.setAsText(values[i]);
                                if (editor.getValue() != null) {
                                    objectValues.add(editor.getValue());
                                }
                            }
                        }
                        finalData.add(PotentialAssignment.forValue(EMPTY_STRING, objectValues));
                    }

                } else {
                    LOG.debug("Editor for class {}  not found. Trying to find converter.", genericType);
                    Converter<?> converter = ConverterManager.findConverter(genericType);
                    if (converter != null) {
                        LOG.debug("Converter for class {} found ", genericType);
                        for (Map<String, Object> object : convertFrom) {
                            Map<String, Object> tempMap = new HashMap<String, Object>();
                            String values = (String) object.get(paramName);
                            String[] splitValues = values.split(COLON);
                            for (int i = 0; i < splitValues.length; i++) {
                                tempMap.put(paramName, splitValues[i]);
                                objectValues.add(converter.convert(tempMap));
                            }
                            finalData.add(PotentialAssignment.forValue(EMPTY_STRING, objectValues));
                        }
                    } else {
                        try {
                            GeneralUtil.fillDataUsingConstructor(genericType, convertFrom, finalData, paramName, objectValues);
                        } catch (IllegalArgumentException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (InstantiationException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (IllegalAccessException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }

                }
            }

            return finalData;

        }

        /**
         * Method that is responsible for returning the right instance of the Collection based on user's input. If the
         * collection type is abstract or if the collection type is an interface a default collection type is returned.
         * 
         * @param parameterType the Class object representing the Collection Type
         * @param genericType the optional generic type for the Collection
         * @return an instance of {@link Collection}
         */
        @SuppressWarnings("unchecked")
        private static Collection getCollectionInstance(Class parameterType, Class genericType) {
            try {
                if (Set.class.isAssignableFrom(parameterType)) {
                    if (EnumSet.class.isAssignableFrom(parameterType)) {
                        LOG.debug("Returning an instance of {} " + " for the input parameter of Type :{}",
                            EnumSet.class.getSimpleName(), parameterType);
                        return EnumSet.noneOf(genericType == null ? Object.class : genericType);
                    }

                    return (Collection) (parameterType.isInterface()
                        || Modifier.isAbstract(parameterType.getModifiers()) ? new TreeSet() : parameterType
                        .newInstance());
                } else if (List.class.isAssignableFrom(parameterType)) {
                    return (Collection) (parameterType.isInterface()
                        || Modifier.isAbstract(parameterType.getModifiers()) ? new LinkedList() : parameterType
                        .newInstance());
                } else if ("Deque".equals(parameterType.getSimpleName())
                    || "LinkedBlockingDeque".equals(parameterType.getSimpleName())
                    || "BlockingDeque".equals(parameterType.getSimpleName())) {
                    // Try to find an instance of the Class from the ConverterManager
                    Converter converter = ConverterManager.findConverter(parameterType);
                    if (converter == null) {
                        Assert
                            .fail("EasyTest does not natively support the Collection of type "
                                + parameterType
                                + " . In order to use this Collection type as parameter, provide an empty implementation of AbstractConveter "
                                + "class with the generic type as " + parameterType
                                + "or provide an implementation of instance() method of the Converter interface ");
                    } else {
                        return (Collection) converter.instanceOfType();
                    }
                } else if (Queue.class.isAssignableFrom(parameterType)) {
                    if (ArrayBlockingQueue.class.isAssignableFrom(parameterType)) {
                        return new ArrayBlockingQueue(100);
                    }
                    return (Collection) (parameterType.isInterface()
                        || Modifier.isAbstract(parameterType.getModifiers()) ? new LinkedBlockingQueue()
                        : parameterType.newInstance());
                } else if (Collection.class.isAssignableFrom(parameterType)) {
                    return new ArrayList();
                }
            } catch (InstantiationException e) {
                LOG.error(
                    "InstantiationException occured while trying to instantiate a Collection of Type : {} . Error is {}",
                    parameterType, e);
                Assert.fail("InstantiationException occured while trying to instantiate a Collection of Type : "
                    + parameterType + " . The exception is :" + e.getMessage());
            } catch (IllegalAccessException e) {
                LOG.error(
                    "IllegalAccessException occured while trying to instantiate a Collection of Type : {} . Error is {}",
                    parameterType, e);
                Assert.fail("IllegalAccessException occured while trying to instantiate a Collection of Type : "
                    + parameterType + " . The exception is :" + e.getMessage());
            } catch (IllegalArgumentException e) {
                LOG.error(
                    "IllegalArgumentException occured while trying to instantiate a Collection of Type : {} . Error is {}",
                    parameterType, e);
                Assert.fail("IllegalArgumentException occured while trying to instantiate a Collection of Type : "
                    + parameterType + " . The exception is :" + e.getMessage());
            } catch (SecurityException e) {
                LOG.error(
                    "SecurityException occured while trying to instantiate a Collection of Type : {} . Error is {}",
                    parameterType, e);
                Assert.fail("SecurityException occured while trying to instantiate a Collection of Type : "
                    + parameterType + " . The exception is :" + e.getMessage());
            }
            return null;
        }

        /**
         * Util method to get the String value
         * 
         * @param paramName the name of the parameter to get the String value for
         * @param data the data that contains the include Holdings value
         * @return String value or null if it is not set in the data.
         */
        private static String getStringValue(String paramName, Map<String, Object> data) {
            return (data.get(paramName) != null && !data.get(paramName).equals("null") ? data.get(paramName).toString()
                : null);

        }
    }
}
