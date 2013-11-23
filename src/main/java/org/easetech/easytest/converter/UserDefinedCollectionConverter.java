package org.easetech.easytest.converter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.easetech.easytest.internal.EasyParamSignature;
import org.junit.experimental.theories.PotentialAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * An instance of {@link BaseConverter} that is responsible for converting the raw data in to a user defined type instance 
 * using any user defined converter
 * 
 * @author Anuj Kumar
 *
 */
public class UserDefinedCollectionConverter implements BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>> {
    
    
    /**
     * The collection instance in case of Collection type parameter
     */
    private final Collection collection;
    
    /**
     * an instance of {@link EasyParamSignature}
     */
    private final EasyParamSignature signature;
    
    /**
     * The name of the parameter that is being converted
     */
    private final String paramName; 
    
    
    /**
     * 
     * Construct a new UserDefinedCollectionConverter
     * @param signature an instance of {@link EasyParamSignature}
     * @param paramName The name of the parameter that is being converted
     * @param collection The collection instance in case of Collection type parameter
     */
    public UserDefinedCollectionConverter(EasyParamSignature signature, String paramName, Collection collection) {
        super();
        this.signature = signature;
        this.paramName = paramName;
        this.collection = collection;
    }

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(UserDefinedCollectionConverter.class);

    /**
     * Convert the raw data into a list of {@link PotentialAssignment} instance 
     * that EasyTest uses to provide the right set of test data to the test method
     * @param convertFrom the raw data to convert from 
     * @return the list of {@link PotentialAssignment} instances
     */
    @SuppressWarnings("unchecked")
    public List<PotentialAssignment> convert(List<Map<String, Object>> convertFrom) {
        
        List<PotentialAssignment> potentialAssignments = null;
        Class<?> genericType = signature.getIsGenericParameter() ? signature.getGenericParameterArgType()
            : Object.class;
        LOG.debug("Editor for class {}  not found. Trying to find converter.", genericType);
        Converter<?> converter = ConverterManager.findConverter(genericType);
        if (converter != null) {
            potentialAssignments = new ArrayList<PotentialAssignment>();
            LOG.debug("Converter for class {} found ", genericType);
            for (Map<String, Object> object : convertFrom) {
                Map<String, Object> tempMap = new HashMap<String, Object>();
                String values = (String) object.get(paramName);
                String[] splitValues = values.split(COLON);
                for (int i = 0; i < splitValues.length; i++) {
                    tempMap.put(paramName, splitValues[i]);
                    Object value = null;
                    if(converter instanceof ParamAwareConverter) {
                        value = ((ParamAwareConverter)converter).convert(tempMap, paramName);
                    } else {
                        value = converter.convert(tempMap);
                    }
                    collection.add(value);
                }
                potentialAssignments.add(PotentialAssignment.forValue(EMPTY_STRING, collection));
            }
        } 
        return potentialAssignments;
    }

}
