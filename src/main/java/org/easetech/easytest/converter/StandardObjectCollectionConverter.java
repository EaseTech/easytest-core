package org.easetech.easytest.converter;

import java.util.ArrayList;

import org.easetech.easytest.internal.EasyParamSignature;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.easetech.easytest.util.GeneralUtil;
import org.junit.experimental.theories.PotentialAssignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * An instance of {@link BaseConverter} that is responsible for converting the raw data in to a user defined type instance 
 * using any jav standard object
 * 
 * @author Anuj Kumar
 *
 */
public class StandardObjectCollectionConverter implements BaseConverter<List<Map<String, Object>>, List<PotentialAssignment>> {
    
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
     * Whether empty values should be converted to Null values or not
     */
    private final Boolean convertEmptyToNull;
    
    /**
     * 
     * Construct a new StandardObjectCollectionConverter
     * @param collection The collection instance in case of Collection type parameter
     * @param signature an instance of {@link EasyParamSignature}
     * @param paramName The name of the parameter that is being converted
     */
    public StandardObjectCollectionConverter(Collection collection, EasyParamSignature signature, String paramName , Boolean convertEmptyToNull) {
        super();
        this.collection = collection;
        this.signature = signature;
        this.paramName = paramName;
        this.convertEmptyToNull = convertEmptyToNull;
    }

    /**
     * Logger
     */
    private static final Logger LOG = LoggerFactory.getLogger(StandardObjectCollectionConverter.class);

    /**
     * Convert the raw data into a list of {@link PotentialAssignment} instance 
     * that EasyTest uses to provide the right set of test data to the test method
     * @param convertFrom the raw data to convert from 
     * @return the list of {@link PotentialAssignment} instances
     */
    @SuppressWarnings({ "unchecked" })
    public List<PotentialAssignment> convert(List<Map<String, Object>> convertFrom) {
        List<PotentialAssignment> potentialAssignments = null;
        Class<?> genericType = signature.getIsGenericParameter() ? signature.getGenericParameterArgType()
            : Object.class;
        if (GeneralUtil.isStandardObjectInstance(genericType)) {
            potentialAssignments = new ArrayList<PotentialAssignment>();
            LOG.debug(
                "parameter to the collection is a Standard Java Class {} . Using Internal Editors to resolve values",
                genericType);
            
            for (Map<String, Object> object : convertFrom) {
                String[] strValues = ((String) object.get(paramName)).split(COLON);
                for (int i = 0; i < strValues.length; i++) {
                    collection.add(GeneralUtil.convertToTargetType(genericType, strValues[i], convertEmptyToNull));
                }
                potentialAssignments.add(PotentialAssignment.forValue(EMPTY_STRING, collection));
            }
        }
        return potentialAssignments;
        
    }

}
