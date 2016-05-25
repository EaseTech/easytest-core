package org.easetech.easytest.converter;

import org.easetech.easytest.internal.DateTimeFormat;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.easetech.easytest.internal.EasyParamSignature;
import org.junit.Assert;
import org.junit.experimental.theories.PotentialAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * An instance of {@link BaseConverter} that is responsible for converting the raw data in to a collection type instance
 * 
 * @author Anuj Kumar
 *
 */
public class CollectionConverter implements BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>> {
    
    /**
     * An instance of {@link EasyParamSignature} that contains information about the signature of the test method.
     */
    private final EasyParamSignature signature;

    /**
     * The name of the parameter for which the data is being converted
     */
    private final String paramName;
    
    /**
     * An instance of the collection to convert to
     */
    private final Collection collection;
    
    /**
     * Constant key for specifying Standard Object Collection converter identified by {@link StandardObjectCollectionConverter}
     */
    private static final String STND_OBJ_COLLECTION_CONVERTER = "stdObjCollectionConverter"; 
    
    /**
     * Constant key for specifying Property Editor Collection converter identified by {@link PropertyEditorCollectionConverter}
     */
    private static final String PROPERTY_EDITOR_COLLECTION_CONVERTER = "propertyEditorCollectionConverter"; 
    
    /**
     * Constant key for specifying User Defined Collection converter identified by {@link UserDefinedCollectionConverter}
     */
    private static final String USER_DEFINED_COLLECTION_CONVERTER = "userDefinedCollectionConverter"; 
    
    /**
     * Constant key for specifying Param Constructor Collection converter identified by {@link ParamConstructorConverter}
     */
    private static final String PARAM_CONSTRUCTOR_CONVERTER = "paramConstructorConverter";
    

    
    /**
     * A list of converters that the {@link CollectionConverter} delegates to
     */
    private final Map<String , BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>>> converters = new LinkedHashMap<String , BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>>>();
    
    /**
     * 
     * Construct a new CollectionConverter
     * @param signature An instance of {@link EasyParamSignature} that contains information about the signature of the test method.
     * @param paramName The name of the parameter for which the data is being converted
     * @param convertEmptyToNull Whether empty values should be converted to Null values or not
     * @param dateTimeFormat the user specified date time format to use
     */
    public CollectionConverter(EasyParamSignature signature , String paramName , Boolean convertEmptyToNull , DateTimeFormat dateTimeFormat) {
        this.signature = signature;
        this.paramName = paramName;
        Class<?> genericType = signature.getIsGenericParameter() ? signature.getGenericParameterArgType()
            : Object.class;
        collection = getCollectionInstance(signature.getParameterType(), genericType);
        converters.put(STND_OBJ_COLLECTION_CONVERTER , new StandardObjectCollectionConverter(collection, signature, paramName , convertEmptyToNull , dateTimeFormat));
        converters.put(PROPERTY_EDITOR_COLLECTION_CONVERTER, new PropertyEditorCollectionConverter(signature, paramName, collection));
        converters.put(USER_DEFINED_COLLECTION_CONVERTER, new UserDefinedCollectionConverter(signature, paramName, collection));
        converters.put(PARAM_CONSTRUCTOR_CONVERTER, new ParamConstructorConverter(genericType, paramName, collection , convertEmptyToNull , dateTimeFormat));
    }
    
    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(CollectionConverter.class);
    
    

    /**
     * Convert the raw data into a list of {@link PotentialAssignment} instance 
     * that EasyTest uses to provide the right set of test data to the test method
     * @param convertFrom the raw data to convert from 
     * @return the list of {@link PotentialAssignment} instances
     */
    @SuppressWarnings("unchecked")
    public List<PotentialAssignment> convert(List<Map<String, Object>> convertFrom) {
        List<PotentialAssignment> potentialAssignments = null;
        
        if (Collection.class.isAssignableFrom(signature.getParameterType())) {
            if (collection == null) {
                Assert.fail("Unable to identify the Collection with Class :" + signature.getParameterType());
            }
            potentialAssignments = new ArrayList<PotentialAssignment>();  
            
            if (!signature.getIsGenericParameter()) {
                
                LOG.debug("Collection is of Non generic type.Setting the same values as fetched from the test file.");
                for (Map<String, Object> object : convertFrom) {
                    String[] strValues = ((String) object.get(paramName)).split(COLON);
                    for (int i = 0; i < strValues.length; i++) {
                        collection.add(strValues[i]);
                    }
                    potentialAssignments.add(PotentialAssignment.forValue(EMPTY_STRING, collection));
                }
            } else {
                for(String key : converters.keySet()) {
                    potentialAssignments = converters.get(key).convert(convertFrom);
                    if(potentialAssignments != null) {
                        break;
                    }
                }
            }
        }
        return potentialAssignments;
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
            } else if (Deque.class.isAssignableFrom(parameterType)) {
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
        } catch (Exception e) {
            LOG.error(
                "Exception occured while trying to instantiate a Collection of Type : {} . Error is {}",
                parameterType, e);
            Assert.fail("Exception occured while trying to instantiate a Collection of Type : "
                + parameterType + " . The exception is :" + e.toString());
        }
        return null;
    }

}
